package com.hundredwidgets.app.ui.components

import android.widget.FrameLayout
import android.widget.RemoteViews
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.ClockFormat
import com.hundredwidgets.core.model.DeviceSnapshot
import com.hundredwidgets.core.model.InstanceConfig
import com.hundredwidgets.widget.designs.WidgetDesign
import com.hundredwidgets.widget.designs.WidgetRenderContext

private const val CELL_DP = 80
private const val GAP_DP = 16

/** Size of a design on the canvas's 4-column grid, used for in-app previews. */
fun CellSize.previewSize(): DpSize = DpSize(
    width = (columns * CELL_DP + (columns - 1) * GAP_DP).dp,
    height = (rows * CELL_DP + (rows - 1) * GAP_DP).dp,
)

/**
 * Renders the real widget: the design's Glance code is composed into RemoteViews and
 * inflated here, so the catalogue shows exactly what lands on the home screen.
 */
@OptIn(ExperimentalGlanceRemoteViewsApi::class)
@Composable
fun WidgetPreview(
    design: WidgetDesign,
    clockFormat: ClockFormat,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
) {
    val context = LocalContext.current
    val size = design.size.previewSize()
    var views by remember(design.id, clockFormat) { mutableStateOf<RemoteViews?>(null) }

    LaunchedEffect(design.id, clockFormat) {
        views = runCatching {
            GlanceRemoteViews().compose(context, size) {
                design.content(
                    WidgetRenderContext(
                        config = InstanceConfig(),
                        clockFormat = clockFormat,
                        snapshot = DeviceSnapshot(),
                        isPreview = true,
                    ),
                )
            }.remoteViews
        }.getOrNull()
    }

    Box(
        modifier = modifier.size(size.width * scale, size.height * scale),
        contentAlignment = Alignment.Center,
    ) {
        val current = views
        if (current == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp)),
            )
        } else {
            AndroidView(
                factory = { FrameLayout(it) },
                update = { frame ->
                    frame.removeAllViews()
                    frame.addView(current.apply(frame.context, frame))
                },
                modifier = Modifier
                    .requiredSize(size.width, size.height)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
            )
        }
    }
}
