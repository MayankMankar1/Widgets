package com.hundredwidgets.widget.engine

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hundredwidgets.core.model.ClockFormat
import com.hundredwidgets.core.model.DeviceSnapshot
import com.hundredwidgets.core.model.WidgetInstance
import com.hundredwidgets.widget.designs.WidgetRegistry
import com.hundredwidgets.widget.designs.WidgetRenderContext
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

/**
 * The single GlanceAppWidget behind every size receiver. It looks up which design the
 * placed widget shows and renders it. Data is collected as Flows, so an open session
 * redraws on its own; [WidgetUpdater] starts a session when none is running.
 */
class HundredWidget : GlanceAppWidget(errorUiLayout = R.layout.we_widget_error) {

    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val deps = EntryPointAccessors.fromApplication(context, WidgetEngineEntryPoint::class.java)
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val instances = deps.widgetInstances()
        val snapshots = deps.deviceSnapshots()
        val settings = deps.settings()

        val initialInstance = instances.get(appWidgetId)
        val initialSnapshot = snapshots.current()
        val initialClockFormat = settings.clockFormat.first()

        provideContent {
            val instance by instances.observe(appWidgetId).collectAsState(initialInstance)
            val snapshot by snapshots.snapshot.collectAsState(initialSnapshot)
            val appClockFormat by settings.clockFormat.collectAsState(initialClockFormat)
            WidgetContent(appWidgetId, instance, snapshot, appClockFormat)
        }
    }
}

@Composable
private fun WidgetContent(
    appWidgetId: Int,
    instance: WidgetInstance?,
    snapshot: DeviceSnapshot,
    appClockFormat: ClockFormat,
) {
    val design = instance?.let { WidgetRegistry.find(it.designId) }
    if (instance == null || design == null) {
        UnconfiguredContent(appWidgetId)
        return
    }
    design.content(
        WidgetRenderContext(
            config = instance.config,
            clockFormat = instance.config.clockFormat ?: appClockFormat,
            snapshot = snapshot,
            isPreview = false,
        ),
    )
}

/** Shown until a design is chosen (for example, right after adding from the system picker). */
@Composable
private fun UnconfiguredContent(appWidgetId: Int) {
    val context = LocalContext.current
    val chooseDesign = Intent()
        .setClassName(context.packageName, EngineContract.CONFIGURE_ACTIVITY)
        .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.we_bg_unconfigured))
            .padding(12.dp)
            .clickable(actionStartActivity(chooseDesign)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = context.getString(R.string.we_tap_to_choose),
            style = TextStyle(
                color = ColorProvider(Color.White),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            ),
        )
    }
}
