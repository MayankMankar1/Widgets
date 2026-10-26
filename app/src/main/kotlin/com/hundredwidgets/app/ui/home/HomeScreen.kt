package com.hundredwidgets.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.ScreenTitle
import com.hundredwidgets.app.ui.components.SectionCard
import com.hundredwidgets.app.ui.components.SectionTitle
import com.hundredwidgets.app.ui.info
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.WidgetRegistry

@Composable
fun HomeScreen(
    onOpenTheme: (Theme) -> Unit,
    onOpenDesign: (DesignId) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val placed by viewModel.placed.collectAsStateWithLifecycle()

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
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(Theme.entries) { theme ->
                    SceneCard(theme = theme, onClick = { onOpenTheme(theme) })
                }
            }
        }
        item { SectionTitle(stringResource(R.string.home_your_widgets)) }
        if (placed.isEmpty()) {
            item {
                SectionCard {
                    Text(
                        stringResource(R.string.home_no_widgets),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            items(placed, key = { it.appWidgetId }) { widget ->
                SectionCard(Modifier.clickable { onOpenDesign(widget.design.id) }) {
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
            SectionCard {
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

@Composable
private fun SceneCard(theme: Theme, onClick: () -> Unit) {
    val designCount = WidgetRegistry.forTheme(theme).size
    Box(
        modifier = Modifier
            .size(width = 150.dp, height = 270.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(theme.info.wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC000000))))
                .padding(14.dp),
        ) {
            Text(theme.displayName, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(
                stringResource(theme.info.tagline),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xCCFFFFFF),
            )
            Text(
                pluralStringResource(R.plurals.home_design_count, designCount, designCount),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xCCFFFFFF),
            )
        }
    }
}
