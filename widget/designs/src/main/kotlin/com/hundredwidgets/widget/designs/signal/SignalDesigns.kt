package com.hundredwidgets.widget.designs.signal

import androidx.compose.ui.graphics.Color
import com.hundredwidgets.core.model.Category
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetDesign

/**
 * Signal: white, black and signal red; circles, pills and dot-matrix numbers (Doto, Space
 * Mono, Instrument Serif). Canvas boards S1–S3. Only designs that need no special
 * permission are here (README §5).
 */
internal val signalDesigns: List<WidgetDesign> = listOf(
    WidgetDesign(
        id = DesignId("signal.analog"),
        theme = Theme.SIGNAL,
        category = Category.CLOCK,
        size = CellSize.S2x2,
        title = R.string.wd_signal_analog,
        addedIn = 2,
        content = { SignalAnalog() },
    ),
    WidgetDesign(
        id = DesignId("signal.torch"),
        theme = Theme.SIGNAL,
        category = Category.CONTROL,
        size = CellSize.S2x1,
        title = R.string.wd_signal_torch,
        addedIn = 2,
        content = { SignalTorch(it) },
    ),
    WidgetDesign(
        id = DesignId("signal.dot_clock"),
        theme = Theme.SIGNAL,
        category = Category.CLOCK,
        size = CellSize.S1x2,
        title = R.string.wd_signal_dot_clock,
        addedIn = 2,
        content = { SignalDotClock(it) },
    ),
    WidgetDesign(
        id = DesignId("signal.date"),
        theme = Theme.SIGNAL,
        category = Category.CALENDAR,
        size = CellSize.S2x2,
        title = R.string.wd_signal_date,
        addedIn = 2,
        content = { SignalDate(it) },
    ),
    WidgetDesign(
        id = DesignId("signal.location"),
        theme = Theme.SIGNAL,
        category = Category.SHORTCUT,
        size = CellSize.S1x1,
        title = R.string.wd_signal_location,
        addedIn = 2,
        content = { SignalLocation(it) },
    ),
    WidgetDesign(
        id = DesignId("signal.compass"),
        theme = Theme.SIGNAL,
        category = Category.UTILITY,
        size = CellSize.S2x2,
        title = R.string.wd_signal_compass,
        addedIn = 2,
        content = { SignalCompass(it) },
    ),
)

internal object SignalColors {
    val ink = Color(0xFF111111)
    val paper = Color(0xFFF6F6F3)
    val muted = Color(0xFF8A8A86)
    val dim = Color(0xFF9A9A96)
}
