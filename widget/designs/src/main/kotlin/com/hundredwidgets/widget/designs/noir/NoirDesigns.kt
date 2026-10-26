package com.hundredwidgets.widget.designs.noir

import androidx.compose.ui.graphics.Color
import com.hundredwidgets.core.model.Category
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DataSource
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetDesign

/** Noir: solid black on grey; colour lives only in icons. Canvas boards N1–N3. */
internal val noirDesigns: List<WidgetDesign> = listOf(
    WidgetDesign(
        id = DesignId("noir.condensed_clock"),
        theme = Theme.NOIR,
        category = Category.CLOCK,
        size = CellSize.S2x2,
        title = R.string.wd_noir_condensed_clock,
        addedIn = 1,
        content = { NoirCondensedClock(it) },
    ),
    WidgetDesign(
        id = DesignId("noir.battery"),
        theme = Theme.NOIR,
        category = Category.BATTERY,
        size = CellSize.S2x2,
        title = R.string.wd_noir_battery,
        needs = setOf(DataSource.BATTERY),
        addedIn = 1,
        content = { NoirBattery(it) },
    ),
)

internal object NoirColors {
    val hour = Color(0xFF8E8E93)
    val text = Color.White
    val label = Color(0xFFBDBDC2)
    val track = Color(0x33FFFFFF)
    val charging = Color(0xFF35D46A)
}
