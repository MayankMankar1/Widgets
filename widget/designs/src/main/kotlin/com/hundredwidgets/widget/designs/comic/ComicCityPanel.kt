package com.hundredwidgets.widget.designs.comic

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.GlanceTextClock
import com.hundredwidgets.widget.designs.common.Layer
import com.hundredwidgets.widget.designs.common.WidgetFont

/**
 * Canvas C1 "City at night": the cm-city photo panel with a "Meanwhile, at 9:41 PM…" caption.
 * The caption is one TextClock whose pattern quotes the words, so it ticks with no updates.
 */
@Composable
internal fun ComicCityPanel(ctx: WidgetRenderContext) {
    val context = LocalContext.current
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_comic_bg_city)),
    ) {
        Layer(Alignment.TopStart, GlanceModifier.padding(start = 8.dp, top = 8.dp, end = 30.dp)) {
            Box(
                modifier = GlanceModifier
                    .background(ImageProvider(R.drawable.wd_bg_comic_caption))
                    .padding(horizontal = 7.dp, vertical = 4.dp),
            ) {
                GlanceTextClock(
                    pattern12 = context.getString(R.string.wd_comic_meanwhile_12),
                    pattern24 = context.getString(R.string.wd_comic_meanwhile_24),
                    clockFormat = ctx.clockFormat,
                    font = WidgetFont.COMIC_NEUE_BOLD,
                    sizeSp = 10.5f,
                    color = ComicColors.ink,
                    caps = true,
                )
            }
        }
    }
}
