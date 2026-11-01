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
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import com.hundredwidgets.core.model.ClockFormat
import com.hundredwidgets.widget.designs.R

/**
 * Bundled theme fonts (OFL, licences in assets/licenses). Glance's own Text can only use
 * system fonts and RemoteViews can't change a font at runtime, so each font has its own
 * XML layouts: a TextClock, an all-caps TextClock and a plain TextView.
 */
enum class WidgetFont(
    @LayoutRes internal val clock: Int,
    @LayoutRes internal val clockCaps: Int,
    @LayoutRes internal val text: Int,
) {
    INTER_LIGHT(R.layout.wd_clock_inter_light, R.layout.wd_clock_inter_light_caps, R.layout.wd_text_inter_light),
    INTER_REGULAR(R.layout.wd_clock_inter_regular, R.layout.wd_clock_inter_regular_caps, R.layout.wd_text_inter_regular),
    INTER_MEDIUM(R.layout.wd_clock_inter_medium, R.layout.wd_clock_inter_medium_caps, R.layout.wd_text_inter_medium),
    ARCHIVO_CONDENSED(
        R.layout.wd_clock_archivo_condensed_extrabold,
        R.layout.wd_clock_archivo_condensed_extrabold_caps,
        R.layout.wd_text_archivo_condensed_extrabold,
    ),
    GEIST_REGULAR(R.layout.wd_clock_geist_regular, R.layout.wd_clock_geist_regular_caps, R.layout.wd_text_geist_regular),
    GEIST_SEMIBOLD(R.layout.wd_clock_geist_semibold, R.layout.wd_clock_geist_semibold_caps, R.layout.wd_text_geist_semibold),
    SPACE_GROTESK_MEDIUM(
        R.layout.wd_clock_space_grotesk_medium,
        R.layout.wd_clock_space_grotesk_medium_caps,
        R.layout.wd_text_space_grotesk_medium,
    ),
    SPACE_GROTESK_BOLD(
        R.layout.wd_clock_space_grotesk_bold,
        R.layout.wd_clock_space_grotesk_bold_caps,
        R.layout.wd_text_space_grotesk_bold,
    ),
    SPACE_MONO(R.layout.wd_clock_space_mono_regular, R.layout.wd_clock_space_mono_regular_caps, R.layout.wd_text_space_mono_regular),
    SPACE_MONO_BOLD(R.layout.wd_clock_space_mono_bold, R.layout.wd_clock_space_mono_bold_caps, R.layout.wd_text_space_mono_bold),
    DOTO(R.layout.wd_clock_doto_extrabold, R.layout.wd_clock_doto_extrabold_caps, R.layout.wd_text_doto_extrabold),
    BANGERS(R.layout.wd_clock_bangers, R.layout.wd_clock_bangers_caps, R.layout.wd_text_bangers),
    COMIC_NEUE(R.layout.wd_clock_comic_neue_regular, R.layout.wd_clock_comic_neue_regular_caps, R.layout.wd_text_comic_neue_regular),
    COMIC_NEUE_BOLD(R.layout.wd_clock_comic_neue_bold, R.layout.wd_clock_comic_neue_bold_caps, R.layout.wd_text_comic_neue_bold),
    INSTRUMENT_SERIF(R.layout.wd_clock_instrument_serif, R.layout.wd_clock_instrument_serif_caps, R.layout.wd_text_instrument_serif),
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
    font: WidgetFont,
    sizeSp: Float,
    color: Color,
    modifier: GlanceModifier = GlanceModifier,
    gravity: Int = Gravity.START,
    caps: Boolean = false,
) {
    val context = LocalContext.current
    val (format12, format24) = when (clockFormat) {
        ClockFormat.SYSTEM -> pattern12 to pattern24
        ClockFormat.H12 -> pattern12 to pattern12
        ClockFormat.H24 -> pattern24 to pattern24
    }
    val views = RemoteViews(context.packageName, if (caps) font.clockCaps else font.clock).apply {
        setCharSequence(R.id.wd_text_clock, "setFormat12Hour", format12)
        setCharSequence(R.id.wd_text_clock, "setFormat24Hour", format24)
        setTextViewTextSize(R.id.wd_text_clock, TypedValue.COMPLEX_UNIT_SP, sizeSp)
        setTextColor(R.id.wd_text_clock, color.toArgb())
        setInt(R.id.wd_text_clock, "setGravity", gravity)
    }
    BoxedRemoteViews(views, modifier)
}

/** Static text in a bundled theme font (Glance's Text is limited to system fonts). */
@Composable
fun GlanceStyledText(
    text: String,
    font: WidgetFont,
    sizeSp: Float,
    color: Color,
    modifier: GlanceModifier = GlanceModifier,
    gravity: Int = Gravity.START,
    maxLines: Int = 1,
) {
    val context = LocalContext.current
    val views = RemoteViews(context.packageName, font.text).apply {
        setTextViewText(R.id.wd_text, text)
        setTextViewTextSize(R.id.wd_text, TypedValue.COMPLEX_UNIT_SP, sizeSp)
        setTextColor(R.id.wd_text, color.toArgb())
        setInt(R.id.wd_text, "setGravity", gravity)
        setInt(R.id.wd_text, "setMaxLines", maxLines)
    }
    BoxedRemoteViews(views, modifier)
}

/**
 * Placed straight into a Column or Row, an AndroidRemoteViews breaks the parent's layout:
 * it lands top-start and later siblings vanish. Inside a Box it lays out correctly.
 */
@Composable
private fun BoxedRemoteViews(views: RemoteViews, modifier: GlanceModifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AndroidRemoteViews(remoteViews = views)
    }
}

/** Analogue faces: the dial and hands are drawables, rotated by the launcher. */
enum class AnalogFace(@LayoutRes internal val layout: Int) {
    BAUHAUS_WHITE_DISC(R.layout.wd_analog_bauhaus_white_disc),
    PAPER_BLACK(R.layout.wd_analog_paper_black),
    SIGNAL_WHITE(R.layout.wd_analog_signal_white),
}

@Composable
fun GlanceAnalogClock(face: AnalogFace, modifier: GlanceModifier = GlanceModifier) {
    val context = LocalContext.current
    AndroidRemoteViews(remoteViews = RemoteViews(context.packageName, face.layout), modifier = modifier)
}
