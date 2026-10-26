package com.hundredwidgets.app.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.WidgetPreview
import com.hundredwidgets.app.ui.info
import com.hundredwidgets.core.model.DataSource
import com.hundredwidgets.widget.designs.WidgetDesign

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
    var pinOutcome by remember { mutableStateOf<PinOutcome?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TextButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) {
            Text(stringResource(R.string.action_back))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(design.theme.info.wallpaper),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            WidgetPreview(
                design = design,
                clockFormat = clockFormat,
                scale = if (design.size.columns == 4) 0.85f else 1f,
            )
        }
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(design.title), style = MaterialTheme.typography.headlineSmall)
            Text(
                "${design.theme.displayName} · ${design.size.label}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                stringResource(updateNote(design)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { pinOutcome = viewModel.addToHomeScreen() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.detail_add))
            }
            OutlinedButton(
                onClick = viewModel::toggleFavorite,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(if (isFavorite) R.string.detail_saved else R.string.detail_save))
            }
        }
    }

    when (pinOutcome) {
        PinOutcome.REQUESTED -> InfoDialog(
            title = R.string.detail_pin_requested_title,
            body = R.string.detail_pin_requested_body,
            onDismiss = { pinOutcome = null },
        )
        PinOutcome.UNSUPPORTED -> InfoDialog(
            title = R.string.detail_pin_manual_title,
            body = R.string.detail_pin_manual_body,
            onDismiss = { pinOutcome = null },
        )
        null -> Unit
    }
}

private fun updateNote(design: WidgetDesign): Int = when {
    DataSource.BATTERY in design.needs -> R.string.detail_note_battery
    design.needs.isEmpty() -> R.string.detail_note_live
    else -> R.string.detail_note_daily
}

@Composable
private fun InfoDialog(title: Int, body: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_ok)) } },
        title = { Text(stringResource(title)) },
        text = { Text(stringResource(body)) },
    )
}
