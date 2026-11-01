package com.hundredwidgets.widget.designs.comic

import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
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

/** Canvas C1 "Speech bubble clock": the time in a bubble on a halftone panel, plus a date sticker. */
@Composable
internal fun ComicBubbleClock(ctx: WidgetRenderContext) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_comic_bg_bubble)),
    ) {
        Layer(Alignment.TopCenter, GlanceModifier.padding(top = 24.dp, end = 6.dp)) {
            GlanceTextClock(
                pattern12 = "h:mm",
                pattern24 = "HH:mm",
                clockFormat = ctx.clockFormat,
                font = WidgetFont.BANGERS,
                sizeSp = 44f,
                color = ComicColors.ink,
                gravity = Gravity.CENTER,
            )
        }
        Layer(Alignment.BottomEnd, GlanceModifier.padding(end = 14.dp, bottom = 14.dp)) {
            Box(
                modifier = GlanceModifier
                    .background(ImageProvider(R.drawable.wd_bg_comic_sticker))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                GlanceTextClock(
                    pattern12 = "EEE · MMM d",
                    pattern24 = "EEE · MMM d",
                    clockFormat = ctx.clockFormat,
                    font = WidgetFont.COMIC_NEUE_BOLD,
                    sizeSp = 11f,
                    color = ComicColors.cream,
                    caps = true,
                )
            }
        }
    }
}
