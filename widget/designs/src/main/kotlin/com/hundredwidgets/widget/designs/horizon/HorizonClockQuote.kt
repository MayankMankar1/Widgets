package com.hundredwidgets.widget.designs.horizon

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.WidgetFont
import com.hundredwidgets.widget.designs.common.GlanceTextClock
import com.hundredwidgets.widget.designs.common.Quotes

/** Canvas H1 "Clock and quote": date, large light time, quote of the day on glass. */
@Composable
internal fun HorizonClockQuote(ctx: WidgetRenderContext) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_horizon_glass))
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        GlanceTextClock(
            pattern12 = "EEE, d MMM",
            pattern24 = "EEE, d MMM",
            clockFormat = ctx.clockFormat,
            font = WidgetFont.INTER_MEDIUM,
            sizeSp = 14f,
            color = HorizonColors.textSoft,
        )
        Spacer(GlanceModifier.height(6.dp))
        GlanceTextClock(
            pattern12 = "hh:mm",
            pattern24 = "HH:mm",
            clockFormat = ctx.clockFormat,
            font = WidgetFont.INTER_LIGHT,
            sizeSp = 46f,
            color = HorizonColors.text,
        )
        Spacer(GlanceModifier.defaultWeight())
        Text(
            text = Quotes.ofTheDay(),
            style = TextStyle(color = ColorProvider(HorizonColors.textMuted), fontSize = 12.sp),
            maxLines = 3,
        )
    }
}
