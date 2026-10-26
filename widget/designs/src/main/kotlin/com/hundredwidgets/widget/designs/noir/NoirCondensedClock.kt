package com.hundredwidgets.widget.designs.noir

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.width
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.ClockFont
import com.hundredwidgets.widget.designs.common.GlanceTextClock

/**
 * Canvas N1 "Clock": grey hours, white minutes. The canvas stretches Archivo 1.72× tall;
 * widgets can't scale text, so this uses a condensed font (README §5, Adapt).
 */
@Composable
internal fun NoirCondensedClock(ctx: WidgetRenderContext) {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_noir_ink)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlanceTextClock(
            pattern12 = "hh",
            pattern24 = "HH",
            clockFormat = ctx.clockFormat,
            font = ClockFont.CONDENSED_BOLD,
            sizeSp = 72f,
            color = NoirColors.hour,
        )
        Spacer(GlanceModifier.width(4.dp))
        GlanceTextClock(
            pattern12 = "mm",
            pattern24 = "mm",
            clockFormat = ctx.clockFormat,
            font = ClockFont.CONDENSED_BOLD,
            sizeSp = 72f,
            color = NoirColors.text,
        )
    }
}
