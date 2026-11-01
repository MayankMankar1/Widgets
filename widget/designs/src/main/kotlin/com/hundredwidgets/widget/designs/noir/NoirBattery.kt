package com.hundredwidgets.widget.designs.noir

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.batteryOrSample

/** Canvas N2 "Battery": label, large percentage and a level bar on black. */
@Composable
internal fun NoirBattery(ctx: WidgetRenderContext) {
    val context = LocalContext.current
    val battery = ctx.batteryOrSample()
    val label = when {
        battery == null -> context.getString(R.string.wd_battery_label)
        battery.isCharging -> context.getString(R.string.wd_battery_charging)
        else -> context.getString(R.string.wd_battery_label)
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_noir_ink))
            .padding(16.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(color = ColorProvider(NoirColors.label), fontSize = 13.sp, fontWeight = FontWeight.Medium),
        )
        Spacer(GlanceModifier.defaultWeight())
        Text(
            text = battery?.let { "${it.level}%" } ?: "--%",
            style = TextStyle(color = ColorProvider(NoirColors.text), fontSize = 44.sp, fontWeight = FontWeight.Bold),
        )
        Spacer(GlanceModifier.height(10.dp))
        LinearProgressIndicator(
            progress = (battery?.level ?: 0) / 100f,
            modifier = GlanceModifier.fillMaxWidth().height(6.dp),
            color = ColorProvider(if (battery?.isCharging == true) NoirColors.charging else NoirColors.text),
            backgroundColor = ColorProvider(NoirColors.track),
        )
    }
}
