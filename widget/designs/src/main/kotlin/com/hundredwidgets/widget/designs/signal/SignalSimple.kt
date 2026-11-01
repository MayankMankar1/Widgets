package com.hundredwidgets.widget.designs.signal

import android.content.Intent
import android.provider.Settings
import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.AnalogFace
import com.hundredwidgets.widget.designs.common.GlanceAnalogClock
import com.hundredwidgets.widget.designs.common.GlanceTextClock
import com.hundredwidgets.widget.designs.common.Layer
import com.hundredwidgets.widget.designs.common.WidgetFont

/** Canvas S1 "Analog clock": white disc, black hands, a long red second hand (Android 12+). */
@Composable
internal fun SignalAnalog() {
    GlanceAnalogClock(face = AnalogFace.SIGNAL_WHITE, modifier = GlanceModifier.fillMaxSize())
}

/** Canvas S2 "Dot clock": hours over minutes in the Doto dot-matrix face, split by a red dot. */
@Composable
internal fun SignalDotClock(ctx: WidgetRenderContext) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_signal_pill_black)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlanceTextClock(
            pattern12 = "hh",
            pattern24 = "HH",
            clockFormat = ctx.clockFormat,
            font = WidgetFont.DOTO,
            sizeSp = 36f,
            color = SignalColors.paper,
            gravity = Gravity.CENTER,
        )
        Spacer(GlanceModifier.height(6.dp))
        Box(GlanceModifier.size(6.dp).background(ImageProvider(R.drawable.wd_signal_dot_red))) {}
        Spacer(GlanceModifier.height(6.dp))
        GlanceTextClock(
            pattern12 = "mm",
            pattern24 = "mm",
            clockFormat = ctx.clockFormat,
            font = WidgetFont.DOTO,
            sizeSp = 36f,
            color = SignalColors.paper,
            gravity = Gravity.CENTER,
        )
    }
}

/** Canvas S2 "Today's date": the short month in Space Mono and a huge day in Instrument Serif. */
@Composable
internal fun SignalDate(ctx: WidgetRenderContext) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_signal_white)),
    ) {
        Layer(Alignment.TopStart, GlanceModifier.padding(start = 16.dp, top = 14.dp)) {
            GlanceTextClock(
                pattern12 = "MMM",
                pattern24 = "MMM",
                clockFormat = ctx.clockFormat,
                font = WidgetFont.SPACE_MONO,
                sizeSp = 10f,
                color = SignalColors.muted,
                caps = true,
            )
        }
        Layer(Alignment.Center, GlanceModifier.padding(top = 14.dp)) {
            GlanceTextClock(
                pattern12 = "d",
                pattern24 = "d",
                clockFormat = ctx.clockFormat,
                font = WidgetFont.INSTRUMENT_SERIF,
                sizeSp = 108f,
                color = SignalColors.ink,
                gravity = Gravity.CENTER,
            )
        }
    }
}

/**
 * Canvas S2 "Location". Adapt: apps can't switch location on or off, so the button opens
 * the phone's location settings.
 */
@Composable
internal fun SignalLocation(ctx: WidgetRenderContext) {
    val settings = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val tap = if (ctx.isPreview) GlanceModifier else GlanceModifier.clickable(actionStartActivity(settings))
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_signal_circle_red))
            .then(tap),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(R.drawable.wd_ic_pin),
            contentDescription = null,
            colorFilter = ColorFilter.tint(ColorProvider(SignalColors.paper)),
            modifier = GlanceModifier.size(26.dp),
        )
    }
}
