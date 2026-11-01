package com.hundredwidgets.app.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.ScreenTitle
import com.hundredwidgets.app.ui.components.SectionCard
import com.hundredwidgets.app.ui.components.SectionTitle
import com.hundredwidgets.app.ui.info
import com.hundredwidgets.app.ui.motion.LocalReducedMotion
import com.hundredwidgets.app.ui.motion.Motion
import com.hundredwidgets.app.ui.motion.enterOnce
import com.hundredwidgets.app.ui.motion.pressable
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.WidgetRegistry
import kotlinx.coroutines.flow.drop
import kotlin.math.abs

@Composable
fun HomeScreen(
    onOpenTheme: (Theme) -> Unit,
    onOpenDesign: (DesignId) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val placed by viewModel.placed.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState { Theme.entries.size }
    val haptics = LocalHapticFeedback.current
    val seen = remember { mutableSetOf<Any>() }

    // A light tick each time the carousel settles on a new scene.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.drop(1).collect {
            haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenTitle(
                text = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.home_subtitle),
            )
        }
        item { SectionTitle(stringResource(R.string.home_scenes)) }
        item {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 48.dp),
                pageSpacing = 12.dp,
                modifier = Modifier.fillMaxWidth(),
            ) { page ->
                val theme = Theme.entries[page]
                SceneCard(
                    theme = theme,
                    pageOffset = { (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction },
                    onClick = { onOpenTheme(theme) },
                )
            }
        }
        item { PagerDots(pagerState) }
        item { SectionTitle(stringResource(R.string.home_your_widgets)) }
        if (placed.isEmpty()) {
            item {
                SectionCard(Modifier.enterOnce(index = 0, key = "empty", seen = seen)) {
                    Text(
                        stringResource(R.string.home_no_widgets),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            items(placed, key = { it.appWidgetId }) { widget ->
                SectionCard(
                    Modifier
                        .enterOnce(index = placed.indexOf(widget), key = widget.appWidgetId, seen = seen)
                        .clip(RoundedCornerShape(12.dp))
                        .pressable(onClickLabel = stringResource(R.string.action_open)) {
                            onOpenDesign(widget.design.id)
                        },
                ) {
                    Text(stringResource(widget.design.title), style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${widget.design.theme.displayName} · ${widget.design.size.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        item {
            SectionCard(Modifier.enterOnce(index = 1, key = "tip", seen = seen)) {
                Text(stringResource(R.string.home_tip_title), style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.home_tip_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * One scene per theme. Cards beside the centred one shrink and dim, and the wallpaper drifts
 * slower than the card (parallax), so swiping feels like moving through a stack of scenes.
 */
@Composable
private fun SceneCard(theme: Theme, pageOffset: () -> Float, onClick: () -> Unit) {
    val designCount = WidgetRegistry.forTheme(theme).size
    val reduced = LocalReducedMotion.current
    val shape = RoundedCornerShape(32.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .graphicsLayer {
                val distance = abs(pageOffset()).coerceIn(0f, 1f)
                val scale = lerp(1f, 0.88f, distance)
                scaleX = scale
                scaleY = scale
                alpha = lerp(1f, 0.55f, distance)
            }
            .clip(shape)
            .pressable(onClickLabel = stringResource(R.string.action_open), role = Role.Button, onClick = onClick),
    ) {
        Image(
            painter = painterResource(theme.info.wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (!reduced) {
                        scaleX = 1.2f
                        scaleY = 1.2f
                        translationX = pageOffset() * size.width * 0.25f
                    }
                },
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xD9000000))))
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Text(theme.displayName, style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Text(
                stringResource(theme.info.tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xD9FFFFFF),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                pluralStringResource(R.plurals.home_design_count, designCount, designCount),
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xB3FFFFFF),
            )
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

/** The current page's dot stretches into a pill; the others stay small. */
@Composable
private fun PagerDots(state: PagerState) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth(),
    ) {
        repeat(state.pageCount) { index ->
            val selected = state.currentPage == index
            val width by animateDpAsState(if (selected) 22.dp else 8.dp, Motion.gentle(), label = "dotWidth")
            val color by animateColorAsState(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                label = "dotColor",
            )
            Box(
                Modifier
                    .size(width = width, height = 8.dp)
                    .background(color, CircleShape),
            )
        }
    }
}
