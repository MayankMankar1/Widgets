package com.hundredwidgets.widget.designs.comic

import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.GlanceStyledText
import com.hundredwidgets.widget.designs.common.WidgetFont

/**
 * Canvas C1 "Sound effect": a word in a starburst; tapping shows the next one. The burst is
 * pre-rendered; the word is live text. Adapt: the canvas tilts the word, which widgets can't.
 */
@Composable
internal fun ComicSoundEffect(ctx: WidgetRenderContext) {
    val words = LocalContext.current.resources.getStringArray(R.array.wd_comic_sound_words)
    var index by remember { mutableIntStateOf(0) }
    val tap = if (ctx.isPreview) GlanceModifier else GlanceModifier.clickable { index = (index + 1) % words.size }
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_comic_bg_sfx))
            .then(tap)
            .padding(end = 6.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        GlanceStyledText(
            text = words[index % words.size],
            font = WidgetFont.BANGERS,
            sizeSp = 40f,
            color = ComicColors.red,
            gravity = Gravity.CENTER,
        )
    }
}
