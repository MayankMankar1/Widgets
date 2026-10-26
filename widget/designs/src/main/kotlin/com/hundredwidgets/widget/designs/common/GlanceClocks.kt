package com.hundredwidgets.widget.designs.common

import android.util.TypedValue
import android.view.Gravity
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import com.hundredwidgets.core.model.ClockFormat
import com.hundredwidgets.widget.designs.R

/**
 * Fonts for clock text. A TextClock's font can't be changed through RemoteViews, so each
 * font has its own XML layout. Bundled theme fonts are added here once Phase 0 confirms
 * they render on Pixel, Samsung and Xiaomi launchers.
 */
enum class ClockFont(@LayoutRes internal val layout: Int) {
    LIGHT(R.layout.wd_text_clock_light),
    REGULAR(R.layout.wd_text_clock_regular),
    MEDIUM(R.layout.wd_text_clock_medium),
    CONDENSED_BOLD(R.layout.wd_text_clock_condensed_bold),
    CONDENSED_BOLD_CAPS(R.layout.wd_text_clock_condensed_bold_caps),
}

/**
 * Live time or date text. The launcher ticks it, so it never needs a widget update.
 * Patterns follow android.text.format.DateFormat, e.g. "hh:mm", "HH:mm", "EEE, d MMM".
 */
@Composable
fun GlanceTextClock(
    pattern12: String,
    pattern24: String,
    clockFormat: ClockFormat,
    font: ClockFont,
    sizeSp: Float,
    color: Color,
    modifier: GlanceModifier = GlanceModifier,
    gravity: Int = Gravity.START,
) {
    val context = LocalContext.current
    val (format12, format24) = when (clockFormat) {
        ClockFormat.SYSTEM -> pattern12 to pattern24
        ClockFormat.H12 -> pattern12 to pattern12
        ClockFormat.H24 -> pattern24 to pattern24
    }
    val views = RemoteViews(context.packageName, font.layout).apply {
        setCharSequence(R.id.wd_text_clock, "setFormat12Hour", format12)
        setCharSequence(R.id.wd_text_clock, "setFormat24Hour", format24)
        setTextViewTextSize(R.id.wd_text_clock, TypedValue.COMPLEX_UNIT_SP, sizeSp)
        setTextColor(R.id.wd_text_clock, color.toArgb())
        setInt(R.id.wd_text_clock, "setGravity", gravity)
    }
    AndroidRemoteViews(remoteViews = views, modifier = modifier)
}

/** Analogue faces: the dial and hands are drawables, rotated by the launcher. */
enum class AnalogFace(@LayoutRes internal val layout: Int) {
    BAUHAUS_WHITE_DISC(R.layout.wd_analog_bauhaus_white_disc),
}

@Composable
fun GlanceAnalogClock(face: AnalogFace, modifier: GlanceModifier = GlanceModifier) {
    val context = LocalContext.current
    AndroidRemoteViews(remoteViews = RemoteViews(context.packageName, face.layout), modifier = modifier)
}
