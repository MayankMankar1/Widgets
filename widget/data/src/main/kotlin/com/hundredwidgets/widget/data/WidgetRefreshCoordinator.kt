package com.hundredwidgets.widget.data

import com.hundredwidgets.core.data.SettingsRepository
import com.hundredwidgets.core.data.WidgetInstanceRepository
import com.hundredwidgets.core.model.WidgetInstance
import com.hundredwidgets.widget.designs.WidgetRegistry
import com.hundredwidgets.widget.engine.WidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Started from Application.onCreate, which also runs when a widget receiver wakes the process.
 * Keeps periodic work scheduled only while a placed widget needs it.
 */
@Singleton
class WidgetRefreshCoordinator @Inject constructor(
    private val instances: WidgetInstanceRepository,
    private val settings: SettingsRepository,
    private val scheduler: RefreshScheduler,
    private val updater: WidgetUpdater,
) {

    fun start(scope: CoroutineScope) {
        scope.launch { updater.reconcile() }

        scope.launch {
            combine(
                instances.instances.map(::needsPeriodicRefresh).distinctUntilChanged(),
                settings.refreshIntervalMinutes,
            ) { needed, minutes -> needed to minutes }
                .distinctUntilChanged()
                .collect { (needed, minutes) ->
                    if (needed) scheduler.schedulePeriodic(minutes) else scheduler.cancelPeriodic()
                }
        }

        // A newly placed widget shouldn't wait a whole interval for its first data. The first
        // emission is skipped: it fires on every process start, including worker wake-ups.
        scope.launch {
            instances.instances
                .map { list -> list.map { it.appWidgetId }.toSet() to needsPeriodicRefresh(list) }
                .distinctUntilChanged()
                .drop(1)
                .collect { (_, needed) -> if (needed) scheduler.refreshNow() }
        }

        scope.launch {
            settings.clockFormat.drop(1).collect { updater.updateAll() }
        }
    }

    private fun needsPeriodicRefresh(list: List<WidgetInstance>): Boolean =
        list.any { instance ->
            WidgetRegistry.find(instance.designId)?.needs?.any { it.refreshedPeriodically } == true
        }
}
