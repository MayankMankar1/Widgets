package com.hundredwidgets.widget.engine

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.widget.designs.WidgetDesign
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Asks the launcher to add a design to the home screen (README §10.7). */
class WidgetPinner @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val manager: AppWidgetManager get() = AppWidgetManager.getInstance(context)

    fun isSupported(): Boolean = manager.isRequestPinAppWidgetSupported

    /** Returns false when the launcher can't pin; show manual instructions then. */
    fun pin(design: WidgetDesign): Boolean {
        if (!isSupported()) return false
        val provider = ComponentName(context, WidgetReceivers.forSize(design.size))
        val callback = Intent(context, PinResultReceiver::class.java)
            .putExtra(EngineContract.EXTRA_DESIGN_ID, design.id.value)
            .putExtra(EngineContract.EXTRA_CELL_SIZE, design.size.name)
        // Must be mutable: the system adds EXTRA_APPWIDGET_ID before sending it.
        val mutable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        val pendingCallback = PendingIntent.getBroadcast(
            context,
            design.id.value.hashCode(),
            callback,
            PendingIntent.FLAG_UPDATE_CURRENT or mutable,
        )
        return manager.requestPinAppWidget(provider, null, pendingCallback)
    }
}

/** Receives the new widget's ID after the user confirms, and records its design. */
class PinResultReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val designId = intent.getStringExtra(EngineContract.EXTRA_DESIGN_ID)
        val size = intent.getStringExtra(EngineContract.EXTRA_CELL_SIZE)?.let { CellSize.fromName(it) }
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID || designId == null || size == null) return

        val deps = EntryPointAccessors.fromApplication(context, WidgetEngineEntryPoint::class.java)
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                deps.widgetInstances().save(appWidgetId, DesignId(designId), size)
                deps.widgetUpdater().update(appWidgetId)
            } finally {
                pending.finish()
            }
        }
    }
}
