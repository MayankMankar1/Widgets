package com.hundredwidgets.widget.designs.paper

import androidx.compose.ui.graphics.Color
import com.hundredwidgets.core.model.Category
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DataSource
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetDesign

/**
 * Paper: black and white shapes, one accent each, Geist type. Canvas boards P1–P2.
 * Only designs that need no special permission are here (README §5); sleep mode (DND) waits.
 */
internal val paperDesigns: List<WidgetDesign> = listOf(
    WidgetDesign(
        id = DesignId("paper.analog"),
        theme = Theme.PAPER,
        category = Category.CLOCK,
        size = CellSize.S2x2,
        title = R.string.wd_paper_analog,
        addedIn = 2,
        content = { PaperAnalog() },
    ),
    WidgetDesign(
        id = DesignId("paper.volume"),
        theme = Theme.PAPER,
        category = Category.CONTROL,
        size = CellSize.S1x2,
        title = R.string.wd_paper_volume,
        addedIn = 2,
        content = { PaperVolume(it) },
    ),
    WidgetDesign(
        id = DesignId("paper.battery"),
        theme = Theme.PAPER,
        category = Category.BATTERY,
        size = CellSize.S2x2,
        title = R.string.wd_paper_battery,
        needs = setOf(DataSource.BATTERY),
        addedIn = 2,
        content = { PaperBattery(it) },
    ),
    WidgetDesign(
        id = DesignId("paper.compass"),
        theme = Theme.PAPER,
        category = Category.UTILITY,
        size = CellSize.S2x2,
        title = R.string.wd_paper_compass,
        addedIn = 2,
        content = { PaperCompass(it) },
    ),
)

internal object PaperColors {
    val black = Color(0xFF0E0E10)
    val white = Color(0xFFFFFFFF)
    val label = Color(0xFFA1A1A6)
    val red = Color(0xFFFF3B30)
}
