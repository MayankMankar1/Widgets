package com.hundredwidgets.widget.engine

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.hundredwidgets.core.model.CellSize
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * One receiver per launcher size, each listed once in the system widget picker.
 * Glance already calls goAsync() in these callbacks, so cleanup runs in [cleanupScope];
 * the startup reconcile in WidgetUpdater catches anything a killed process missed.
 */
abstract class HundredWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = HundredWidget()

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val instances = entryPoint(context).widgetInstances()
        cleanupScope.launch { instances.delete(appWidgetIds.toList()) }
    }

    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        val instances = entryPoint(context).widgetInstances()
        cleanupScope.launch { instances.remap(oldWidgetIds, newWidgetIds) }
    }

    private fun entryPoint(context: Context): WidgetEngineEntryPoint =
        EntryPointAccessors.fromApplication(context, WidgetEngineEntryPoint::class.java)

    private companion object {
        val cleanupScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}

class Widget1x1Receiver : HundredWidgetReceiver()
class Widget1x2Receiver : HundredWidgetReceiver()
class Widget2x1Receiver : HundredWidgetReceiver()
class Widget2x2Receiver : HundredWidgetReceiver()
class Widget4x1Receiver : HundredWidgetReceiver()
class Widget4x2Receiver : HundredWidgetReceiver()

object WidgetReceivers {

    fun forSize(size: CellSize): Class<out HundredWidgetReceiver> = when (size) {
        CellSize.S1x1 -> Widget1x1Receiver::class.java
        CellSize.S1x2 -> Widget1x2Receiver::class.java
        CellSize.S2x1 -> Widget2x1Receiver::class.java
        CellSize.S2x2 -> Widget2x2Receiver::class.java
        CellSize.S4x1 -> Widget4x1Receiver::class.java
        CellSize.S4x2 -> Widget4x2Receiver::class.java
    }

    /** The size a placed widget has, from its provider's class name. */
    fun sizeOf(providerClassName: String): CellSize? =
        CellSize.entries.firstOrNull { forSize(it).name == providerClassName }
}
