package com.hundredwidgets.widget.designs.comic

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.common.DesignDates
import com.hundredwidgets.widget.designs.common.GlanceStyledText
import com.hundredwidgets.widget.designs.common.WidgetFont

/** Canvas C1 "To be continued": a red arrow banner and the days left until Saturday. */
@Composable
internal fun ComicToBeContinued() {
    val context = LocalContext.current
    val days = DesignDates.daysToSaturday()
    val line = if (days == 0) {
        context.getString(R.string.wd_comic_weekend)
    } else {
        context.resources.getQuantityString(R.plurals.wd_comic_days_to_saturday, days, days)
    }
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_comic_bg_tone_2x1))
            .padding(start = 10.dp, top = 9.dp, end = 14.dp, bottom = 4.dp),
    ) {
        Box(
            modifier = GlanceModifier
                .size(width = 150.dp, height = 32.dp)
                .background(ImageProvider(R.drawable.wd_comic_banner))
                .padding(start = 9.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            GlanceStyledText(
                text = context.getString(R.string.wd_comic_tbc),
                font = WidgetFont.BANGERS,
                sizeSp = 15f,
                color = ComicColors.cream,
            )
        }
        Spacer(GlanceModifier.height(6.dp))
        GlanceStyledText(
            text = line.uppercase(),
            font = WidgetFont.COMIC_NEUE_BOLD,
            sizeSp = 10.5f,
            color = ComicColors.ink,
        )
    }
}
