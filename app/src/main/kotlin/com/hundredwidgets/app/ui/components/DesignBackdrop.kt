package com.hundredwidgets.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.hundredwidgets.app.ui.info
import com.hundredwidgets.app.ui.motion.HeroKeys
import com.hundredwidgets.app.ui.motion.sharedHero
import com.hundredwidgets.widget.designs.WidgetDesign

/**
 * A design shown on its theme's wallpaper, the way it will look on a home screen. The
 * wallpaper is the hero element that morphs between the catalogue card and the detail page;
 * the live preview inside it fades in on its own.
 */
@Composable
fun DesignBackdrop(
    design: WidgetDesign,
    modifier: Modifier = Modifier,
    hero: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        BundledImage(
            res = design.theme.info.backdrop,
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .then(if (hero) Modifier.sharedHero(HeroKeys.designBackdrop(design.id.value)) else Modifier),
        )
        // A light scrim keeps busy wallpapers from competing with the widget.
        Box(
            Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(listOf(Color(0x14000000), Color(0x52000000)))),
        )
        content()
    }
}
