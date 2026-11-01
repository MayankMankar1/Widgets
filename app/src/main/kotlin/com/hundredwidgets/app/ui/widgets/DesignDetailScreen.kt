package com.hundredwidgets.app.ui.widgets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.DesignBackdrop
import com.hundredwidgets.app.ui.components.WidgetPreview
import com.hundredwidgets.app.ui.motion.HeroKeys
import com.hundredwidgets.app.ui.motion.LocalReducedMotion
import com.hundredwidgets.app.ui.motion.Motion
import com.hundredwidgets.app.ui.motion.enterOnce
import com.hundredwidgets.app.ui.motion.sharedHero
import com.hundredwidgets.core.model.ClockFormat
import com.hundredwidgets.core.model.DataSource
import com.hundredwidgets.widget.designs.WidgetDesign
import kotlinx.coroutines.launch

@Composable
fun DesignDetailScreen(
    onBack: () -> Unit,
    viewModel: DesignDetailViewModel = hiltViewModel(),
) {
    val design = viewModel.design
    if (design == null) {
        Column(Modifier.padding(20.dp)) {
            Text(stringResource(R.string.detail_not_found))
            TextButton(onClick = onBack) { Text(stringResource(R.string.action_back)) }
        }
        return
    }
    val clockFormat by viewModel.clockFormat.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    var showAddSheet by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val seen = remember { mutableSetOf<Any>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, end = 12.dp, top = 4.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
            }
            Spacer(Modifier.weight(1f))
            FavoriteButton(
                isFavorite = isFavorite,
                onToggle = {
                    haptics.performHapticFeedback(
                        if (isFavorite) HapticFeedbackType.ToggleOff else HapticFeedbackType.ToggleOn,
                    )
                    viewModel.toggleFavorite()
                },
            )
        }
        DesignBackdrop(
            design = design,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(32.dp)),
        ) {
            WidgetPreview(
                design = design,
                clockFormat = clockFormat,
                scale = if (design.size.columns == 4) 0.85f else 1f,
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                stringResource(design.title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.sharedHero(HeroKeys.designTitle(design.id.value)),
            )
            Text(
                "${design.theme.displayName} · ${design.size.label}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.enterOnce(index = 1, key = "meta", seen = seen),
            )
            Text(
                stringResource(updateNote(design)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.enterOnce(index = 2, key = "note", seen = seen),
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { showAddSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .enterOnce(index = 3, key = "add", seen = seen),
            ) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.detail_add), style = MaterialTheme.typography.titleSmall)
            }
        }
    }

    if (showAddSheet) {
        AddWidgetSheet(
            design = design,
            clockFormat = clockFormat,
            onAdd = viewModel::addToHomeScreen,
            onDismiss = { showAddSheet = false },
        )
    }
}

/** Heart that pops when toggled on; its label tells screen readers what a tap will do. */
@Composable
private fun FavoriteButton(isFavorite: Boolean, onToggle: () -> Unit) {
    val reduced = LocalReducedMotion.current
    val pop = remember { Animatable(1f) }
    LaunchedEffect(isFavorite) {
        if (isFavorite && !reduced && pop.value == 1f) {
            pop.snapTo(0.7f)
            pop.animateTo(1f, Motion.bouncy())
        }
    }
    FilledTonalIconToggleButton(checked = isFavorite, onCheckedChange = { onToggle() }) {
        Icon(
            painterResource(if (isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_outline),
            contentDescription = stringResource(
                if (isFavorite) R.string.detail_favorite_remove else R.string.detail_favorite_add,
            ),
            modifier = Modifier.graphicsLayer {
                scaleX = pop.value
                scaleY = pop.value
            },
        )
    }
}

/**
 * Explains what happens before handing off to the launcher, then confirms with a check mark,
 * so adding a widget never feels like it silently did nothing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWidgetSheet(
    design: WidgetDesign,
    clockFormat: ClockFormat,
    onAdd: () -> PinOutcome,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    var outcome by remember { mutableStateOf<PinOutcome?>(null) }
    val close: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        AnimatedContent(
            targetState = outcome,
            transitionSpec = {
                (fadeIn(tween(Motion.MEDIUM, delayMillis = Motion.SHORT / 2)) +
                    scaleIn(tween(Motion.MEDIUM, delayMillis = Motion.SHORT / 2), initialScale = 0.92f)) togetherWith
                    fadeOut(tween(Motion.SHORT))
            },
            label = "addSheetStep",
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
        ) { step ->
            when (step) {
                null -> Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.detail_sheet_title), style = MaterialTheme.typography.titleLarge)
                    DesignBackdrop(
                        design = design,
                        hero = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(24.dp)),
                    ) {
                        WidgetPreview(design = design, clockFormat = clockFormat, scale = 0.6f)
                    }
                    SheetStep(number = 1, text = stringResource(R.string.detail_sheet_step_confirm))
                    SheetStep(number = 2, text = stringResource(R.string.detail_sheet_step_place))
                    Button(
                        onClick = {
                            val result = onAdd()
                            if (result == PinOutcome.REQUESTED) {
                                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                            }
                            outcome = result
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    ) {
                        Text(stringResource(R.string.detail_sheet_add), style = MaterialTheme.typography.titleSmall)
                    }
                }
                PinOutcome.REQUESTED -> SheetResult(
                    success = true,
                    title = stringResource(R.string.detail_pin_requested_title),
                    body = stringResource(R.string.detail_pin_requested_body),
                    onDone = close,
                )
                PinOutcome.UNSUPPORTED -> SheetResult(
                    success = false,
                    title = stringResource(R.string.detail_pin_manual_title),
                    body = stringResource(R.string.detail_pin_manual_body),
                    onDone = close,
                )
            }
        }
    }
}

@Composable
private fun SheetStep(number: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
        ) {
            Text(number.toString(), style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SheetResult(success: Boolean, title: String, body: String, onDone: () -> Unit) {
    val reduced = LocalReducedMotion.current
    val mark = remember { Animatable(if (reduced) 1f else 0f) }
    LaunchedEffect(Unit) { mark.animateTo(1f, Motion.bouncy()) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (success) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer {
                        scaleX = mark.value
                        scaleY = mark.value
                    }
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
            ) {
                Icon(
                    painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp),
                )
            }
        }
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(stringResource(R.string.action_done))
        }
    }
}

private fun updateNote(design: WidgetDesign): Int = when {
    DataSource.BATTERY in design.needs -> R.string.detail_note_battery
    design.needs.isEmpty() -> R.string.detail_note_live
    else -> R.string.detail_note_daily
}
