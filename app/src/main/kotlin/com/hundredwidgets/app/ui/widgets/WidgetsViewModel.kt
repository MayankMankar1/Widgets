package com.hundredwidgets.app.ui.widgets

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hundredwidgets.app.navigation.DesignDetailRoute
import com.hundredwidgets.core.data.FavoritesRepository
import com.hundredwidgets.core.data.SettingsRepository
import com.hundredwidgets.core.model.ClockFormat
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.widget.designs.WidgetDesign
import com.hundredwidgets.widget.designs.WidgetRegistry
import com.hundredwidgets.widget.engine.WidgetPinner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetsViewModel @Inject constructor(
    settings: SettingsRepository,
) : ViewModel() {

    val clockFormat: StateFlow<ClockFormat> = settings.clockFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClockFormat.SYSTEM)
}

enum class PinOutcome { REQUESTED, UNSUPPORTED }

@HiltViewModel
class DesignDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    settings: SettingsRepository,
    private val favorites: FavoritesRepository,
    private val pinner: WidgetPinner,
) : ViewModel() {

    val design: WidgetDesign? =
        WidgetRegistry.find(DesignId(savedStateHandle.toRoute<DesignDetailRoute>().designId))

    val clockFormat: StateFlow<ClockFormat> = settings.clockFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClockFormat.SYSTEM)

    val isFavorite: StateFlow<Boolean> = favorites.favorites
        .map { ids -> design != null && design.id in ids }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun toggleFavorite() {
        val current = design ?: return
        viewModelScope.launch { favorites.toggle(current.id) }
    }

    fun addToHomeScreen(): PinOutcome {
        val current = design ?: return PinOutcome.UNSUPPORTED
        return if (pinner.pin(current)) PinOutcome.REQUESTED else PinOutcome.UNSUPPORTED
    }
}
