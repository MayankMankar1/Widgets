package com.hundredwidgets.widget.designs.comic

import androidx.compose.ui.graphics.Color
import com.hundredwidgets.core.model.Category
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DataSource
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetDesign

/**
 * Comic: inked panels, captions and sound effects. Canvas boards C1–C2. Halftone panels,
 * bubbles and bursts are pre-rendered images (drawable-nodpi); only text that changes is live.
 */
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
    WidgetDesign(
        id = DesignId("comic.masthead"),
        theme = Theme.COMIC,
        category = Category.UTILITY,
        size = CellSize.S4x1,
        title = R.string.wd_comic_masthead,
        needs = setOf(DataSource.DATE),
        addedIn = 2,
        content = { ComicMasthead() },
    ),
    WidgetDesign(
        id = DesignId("comic.city_panel"),
        theme = Theme.COMIC,
        category = Category.PHOTO,
        size = CellSize.S2x2,
        title = R.string.wd_comic_city_panel,
        addedIn = 2,
        content = { ComicCityPanel(it) },
    ),
    WidgetDesign(
        id = DesignId("comic.sound_effect"),
        theme = Theme.COMIC,
        category = Category.UTILITY,
        size = CellSize.S2x2,
        title = R.string.wd_comic_sound_effect,
        addedIn = 2,
        content = { ComicSoundEffect(it) },
    ),
    WidgetDesign(
        id = DesignId("comic.bubble_clock"),
        theme = Theme.COMIC,
        category = Category.CLOCK,
        size = CellSize.S2x2,
        title = R.string.wd_comic_bubble_clock,
        addedIn = 2,
        content = { ComicBubbleClock(it) },
    ),
    WidgetDesign(
        id = DesignId("comic.battery"),
        theme = Theme.COMIC,
        category = Category.BATTERY,
        size = CellSize.S2x1,
        title = R.string.wd_comic_battery,
        needs = setOf(DataSource.BATTERY),
        addedIn = 2,
        content = { ComicBattery(it) },
    ),
    WidgetDesign(
        id = DesignId("comic.to_be_continued"),
        theme = Theme.COMIC,
        category = Category.CALENDAR,
        size = CellSize.S2x1,
        title = R.string.wd_comic_to_be_continued,
        needs = setOf(DataSource.DATE),
        addedIn = 2,
        content = { ComicToBeContinued() },
    ),
    WidgetDesign(
        id = DesignId("comic.search"),
        theme = Theme.COMIC,
        category = Category.SEARCH,
        size = CellSize.S4x1,
        title = R.string.wd_comic_search,
        addedIn = 2,
        content = { ComicSearch(it) },
    ),
)

internal object ComicColors {
    val ink = Color(0xFF171311)
    val cream = Color(0xFFF6EBD0)
    val red = Color(0xFFB3202A)
}
