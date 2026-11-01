package com.hundredwidgets.widget.designs.signal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.GlanceStyledText
import com.hundredwidgets.widget.designs.common.Layer
import com.hundredwidgets.widget.designs.common.WidgetFont

private val Arrows = intArrayOf(
    R.drawable.wd_signal_arrow_0, R.drawable.wd_signal_arrow_45, R.drawable.wd_signal_arrow_90,
    R.drawable.wd_signal_arrow_135, R.drawable.wd_signal_arrow_180, R.drawable.wd_signal_arrow_225,
    R.drawable.wd_signal_arrow_270, R.drawable.wd_signal_arrow_315,
)

/**
 * Canvas S2 "Compass": a dot-matrix arrow on a black disc. Adapt: no live compass in a
 * widget, so it is decorative; each tap turns the arrow 45°.
 */
@Composable
internal fun SignalCompass(ctx: WidgetRenderContext) {
    val context = LocalContext.current
    var heading by remember { mutableIntStateOf(0) }
    val tap = if (ctx.isPreview) GlanceModifier else GlanceModifier.clickable { heading = (heading + 1) % Arrows.size }
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_signal_circle_black))
            .then(tap),
    ) {
        Layer(Alignment.TopCenter, GlanceModifier.padding(top = 14.dp)) {
            GlanceStyledText(context.getString(R.string.wd_compass_n), WidgetFont.SPACE_MONO, 11f, SignalColors.paper)
        }
        Layer(Alignment.BottomCenter, GlanceModifier.padding(bottom = 14.dp)) {
            GlanceStyledText(context.getString(R.string.wd_compass_s), WidgetFont.SPACE_MONO, 11f, SignalColors.dim)
        }
        Layer(Alignment.CenterEnd, GlanceModifier.padding(end = 14.dp)) {
            GlanceStyledText(context.getString(R.string.wd_compass_e), WidgetFont.SPACE_MONO, 11f, SignalColors.dim)
        }
        Layer(Alignment.CenterStart, GlanceModifier.padding(start = 14.dp)) {
            GlanceStyledText(context.getString(R.string.wd_compass_w), WidgetFont.SPACE_MONO, 11f, SignalColors.dim)
        }
        Image(
            provider = ImageProvider(Arrows[heading]),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = GlanceModifier.fillMaxSize(),
        )
    }
}
