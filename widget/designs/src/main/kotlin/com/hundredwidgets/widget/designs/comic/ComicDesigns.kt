package com.hundredwidgets.widget.designs.comic

import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import com.hundredwidgets.core.model.Category
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetDesign
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.ClockFont
import com.hundredwidgets.widget.designs.common.GlanceTextClock

/** Comic: inked panels, captions and sound effects. Canvas boards C1–C2. */
internal val comicDesigns: List<WidgetDesign> = listOf(
    WidgetDesign(
        id = DesignId("comic.calendar_page"),
        theme = Theme.COMIC,
        category = Category.CALENDAR,
        size = CellSize.S2x2,
        title = R.string.wd_comic_calendar_page,
        addedIn = 1,
        content = { ComicCalendarPage(it) },
    ),
)

internal object ComicColors {
    val ink = Color(0xFF171311)
    val cream = Color(0xFFF6EBD0)
}

/**
 * Canvas C1 "Calendar page": red header with the month, big day number, weekday.
 * All three are TextClocks, so the page turns at midnight without an update.
 * Bangers is not bundled yet; condensed bold stands in.
 */
@Composable
internal fun ComicCalendarPage(ctx: WidgetRenderContext) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_comic_panel))
            // Keeps content off the panel's 4dp offset shadow.
            .padding(end = 4.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(44.dp)
                .background(ImageProvider(R.drawable.wd_bg_comic_header)),
            contentAlignment = Alignment.Center,
        ) {
            GlanceTextClock(
                pattern12 = "MMMM",
                pattern24 = "MMMM",
                clockFormat = ctx.clockFormat,
                font = ClockFont.CONDENSED_BOLD_CAPS,
                sizeSp = 20f,
                color = ComicColors.cream,
                gravity = Gravity.CENTER,
            )
        }
        Spacer(GlanceModifier.defaultWeight())
        GlanceTextClock(
            pattern12 = "d",
            pattern24 = "d",
            clockFormat = ctx.clockFormat,
            font = ClockFont.CONDENSED_BOLD,
            sizeSp = 64f,
            color = ComicColors.ink,
            gravity = Gravity.CENTER,
        )
        GlanceTextClock(
            pattern12 = "EEEE",
            pattern24 = "EEEE",
            clockFormat = ctx.clockFormat,
            font = ClockFont.CONDENSED_BOLD_CAPS,
            sizeSp = 12f,
            color = ComicColors.ink,
            gravity = Gravity.CENTER,
        )
        Spacer(GlanceModifier.defaultWeight())
    }
}
