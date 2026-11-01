package com.hundredwidgets.widget.designs.paper

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.layout.fillMaxSize
import com.hundredwidgets.widget.designs.common.AnalogFace
import com.hundredwidgets.widget.designs.common.GlanceAnalogClock

/** Canvas P1 "Analog clock": a black disc, grey ticks, white hands and a red second hand (Android 12+). */
@Composable
internal fun PaperAnalog() {
    GlanceAnalogClock(face = AnalogFace.PAPER_BLACK, modifier = GlanceModifier.fillMaxSize())
}
