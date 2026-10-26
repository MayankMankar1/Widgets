package com.hundredwidgets.widget.engine

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import com.hundredwidgets.core.data.WidgetInstanceRepository
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DataSource
import com.hundredwidgets.widget.designs.WidgetRegistry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** The one way to redraw widgets: only those whose design needs the changed source. */
@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
    private val instances: WidgetInstanceRepository,
) {

    suspend fun refresh(source: DataSource) {
        instances.getAll()
            .filter { WidgetRegistry.find(it.designId)?.needs?.contains(source) == true }
            .forEach { update(it.appWidgetId) }
    }

    suspend fun update(appWidgetId: Int) {
        runCatching {
            val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            HundredWidget().update(context, glanceId)
        }
    }

    suspend fun updateAll() {
        HundredWidget().updateAll(context)
    }

    /** Drops rows for widgets the user removed while our process wasn't running. */
    suspend fun reconcile() {
        val manager = AppWidgetManager.getInstance(context)
        val placedIds = CellSize.entries.flatMap { size ->
            manager.getAppWidgetIds(ComponentName(context, WidgetReceivers.forSize(size))).toList()
        }
        instances.keepOnly(placedIds)
    }
}
