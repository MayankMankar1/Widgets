package com.hundredwidgets.widget.designs.paper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.Layer

private val Needles = intArrayOf(
    R.drawable.wd_paper_needle_0, R.drawable.wd_paper_needle_45, R.drawable.wd_paper_needle_90,
    R.drawable.wd_paper_needle_135, R.drawable.wd_paper_needle_180, R.drawable.wd_paper_needle_225,
    R.drawable.wd_paper_needle_270, R.drawable.wd_paper_needle_315,
)

/**
 * Canvas P2 "Compass". Adapt: widgets can't read the compass sensor live, so it is a
 * decorative dial; each tap turns the needle 45°.
 */
@Composable
internal fun PaperCompass(ctx: WidgetRenderContext) {
    val context = LocalContext.current
    var heading by remember { mutableIntStateOf(0) }
    val tap = if (ctx.isPreview) GlanceModifier else GlanceModifier.clickable { heading = (heading + 1) % Needles.size }
    val letter = TextStyle(color = ColorProvider(PaperColors.white), fontSize = 11.sp, fontWeight = FontWeight.Medium)
    Box(modifier = GlanceModifier.fillMaxSize().then(tap)) {
        Image(
            provider = ImageProvider(R.drawable.wd_paper_compass_dial),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = GlanceModifier.fillMaxSize(),
        )
        Layer(Alignment.TopCenter, GlanceModifier.padding(top = 20.dp)) {
            Text(
                context.getString(R.string.wd_compass_n),
                style = TextStyle(color = ColorProvider(PaperColors.red), fontSize = 13.sp, fontWeight = FontWeight.Bold),
            )
        }
        Layer(Alignment.BottomCenter, GlanceModifier.padding(bottom = 20.dp)) {
            Text(context.getString(R.string.wd_compass_s), style = letter)
        }
        Layer(Alignment.CenterEnd, GlanceModifier.padding(end = 22.dp)) {
            Text(context.getString(R.string.wd_compass_e), style = letter)
        }
        Layer(Alignment.CenterStart, GlanceModifier.padding(start = 22.dp)) {
            Text(context.getString(R.string.wd_compass_w), style = letter)
        }
        Image(
            provider = ImageProvider(Needles[heading]),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = GlanceModifier.fillMaxSize(),
        )
    }
}
