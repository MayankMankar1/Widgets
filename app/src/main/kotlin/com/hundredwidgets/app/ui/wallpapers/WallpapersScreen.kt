package com.hundredwidgets.app.ui.wallpapers

import android.text.format.DateFormat
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.BundledImage
import com.hundredwidgets.app.ui.components.ScreenTitle
import com.hundredwidgets.app.ui.motion.LocalReducedMotion
import com.hundredwidgets.app.ui.motion.Motion
import com.hundredwidgets.app.ui.motion.enterOnce
import com.hundredwidgets.app.ui.motion.pressable
import com.hundredwidgets.app.ui.theme.Radius
import com.hundredwidgets.app.ui.theme.Spacing
import com.hundredwidgets.app.wallpaper.WallpaperTarget
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Every wallpaper in one two-column grid, mixed across collections so neighbours differ. Tapping
 * one grows it into a full-screen preview with a sample lock-screen clock, where the user sets it
 * and sees progress and success in place.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WallpapersScreen(viewModel: WallpapersViewModel = hiltViewModel()) {
    val status by viewModel.status.collectAsStateWithLifecycle()
    var previewingId by rememberSaveable { mutableStateOf<String?>(null) }
    val haptics = LocalHapticFeedback.current
    val seen = remember { mutableSetOf<Any>() }
    // Hoisted so closing a preview returns to the same scroll position.
    val gridState = rememberLazyGridState()

    BackHandler(enabled = previewingId != null) { previewingId = null }

    LaunchedEffect(status) {
        when (status) {
            WallpaperStatus.DONE -> {
                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                delay(1_200)
                viewModel.statusShown()
                previewingId = null
            }
            WallpaperStatus.FAILED -> {
                haptics.performHapticFeedback(HapticFeedbackType.Reject)
                delay(2_500)
                viewModel.statusShown()
            }
            WallpaperStatus.APPLYING, null -> Unit
        }
    }

    SharedTransitionLayout(Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = previewingId?.let(WallpaperCatalog::find),
            transitionSpec = { fadeIn(tween(Motion.MEDIUM)) togetherWith fadeOut(tween(Motion.MEDIUM)) },
            label = "wallpaperPreview",
        ) { wallpaper ->
            if (wallpaper == null) {
                Column(Modifier.fillMaxSize()) {
                    ScreenTitle(
                        text = stringResource(R.string.tab_wallpapers),
                        subtitle = stringResource(R.string.wallpapers_preview_hint),
                    )
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = Spacing.lg, end = Spacing.lg, bottom = Spacing.section),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        itemsIndexed(WallpaperCatalog.all, key = { _, it -> it.id }) { index, item ->
                            WallpaperTile(
                                wallpaper = item,
                                onClick = { previewingId = item.id },
                                modifier = Modifier.enterOnce(index = index, key = item.id, seen = seen),
                                imageModifier = Modifier.wallpaperHero(item, this@SharedTransitionLayout, this@AnimatedContent),
                            )
                        }
                    }
                }
            } else {
                WallpaperPreview(
                    wallpaper = wallpaper,
                    status = status,
                    imageModifier = Modifier.wallpaperHero(wallpaper, this@SharedTransitionLayout, this@AnimatedContent),
                    onSet = { target -> viewModel.setWallpaper(wallpaper, target) },
                    onClose = { previewingId = null },
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Modifier.wallpaperHero(
    wallpaper: Wallpaper,
    shared: SharedTransitionScope,
    scope: AnimatedVisibilityScope,
): Modifier {
    if (LocalReducedMotion.current) return this
    return with(shared) {
        this@wallpaperHero.sharedElement(
            sharedContentState = rememberSharedContentState("wallpaper-${wallpaper.id}"),
            animatedVisibilityScope = scope,
            boundsTransform = { _, _ -> tween(Motion.LONG, easing = Motion.Emphasized) },
        )
    }
}

/** A tall rounded thumbnail with no caption: the picture is the label (its title is read out). */
@Composable
private fun WallpaperTile(
    wallpaper: Wallpaper,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 18f)
            .clip(RoundedCornerShape(Radius.card))
            .pressable(onClickLabel = stringResource(R.string.action_open), onClick = onClick),
    ) {
        BundledImage(res = wallpaper.thumb, contentDescription = wallpaper.title(), modifier = imageModifier.fillMaxSize())
    }
}

@Composable
private fun WallpaperPreview(
    wallpaper: Wallpaper,
    status: WallpaperStatus?,
    onSet: (WallpaperTarget) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val title = wallpaper.title()
    val now = remember { LocalDateTime.now() }
    val time = remember(now) {
        now.format(DateTimeFormatter.ofPattern(if (DateFormat.is24HourFormat(context)) "HH:mm" else "h:mm"))
    }
    val date = remember(now) { now.format(DateTimeFormatter.ofPattern("EEEE, d MMMM")) }

    Box(
        modifier
            .fillMaxSize()
            .padding(12.dp)
            .clip(RoundedCornerShape(28.dp)),
    ) {
        Image(
            painter = painterResource(wallpaper.image),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = imageModifier.fillMaxSize(),
        )
        IconButton(
            onClick = onClose,
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x66000000), contentColor = Color.White),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
        ) {
            Icon(painterResource(R.drawable.ic_close), contentDescription = stringResource(R.string.action_close))
        }
        // Sample lock-screen clock, so the wallpaper is judged with something on top of it.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp),
        ) {
            Text(time, color = Color.White, fontSize = 72.sp, fontWeight = FontWeight.Light)
            Text(date, color = Color(0xE6FFFFFF), style = MaterialTheme.typography.titleMedium)
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC000000))))
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedContent(
                targetState = status,
                transitionSpec = { fadeIn(tween(Motion.MEDIUM)) togetherWith fadeOut(tween(Motion.SHORT)) },
                label = "wallpaperStatus",
            ) { current ->
                when (current) {
                    null, WallpaperStatus.FAILED -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (current == WallpaperStatus.FAILED) {
                                stringResource(R.string.wallpapers_failed)
                            } else {
                                stringResource(R.string.wallpapers_set_title, title)
                            },
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            TargetButton(R.string.wallpapers_target_home, Modifier.weight(1f)) { onSet(WallpaperTarget.HOME) }
                            TargetButton(R.string.wallpapers_target_lock, Modifier.weight(1f)) { onSet(WallpaperTarget.LOCK) }
                            TargetButton(R.string.wallpapers_target_both, Modifier.weight(1f)) { onSet(WallpaperTarget.BOTH) }
                        }
                    }
                    WallpaperStatus.APPLYING -> StatusRow(text = stringResource(R.string.wallpapers_applying)) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                    }
                    WallpaperStatus.DONE -> StatusRow(text = stringResource(R.string.wallpapers_done)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TargetButton(label: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = Spacing.sm),
        modifier = modifier.height(48.dp),
    ) {
        Text(stringResource(label), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun StatusRow(text: String, icon: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(88.dp)) {
        icon()
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color.White, style = MaterialTheme.typography.titleMedium)
    }
}
