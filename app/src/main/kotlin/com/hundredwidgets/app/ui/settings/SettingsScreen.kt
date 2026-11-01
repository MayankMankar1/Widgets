package com.hundredwidgets.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.BuildConfig
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.ScreenTitle
import com.hundredwidgets.app.ui.components.SectionCard
import com.hundredwidgets.app.ui.motion.enterOnce
import com.hundredwidgets.core.data.REFRESH_INTERVAL_OPTIONS
import com.hundredwidgets.core.model.ClockFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val clockFormat by viewModel.clockFormat.collectAsStateWithLifecycle()
    val refreshInterval by viewModel.refreshIntervalMinutes.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    val seen = remember { mutableSetOf<Any>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenTitle(text = stringResource(R.string.tab_settings))

        SectionCard(Modifier.enterOnce(index = 0, key = "clock", seen = seen)) {
            Text(stringResource(R.string.settings_clock_format), style = MaterialTheme.typography.titleSmall)
            ClockFormat.entries.forEach { format ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .selectable(
                            selected = format == clockFormat,
                            onClick = {
                                if (format != clockFormat) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                viewModel.setClockFormat(format)
                            },
                            role = Role.RadioButton,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = format == clockFormat, onClick = null)
                    Text(stringResource(format.label()), modifier = Modifier.padding(start = 12.dp))
                }
            }
        }

        SectionCard(Modifier.enterOnce(index = 1, key = "refresh", seen = seen)) {
            Text(stringResource(R.string.settings_refresh), style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(R.string.settings_refresh_body),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                REFRESH_INTERVAL_OPTIONS.forEach { minutes ->
                    FilterChip(
                        selected = minutes == refreshInterval,
                        onClick = {
                            if (minutes != refreshInterval) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            viewModel.setRefreshIntervalMinutes(minutes)
                        },
                        label = { Text(stringResource(R.string.settings_minutes, minutes)) },
                    )
                }
            }
        }

        SectionCard(Modifier.enterOnce(index = 2, key = "battery", seen = seen)) {
            Text(stringResource(R.string.settings_battery_title), style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(R.string.settings_battery_body),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        SectionCard(
            Modifier
                .padding(bottom = 24.dp)
                .enterOnce(index = 3, key = "about", seen = seen),
        ) {
            Text(stringResource(R.string.settings_about), style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun ClockFormat.label(): Int = when (this) {
    ClockFormat.SYSTEM -> R.string.settings_clock_system
    ClockFormat.H12 -> R.string.settings_clock_12
    ClockFormat.H24 -> R.string.settings_clock_24
}
