package com.hundredwidgets.widget.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hundredwidgets.widget.engine.WidgetUpdater
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/** Runs every collector, then redraws only the widgets whose sources changed. */
class RefreshWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val deps = EntryPointAccessors.fromApplication(applicationContext, WidgetDataEntryPoint::class.java)
        val updater = deps.widgetUpdater()
        deps.collectors().forEach { collector ->
            val changed = runCatching { collector.collect() }.getOrDefault(false)
            if (changed) updater.refresh(collector.source)
        }
        return Result.success()
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetDataEntryPoint {
    fun collectors(): Set<@JvmSuppressWildcards Collector>
    fun widgetUpdater(): WidgetUpdater
}
