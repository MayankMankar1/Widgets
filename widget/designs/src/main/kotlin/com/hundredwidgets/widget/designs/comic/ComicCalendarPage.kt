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

/**
 * Canvas C1 "Calendar page": torn page with a red header (pre-rendered), month, big day and
 * weekday. All three are TextClocks, so the page turns at midnight without an update.
 */
@Composable
internal fun ComicCalendarPage(ctx: WidgetRenderContext) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_comic_bg_calendar)),
    ) {
        Layer(Alignment.TopCenter, GlanceModifier.padding(top = 10.dp, end = 4.dp)) {
            GlanceTextClock(
                pattern12 = "MMMM",
                pattern24 = "MMMM",
                clockFormat = ctx.clockFormat,
                font = WidgetFont.BANGERS,
                sizeSp = 21f,
                color = ComicColors.cream,
                gravity = Gravity.CENTER,
                caps = true,
            )
        }
        Layer(Alignment.TopCenter, GlanceModifier.padding(top = 46.dp, end = 4.dp)) {
            GlanceTextClock(
                pattern12 = "d",
                pattern24 = "d",
                clockFormat = ctx.clockFormat,
                font = WidgetFont.BANGERS,
                sizeSp = 68f,
                color = ComicColors.ink,
                gravity = Gravity.CENTER,
            )
        }
        Layer(Alignment.BottomCenter, GlanceModifier.padding(bottom = 30.dp, end = 4.dp)) {
            GlanceTextClock(
                pattern12 = "EEEE",
                pattern24 = "EEEE",
                clockFormat = ctx.clockFormat,
                font = WidgetFont.COMIC_NEUE_BOLD,
                sizeSp = 12f,
                color = ComicColors.ink,
                gravity = Gravity.CENTER,
                caps = true,
            )
        }
    }
}
