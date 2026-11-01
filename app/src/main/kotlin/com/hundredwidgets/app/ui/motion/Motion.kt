package com.hundredwidgets.app.ui.motion

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * One set of timings, easings and springs for the whole app, so every movement feels like
 * it belongs to the same product. Values and usage rules live in docs/ux-guide.md.
 */
object Motion {
    /** Small state changes: chips, icons, press feedback. */
    const val SHORT = 150

    /** Most transitions: screens, sheets, content swaps. */
    const val MEDIUM = 300

    /** Large or expressive moves: hero morphs, list entrances. */
    const val LONG = 450

    /** Delay between list items entering one after another. */
    const val STAGGER = 45L

    val Emphasized = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

    /** Settles without overshoot: layout, size and position changes. */
    fun <T> gentle(): SpringSpec<T> = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)

    /** A small overshoot: press feedback, toggles, success marks. */
    fun <T> bouncy(): SpringSpec<T> =
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
}

/** True when the phone's "Remove animations" setting is on; decorative motion is then skipped. */
val LocalReducedMotion = staticCompositionLocalOf { false }

@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

/** A click target that shrinks slightly while pressed and springs back, with a ripple. */
fun Modifier.pressable(
    onClickLabel: String? = null,
    role: Role? = Role.Button,
    pressedScale: Float = 0.96f,
    onClick: () -> Unit,
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && !LocalReducedMotion.current) pressedScale else 1f,
        animationSpec = Motion.bouncy(),
        label = "pressScale",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(
        interactionSource = interaction,
        indication = ripple(),
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )
}

/** Loading placeholder: a soft highlight sweeps across while content is being prepared. */
fun Modifier.shimmer(shape: Shape = RoundedCornerShape(24.dp)): Modifier = composed {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.outline
    val reduced = LocalReducedMotion.current
    val progress = if (reduced) {
        0f
    } else {
        val transition = rememberInfiniteTransition(label = "shimmer")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1_200, easing = LinearEasing)),
            label = "shimmerProgress",
        ).value
    }
    clip(shape).drawBehind {
        if (reduced) {
            drawRect(base)
        } else {
            val center = size.width * (progress * 3f - 1f)
            drawRect(
                Brush.linearGradient(
                    colors = listOf(base, highlight, base),
                    start = Offset(center - size.width / 2f, 0f),
                    end = Offset(center + size.width / 2f, size.height),
                ),
            )
        }
    }
}

/**
 * Fades and lifts an item in the first time it appears, [index] steps after the first one.
 * [seen] remembers keys that already entered, so items scrolled back into view don't replay.
 */
fun Modifier.enterOnce(index: Int, key: Any, seen: MutableSet<Any>): Modifier = composed {
    val reduced = LocalReducedMotion.current
    val progress = remember { Animatable(if (reduced || key in seen) 1f else 0f) }
    LaunchedEffect(key) {
        seen += key
        if (progress.value < 1f) {
            delay(index.coerceAtMost(8) * Motion.STAGGER)
            progress.animateTo(1f, tween(Motion.LONG, easing = Motion.EmphasizedDecelerate))
        }
    }
    graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * 24.dp.toPx()
    }
}
