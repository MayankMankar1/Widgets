package com.hundredwidgets.app.ui.motion

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * Shared-element plumbing between navigation destinations. The app root provides the
 * [SharedTransitionScope]; each destination provides its own [AnimatedVisibilityScope].
 * Screens only call [sharedHero], so they still work (without the morph) in previews.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

val LocalNavAnimatedScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

@Composable
fun ProvideNavAnimatedScope(scope: AnimatedVisibilityScope, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNavAnimatedScope provides scope, content = content)
}

/** Hero keys, so both ends of a morph use the same one. */
object HeroKeys {
    fun designBackdrop(designId: String) = "design-backdrop-$designId"
    fun designTitle(designId: String) = "design-title-$designId"
}

@OptIn(ExperimentalSharedTransitionApi::class)
private val heroBounds = BoundsTransform { _, _ -> tween(Motion.LONG, easing = Motion.Emphasized) }

/** Morphs this element into the element with the same [key] on the next screen. */
@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedHero(key: String): Modifier = composed {
    val shared = LocalSharedTransitionScope.current
    val scope = LocalNavAnimatedScope.current
    if (shared == null || scope == null || LocalReducedMotion.current) {
        Modifier
    } else {
        with(shared) {
            Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key),
                animatedVisibilityScope = scope,
                boundsTransform = heroBounds,
            )
        }
    }
}
