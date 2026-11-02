package com.hundredwidgets.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hundredwidgets.app.ui.motion.LocalReducedMotion
import com.hundredwidgets.app.ui.motion.Motion
import com.hundredwidgets.app.ui.theme.IconSize
import com.hundredwidgets.app.ui.theme.Spacing

/** What the tab bar needs to draw one tab. */
data class TabBarItem(@StringRes val label: Int, @DrawableRes val icon: Int)

/**
 * A floating pill-shaped tab bar. The selected tab grows into a tinted pill that shows its
 * label; the others show only their icon, so the bar stays calm. No haptics: the moving pill
 * is the feedback.
 */
@Composable
fun TabBar(
    items: List<TabBarItem>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = Spacing.xl, end = Spacing.xl, top = Spacing.sm, bottom = Spacing.md),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .padding(6.dp)
                .selectableGroup(),
        ) {
            items.forEachIndexed { index, item ->
                Tab(item = item, selected = index == selected, onClick = { onSelect(index) })
            }
        }
    }
}

@Composable
private fun Tab(item: TabBarItem, selected: Boolean, onClick: () -> Unit) {
    val reduced = LocalReducedMotion.current
    val label = stringResource(item.label)
    val colorSpec = if (reduced) snap<Color>() else tween(Motion.MEDIUM, easing = Motion.Emphasized)
    val container by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        colorSpec,
        label = "tabContainer",
    )
    val content by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        colorSpec,
        label = "tabContent",
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(48.dp)
            .animateContentSize(if (reduced) snap() else Motion.gentle())
            .clip(CircleShape)
            .background(container)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
            )
            .padding(horizontal = Spacing.lg),
    ) {
        Icon(
            painterResource(item.icon),
            contentDescription = if (selected) null else label,
            tint = content,
            modifier = Modifier.size(IconSize.md),
        )
        if (selected) {
            Spacer(Modifier.width(Spacing.sm))
            Text(label, style = MaterialTheme.typography.labelLarge, color = content, maxLines = 1)
        }
    }
}
