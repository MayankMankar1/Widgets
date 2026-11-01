package com.hundredwidgets.app.ui.widgets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.DesignBackdrop
import com.hundredwidgets.app.ui.components.ScreenTitle
import com.hundredwidgets.app.ui.components.WidgetPreview
import com.hundredwidgets.app.ui.components.previewSize
import com.hundredwidgets.app.ui.motion.HeroKeys
import com.hundredwidgets.app.ui.motion.Motion
import com.hundredwidgets.app.ui.motion.enterOnce
import com.hundredwidgets.app.ui.motion.pressable
import com.hundredwidgets.app.ui.motion.sharedHero
import com.hundredwidgets.core.model.ClockFormat
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.WidgetDesign
import com.hundredwidgets.widget.designs.WidgetRegistry

@Composable
fun WidgetsScreen(
    initialThemeId: String?,
    onOpenDesign: (DesignId) -> Unit,
    viewModel: WidgetsViewModel = hiltViewModel(),
) {
    val clockFormat by viewModel.clockFormat.collectAsStateWithLifecycle()
    var selectedThemeId by rememberSaveable(initialThemeId) { mutableStateOf(initialThemeId) }
    val designs = remember(selectedThemeId) {
        selectedThemeId?.let { Theme.fromId(it) }?.let(WidgetRegistry::forTheme) ?: WidgetRegistry.all
    }
    val haptics = LocalHapticFeedback.current
    val seen = remember { mutableSetOf<Any>() }
    val select: (String?) -> Unit = { id ->
        if (id != selectedThemeId) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
        selectedThemeId = id
    }

    Column(Modifier.fillMaxSize()) {
        ScreenTitle(text = stringResource(R.string.tab_widgets))
        // The count rolls to its new value when a filter changes.
        AnimatedContent(
            targetState = designs.size,
            transitionSpec = {
                val up = targetState > initialState
                (slideInVertically { if (up) it else -it } + fadeIn()) togetherWith
                    (slideOutVertically { if (up) -it else it } + fadeOut())
            },
            label = "designCount",
            modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
        ) { count ->
            Text(
                pluralStringResource(R.plurals.home_design_count, count, count),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        // Opening a scene preselects its theme; keep that chip in view instead of off-screen.
        val chipState = rememberLazyListState()
        val selectedChip = selectedThemeId?.let { id -> Theme.entries.indexOfFirst { it.id == id } + 1 } ?: 0
        LaunchedEffect(selectedChip) { chipState.animateScrollToItem(selectedChip) }
        LazyRow(
            state = chipState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "all") {
                ThemeChip(
                    label = stringResource(R.string.widgets_filter_all),
                    selected = selectedThemeId == null,
                    onClick = { select(null) },
                )
            }
            items(Theme.entries, key = { it.id }) { theme ->
                ThemeChip(
                    label = theme.displayName,
                    selected = selectedThemeId == theme.id,
                    onClick = { select(theme.id) },
                )
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(designs, key = { it.id.value }) { design ->
                DesignCard(
                    design = design,
                    clockFormat = clockFormat,
                    onClick = { onOpenDesign(design.id) },
                    modifier = Modifier
                        .animateItem(
                            fadeInSpec = tween(Motion.MEDIUM),
                            placementSpec = Motion.gentle(),
                            fadeOutSpec = tween(Motion.SHORT),
                        )
                        .enterOnce(index = designs.indexOf(design), key = design.id.value, seen = seen),
                )
            }
        }
    }
}

@Composable
private fun ThemeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) {
            { Icon(painterResource(R.drawable.ic_check), contentDescription = null, Modifier.size(18.dp)) }
        } else {
            null
        },
        modifier = Modifier.animateContentSize(Motion.gentle()),
    )
}

@Composable
private fun DesignCard(
    design: WidgetDesign,
    clockFormat: ClockFormat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(28.dp)
    val title = stringResource(design.title)
    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .pressable(onClickLabel = stringResource(R.string.action_open), onClick = onClick),
    ) {
        Column {
            DesignBackdrop(
                design = design,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(design.size.previewSize().height * cardScale(design) + 48.dp),
            ) {
                WidgetPreview(design = design, clockFormat = clockFormat, scale = cardScale(design))
            }
            Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.sharedHero(HeroKeys.designTitle(design.id.value)),
                )
                Text(
                    "${design.theme.displayName} · ${design.size.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun cardScale(design: WidgetDesign): Float = if (design.size.columns == 4) 0.75f else 0.8f
