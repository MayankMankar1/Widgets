package com.hundredwidgets.app.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.motion.Motion

/**
 * The app's design system: colour tokens for dark and light, the Inter type scale, spacing,
 * radii and icon sizes. Screens use these instead of raw values (docs/ux-guide.md §4).
 */
private val DarkColors = darkColorScheme(
    background = Color(0xFF0B0B0D),
    surface = Color(0xFF0B0B0D),
    surfaceContainer = Color(0xFF17171A),
    surfaceContainerHigh = Color(0xFF202024),
    surfaceContainerHighest = Color(0xFF2A2A2F),
    surfaceVariant = Color(0xFF202024),
    onBackground = Color(0xFFF5F5F5),
    onSurface = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF929299),
    primary = Color(0xFF9B8AFB),
    onPrimary = Color(0xFF160E3D),
    primaryContainer = Color(0xFF2A2350),
    onPrimaryContainer = Color(0xFFD9D2FF),
    secondaryContainer = Color(0xFF2A2A2F),
    onSecondaryContainer = Color(0xFFF5F5F5),
    outline = Color(0xFF2E2E33),
    outlineVariant = Color(0xFF232327),
    scrim = Color(0xFF000000),
)

private val LightColors = lightColorScheme(
    background = Color(0xFFF7F7F5),
    surface = Color(0xFFF7F7F5),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFEEEEEB),
    surfaceContainerHighest = Color(0xFFE6E6E2),
    surfaceVariant = Color(0xFFEEEEEB),
    onBackground = Color(0xFF111113),
    onSurface = Color(0xFF111113),
    onSurfaceVariant = Color(0xFF6B6B73),
    primary = Color(0xFF6D5BF0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEAE6FF),
    onPrimaryContainer = Color(0xFF2B1F7A),
    secondaryContainer = Color(0xFFE6E6E2),
    onSecondaryContainer = Color(0xFF111113),
    outline = Color(0xFFDCDCD8),
    outlineVariant = Color(0xFFE8E8E4),
    scrim = Color(0xFF000000),
)

private val Inter = FontFamily(
    Font(R.font.app_inter_regular, FontWeight.Normal),
    Font(R.font.app_inter_medium, FontWeight.Medium),
    Font(R.font.app_inter_semibold, FontWeight.SemiBold),
)

private fun inter(size: Int, line: Int, weight: FontWeight, tracking: Double = 0.0) = TextStyle(
    fontFamily = Inter,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = tracking.sp,
)

/** One type scale: tight display sizes, comfortable reading sizes. */
private val AppTypography = Typography(
    displaySmall = inter(34, 40, FontWeight.SemiBold, -0.8),
    headlineLarge = inter(30, 36, FontWeight.SemiBold, -0.6),
    headlineMedium = inter(28, 34, FontWeight.SemiBold, -0.5),
    headlineSmall = inter(22, 28, FontWeight.SemiBold, -0.3),
    titleLarge = inter(20, 26, FontWeight.SemiBold, -0.2),
    titleMedium = inter(16, 22, FontWeight.SemiBold, -0.1),
    titleSmall = inter(14, 20, FontWeight.SemiBold),
    bodyLarge = inter(16, 24, FontWeight.Normal),
    bodyMedium = inter(14, 20, FontWeight.Normal),
    bodySmall = inter(13, 18, FontWeight.Normal),
    labelLarge = inter(14, 20, FontWeight.Medium),
    labelMedium = inter(12, 16, FontWeight.Medium, 0.1),
    labelSmall = inter(11, 14, FontWeight.Medium, 0.2),
)

/** Spacing on a 4 dp grid. */
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val section = 32.dp

    /** Horizontal screen padding. */
    val screen = 20.dp
}

/** Corner radii, largest for hero images, smallest for chips and rows. */
object Radius {
    val hero = 28.dp
    val card = 24.dp
    val tile = 20.dp
    val row = 16.dp
    val chip = 12.dp
}

object IconSize {
    val sm = 18.dp
    val md = 22.dp
    val lg = 24.dp
}

/**
 * Applies the chosen palette. Colours animate when the theme changes, so switching Light,
 * Dark or System cross-fades the whole interface instead of snapping.
 */
@Composable
fun HundredWidgetsTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    val target = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = animated(target),
        typography = AppTypography,
        content = content,
    )
}

@Composable
private fun animated(target: ColorScheme): ColorScheme {
    @Composable
    fun Color.anim(label: String): Color =
        animateColorAsState(this, tween(Motion.LONG, easing = Motion.Emphasized), label = label).value

    return target.copy(
        background = target.background.anim("background"),
        surface = target.surface.anim("surface"),
        surfaceContainer = target.surfaceContainer.anim("surfaceContainer"),
        surfaceContainerHigh = target.surfaceContainerHigh.anim("surfaceContainerHigh"),
        surfaceContainerHighest = target.surfaceContainerHighest.anim("surfaceContainerHighest"),
        surfaceVariant = target.surfaceVariant.anim("surfaceVariant"),
        onBackground = target.onBackground.anim("onBackground"),
        onSurface = target.onSurface.anim("onSurface"),
        onSurfaceVariant = target.onSurfaceVariant.anim("onSurfaceVariant"),
        primary = target.primary.anim("primary"),
        onPrimary = target.onPrimary.anim("onPrimary"),
        primaryContainer = target.primaryContainer.anim("primaryContainer"),
        onPrimaryContainer = target.onPrimaryContainer.anim("onPrimaryContainer"),
        secondaryContainer = target.secondaryContainer.anim("secondaryContainer"),
        onSecondaryContainer = target.onSecondaryContainer.anim("onSecondaryContainer"),
        outline = target.outline.anim("outline"),
        outlineVariant = target.outlineVariant.anim("outlineVariant"),
    )
}
