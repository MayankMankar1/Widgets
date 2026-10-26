package com.hundredwidgets.widget.designs.horizon

import androidx.compose.ui.graphics.Color
import com.hundredwidgets.core.model.Category
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DataSource
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetDesign

/** Horizon: frosted glass over mountain light. Canvas boards H1–H3. */
internal val horizonDesigns: List<WidgetDesign> = listOf(
    WidgetDesign(
        id = DesignId("horizon.clock_quote"),
        theme = Theme.HORIZON,
        category = Category.CLOCK,
        size = CellSize.S2x2,
        title = R.string.wd_horizon_clock_quote,
        needs = setOf(DataSource.DATE),
        addedIn = 1,
        content = { HorizonClockQuote(it) },
    ),
)

internal object HorizonColors {
    val text = Color.White
    val textSoft = Color(0xDBFFFFFF)
    val textMuted = Color(0xBDFFFFFF)
}
