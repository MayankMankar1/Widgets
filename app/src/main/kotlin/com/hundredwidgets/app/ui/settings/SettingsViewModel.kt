package com.hundredwidgets.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hundredwidgets.core.data.DEFAULT_REFRESH_MINUTES
import com.hundredwidgets.core.data.SettingsRepository
import com.hundredwidgets.core.model.AppTheme
import com.hundredwidgets.core.model.ClockFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Widgets pick up changes through WidgetRefreshCoordinator, which watches these settings. */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsRepository,
) : ViewModel() {

    val clockFormat: StateFlow<ClockFormat> = settings.clockFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClockFormat.SYSTEM)

    val refreshIntervalMinutes: StateFlow<Int> = settings.refreshIntervalMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DEFAULT_REFRESH_MINUTES)

    val appTheme: StateFlow<AppTheme> = settings.appTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppTheme.SYSTEM)

    fun setAppTheme(theme: AppTheme) {
        viewModelScope.launch { settings.setAppTheme(theme) }
    }

    fun setClockFormat(format: ClockFormat) {
        viewModelScope.launch { settings.setClockFormat(format) }
    }

    fun setRefreshIntervalMinutes(minutes: Int) {
        viewModelScope.launch { settings.setRefreshIntervalMinutes(minutes) }
    }
}
