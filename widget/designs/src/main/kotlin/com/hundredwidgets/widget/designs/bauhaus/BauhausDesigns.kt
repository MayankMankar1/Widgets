package com.hundredwidgets.widget.designs.bauhaus

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.hundredwidgets.core.model.Category
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetDesign
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.AnalogFace
import com.hundredwidgets.widget.designs.common.GlanceAnalogClock

/** Bauhaus: watch-face squircles in black, grey and orange. Canvas boards B1–B3. */
internal val bauhausDesigns: List<WidgetDesign> = listOf(
    WidgetDesign(
        id = DesignId("bauhaus.white_disc"),
        theme = Theme.BAUHAUS,
        category = Category.CLOCK,
        size = CellSize.S2x2,
        title = R.string.wd_bauhaus_white_disc,
        addedIn = 1,
        content = { BauhausWhiteDisc(it) },
    ),
)

/** Canvas B2 "Analog clock on a white disc". Second hand on Android 12+ (layout-v31). */
@Composable
@Suppress("UNUSED_PARAMETER")
internal fun BauhausWhiteDisc(ctx: WidgetRenderContext) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_bauhaus_square))
            .padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        GlanceAnalogClock(face = AnalogFace.BAUHAUS_WHITE_DISC, modifier = GlanceModifier.fillMaxSize())
    }
}
