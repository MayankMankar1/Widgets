package com.hundredwidgets.widget.designs.comic

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
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.common.DesignDates
import com.hundredwidgets.widget.designs.common.GlanceStyledText
import com.hundredwidgets.widget.designs.common.WidgetFont

/** Canvas C1 "The Daily Issue": red halftone masthead; the issue number is the day of the year. */
@Composable
internal fun ComicMasthead() {
    val context = LocalContext.current
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_comic_bg_masthead))
            .padding(start = 18.dp, end = 14.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlanceStyledText(
            text = context.getString(R.string.wd_comic_masthead_text),
            font = WidgetFont.BANGERS,
            sizeSp = 30f,
            color = ComicColors.cream,
        )
        Spacer(GlanceModifier.defaultWeight())
        Box(
            modifier = GlanceModifier
                .size(54.dp)
                .background(ImageProvider(R.drawable.wd_bg_comic_badge)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = context.getString(R.string.wd_comic_issue_no),
                    style = TextStyle(color = ColorProvider(ComicColors.ink), fontSize = 8.sp, fontWeight = FontWeight.Bold),
                )
                GlanceStyledText(
                    text = DesignDates.dayOfYear().toString(),
                    font = WidgetFont.BANGERS,
                    sizeSp = 20f,
                    color = ComicColors.ink,
                    gravity = Gravity.CENTER,
                )
            }
        }
    }
}
