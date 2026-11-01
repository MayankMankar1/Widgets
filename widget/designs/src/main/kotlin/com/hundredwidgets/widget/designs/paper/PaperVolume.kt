package com.hundredwidgets.widget.designs.paper

import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
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
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.Layer

/** Media volume steps the pill cycles through, as a percent of the maximum. */
private val Levels = intArrayOf(35, 55, 75, 90)
private val LevelArt = intArrayOf(
    R.drawable.wd_paper_volume_35,
    R.drawable.wd_paper_volume_55,
    R.drawable.wd_paper_volume_75,
    R.drawable.wd_paper_volume_90,
)

/**
 * Canvas P1 "Volume slider": a pill that fills from the top. Adapt: widgets can't be dragged,
 * so a tap steps media volume to the next level; the pill shows the last level set here.
 */
@Composable
internal fun PaperVolume(ctx: WidgetRenderContext) {
    val context = LocalContext.current
    var step by remember { mutableIntStateOf(1) }
    val tap = if (ctx.isPreview) {
        GlanceModifier
    } else {
        GlanceModifier.clickable {
            step = (step + 1) % Levels.size
            setMediaVolume(context, Levels[step])
        }
    }
    Box(modifier = GlanceModifier.fillMaxSize().then(tap)) {
        Image(
            provider = ImageProvider(LevelArt[step]),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = GlanceModifier.fillMaxSize(),
        )
        Layer(Alignment.TopCenter, GlanceModifier.padding(top = 24.dp)) {
            Image(
                provider = ImageProvider(R.drawable.wd_ic_music),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ColorProvider(PaperColors.black)),
                modifier = GlanceModifier.size(26.dp),
            )
        }
    }
}

private fun setMediaVolume(context: Context, percent: Int) {
    val audio = context.getSystemService(AudioManager::class.java) ?: return
    val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    audio.setStreamVolume(AudioManager.STREAM_MUSIC, (max * percent + 50) / 100, 0)
}
