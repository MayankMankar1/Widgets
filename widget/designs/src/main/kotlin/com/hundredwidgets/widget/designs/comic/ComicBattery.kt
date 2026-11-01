package com.hundredwidgets.widget.designs.comic

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
import androidx.glance.layout.fillMaxHeight
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

/** Canvas C1 "Battery": an inked battery with four cells and the level in Bangers, on black. */
@Composable
internal fun ComicBattery(ctx: WidgetRenderContext) {
    val battery = ctx.batteryOrSample()
    val filled = battery?.let { ((it.level + 12) / 25).coerceIn(1, 4) } ?: 0
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_comic_panel_black))
            .padding(start = 12.dp, end = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = GlanceModifier
                    .size(width = 58.dp, height = 30.dp)
                    .background(ImageProvider(R.drawable.wd_comic_batt_frame))
                    .padding(6.dp),
            ) {
                repeat(4) { cell ->
                    if (cell > 0) Spacer(GlanceModifier.width(3.dp))
                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .fillMaxHeight()
                            .then(
                                if (cell < filled) GlanceModifier.background(ImageProvider(R.drawable.wd_comic_batt_cell)) else GlanceModifier,
                            ),
                    ) {}
                }
            }
            Box(
                modifier = GlanceModifier
                    .size(width = 4.dp, height = 12.dp)
                    .background(ImageProvider(R.drawable.wd_comic_batt_nub)),
            ) {}
        }
        Spacer(GlanceModifier.width(10.dp))
        Column {
            Text(
                text = LocalContext.current.getString(R.string.wd_comic_power),
                style = TextStyle(color = ColorProvider(ComicColors.cream), fontSize = 10.sp, fontWeight = FontWeight.Bold),
            )
            Spacer(GlanceModifier.height(2.dp))
            GlanceStyledText(
                text = battery?.let { "${it.level}%" } ?: "--%",
                font = WidgetFont.BANGERS,
                sizeSp = 28f,
                color = ComicColors.cream,
            )
        }
    }
}
