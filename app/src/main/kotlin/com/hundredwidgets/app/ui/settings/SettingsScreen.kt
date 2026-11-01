package com.hundredwidgets.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.BuildConfig
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.ScreenTitle
import com.hundredwidgets.app.ui.components.SegmentedControl
import com.hundredwidgets.app.ui.components.SettingsGroup
import com.hundredwidgets.app.ui.components.SettingsItem
import com.hundredwidgets.app.ui.motion.enterOnce
import com.hundredwidgets.app.ui.theme.Spacing
import com.hundredwidgets.core.data.REFRESH_INTERVAL_OPTIONS
import com.hundredwidgets.core.model.AppTheme
import com.hundredwidgets.core.model.ClockFormat

/** Settings grouped by what they affect: the app's look, the widgets, help, and about. */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
    val clockFormat by viewModel.clockFormat.collectAsStateWithLifecycle()
    val refreshInterval by viewModel.refreshIntervalMinutes.collectAsStateWithLifecycle()
    val seen = remember { mutableSetOf<Any>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.section),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxl),
    ) {
        ScreenTitle(text = stringResource(R.string.tab_settings))

        SettingsGroup(stringResource(R.string.settings_group_appearance), Modifier.enterOnce(0, "appearance", seen)) {
            SettingsItem(
                icon = R.drawable.ic_set_theme,
                title = stringResource(R.string.settings_theme),
                subtitle = stringResource(R.string.settings_theme_body),
            ) {
                SegmentedControl(
                    options = AppTheme.entries.toList(),
                    selected = appTheme,
                    label = { stringResource(it.label()) },
                    onSelect = viewModel::setAppTheme,
                )
            }
        }

        SettingsGroup(stringResource(R.string.settings_group_widgets), Modifier.enterOnce(1, "widgets", seen)) {
            SettingsItem(
                icon = R.drawable.ic_set_clock,
                title = stringResource(R.string.settings_clock_format),
            ) {
                SegmentedControl(
                    options = ClockFormat.entries.toList(),
                    selected = clockFormat,
                    label = { stringResource(it.label()) },
                    onSelect = viewModel::setClockFormat,
                )
            }
            SettingsItem(
                icon = R.drawable.ic_set_refresh,
                title = stringResource(R.string.settings_refresh),
                subtitle = stringResource(R.string.settings_refresh_body),
            ) {
                SegmentedControl(
                    options = REFRESH_INTERVAL_OPTIONS,
                    selected = refreshInterval,
                    label = { stringResource(R.string.settings_minutes, it) },
                    onSelect = viewModel::setRefreshIntervalMinutes,
                )
            }
        }

        SettingsGroup(stringResource(R.string.settings_group_help), Modifier.enterOnce(2, "help", seen)) {
            SettingsItem(
                icon = R.drawable.ic_set_battery,
                title = stringResource(R.string.settings_battery_title),
                subtitle = stringResource(R.string.settings_battery_body),
            )
        }

        SettingsGroup(stringResource(R.string.settings_about), Modifier.enterOnce(3, "about", seen)) {
            SettingsItem(
                icon = R.drawable.ic_set_info,
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
            )
        }
    }
}

private fun AppTheme.label(): Int = when (this) {
    AppTheme.SYSTEM -> R.string.settings_theme_system
    AppTheme.LIGHT -> R.string.settings_theme_light
    AppTheme.DARK -> R.string.settings_theme_dark
}

private fun ClockFormat.label(): Int = when (this) {
    ClockFormat.SYSTEM -> R.string.settings_clock_system
    ClockFormat.H12 -> R.string.settings_clock_12
    ClockFormat.H24 -> R.string.settings_clock_24
}
