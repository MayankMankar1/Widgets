package com.hundredwidgets.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hundredwidgets.core.data.WidgetInstanceRepository
import com.hundredwidgets.widget.designs.WidgetDesign
import com.hundredwidgets.widget.designs.WidgetRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PlacedWidget(val appWidgetId: Int, val design: WidgetDesign)

@HiltViewModel
class HomeViewModel @Inject constructor(
    instances: WidgetInstanceRepository,
) : ViewModel() {

    val placed: StateFlow<List<PlacedWidget>> = instances.instances
        .map { list ->
            list.mapNotNull { instance ->
                WidgetRegistry.find(instance.designId)?.let { PlacedWidget(instance.appWidgetId, it) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
