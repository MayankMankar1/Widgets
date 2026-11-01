package com.hundredwidgets.widget.designs.common

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize

/**
 * A full-size layer inside a Box, with its own alignment. Glance can't position children
 * absolutely, so text over pre-rendered art is stacked as layers (a Box draws children on
 * top of each other), each aligned and padded to its spot on the canvas.
 */
@Composable
internal fun Layer(
    alignment: Alignment,
    modifier: GlanceModifier = GlanceModifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = GlanceModifier.fillMaxSize().then(modifier), contentAlignment = alignment, content = content)
}
