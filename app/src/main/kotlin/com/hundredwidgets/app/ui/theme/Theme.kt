package com.hundredwidgets.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Ink = Color(0xFF0E0F12)
private val Surface = Color(0xFF17181C)
private val SurfaceHigh = Color(0xFF212228)
private val Accent = Color(0xFFFF9F45)
private val OnAccent = Color(0xFF2B1600)
private val Text = Color(0xFFF2F1EC)
private val TextMuted = Color(0xFFA6A7AD)

private val DarkColors = darkColorScheme(
    primary = Accent,
    onPrimary = OnAccent,
    secondary = Color(0xFFE8572A),
    onSecondary = Color.White,
    background = Ink,
    onBackground = Text,
    surface = Ink,
    onSurface = Text,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = TextMuted,
    surfaceContainer = Surface,
    surfaceContainerHigh = SurfaceHigh,
    outline = Color(0xFF3A3B42),
)

/** The app is dark-only, so the widgets and wallpapers are the brightest things on screen. */
@Composable
fun HundredWidgetsTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}
