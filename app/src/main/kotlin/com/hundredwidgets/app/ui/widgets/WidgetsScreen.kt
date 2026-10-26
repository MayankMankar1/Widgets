package com.hundredwidgets.app.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.ScreenTitle
import com.hundredwidgets.app.ui.components.WidgetPreview
import com.hundredwidgets.core.model.ClockFormat
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.WidgetDesign
import com.hundredwidgets.widget.designs.WidgetRegistry

@OptIn(ExperimentalMaterial3Api::class)
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

    Column(Modifier.fillMaxSize()) {
        ScreenTitle(text = stringResource(R.string.tab_widgets))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selectedThemeId == null,
                onClick = { selectedThemeId = null },
                label = { Text(stringResource(R.string.widgets_filter_all)) },
            )
            Theme.entries.forEach { theme ->
                FilterChip(
                    selected = selectedThemeId == theme.id,
                    onClick = { selectedThemeId = theme.id },
                    label = { Text(theme.displayName) },
                )
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(designs, key = { it.id.value }) { design ->
                DesignCard(design, clockFormat, onClick = { onOpenDesign(design.id) })
            }
        }
    }
}

@Composable
private fun DesignCard(design: WidgetDesign, clockFormat: ClockFormat, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WidgetPreview(
                design = design,
                clockFormat = clockFormat,
                scale = if (design.size.columns == 4) 0.75f else 0.85f,
            )
            Spacer(Modifier.height(12.dp))
            Text(stringResource(design.title), style = MaterialTheme.typography.titleSmall)
            Text(
                "${design.theme.displayName} · ${design.size.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
