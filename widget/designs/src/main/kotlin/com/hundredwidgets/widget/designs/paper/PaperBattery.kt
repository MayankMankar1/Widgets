package com.hundredwidgets.widget.designs.paper

import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.GlanceStyledText
import com.hundredwidgets.widget.designs.common.WidgetFont
import com.hundredwidgets.widget.designs.common.batteryOrSample

/** Canvas P1 "Battery": label, percentage in Geist and five green cells on black. */
@Composable
internal fun PaperBattery(ctx: WidgetRenderContext) {
    val context = LocalContext.current
    val battery = ctx.batteryOrSample()
    val filled = battery?.let { ((it.level + 10) / 20).coerceIn(1, 5) } ?: 0
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_paper_black))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = context.getString(
                if (battery?.isCharging == true) R.string.wd_paper_charging else R.string.wd_paper_battery_label,
            ),
            style = TextStyle(color = ColorProvider(PaperColors.label), fontSize = 9.5.sp, fontWeight = FontWeight.Medium),
        )
        Spacer(GlanceModifier.height(4.dp))
        GlanceStyledText(
            text = battery?.let { "${it.level}%" } ?: "--%",
            font = WidgetFont.GEIST_SEMIBOLD,
            sizeSp = 30f,
            color = PaperColors.white,
            gravity = Gravity.CENTER,
        )
        Spacer(GlanceModifier.defaultWeight())
        Row(
            modifier = GlanceModifier
                .background(ImageProvider(R.drawable.wd_bg_paper_cells))
                .padding(5.dp),
        ) {
            repeat(5) { cell ->
                if (cell > 0) Spacer(GlanceModifier.width(5.dp))
                Box(
                    modifier = GlanceModifier
                        .size(width = 22.dp, height = 40.dp)
                        .background(
                            ImageProvider(if (cell < filled) R.drawable.wd_paper_cell_on else R.drawable.wd_paper_cell_off),
                        ),
                ) {}
            }
        }
    }
}
