package com.hundredwidgets.app.ui.wallpapers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hundredwidgets.app.ui.info
import com.hundredwidgets.app.wallpaper.WallpaperApplier
import com.hundredwidgets.app.wallpaper.WallpaperTarget
import com.hundredwidgets.core.model.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class WallpaperStatus { APPLYING, DONE, FAILED }

@HiltViewModel
class WallpapersViewModel @Inject constructor(
    private val applier: WallpaperApplier,
) : ViewModel() {

    private val _status = MutableStateFlow<WallpaperStatus?>(null)
    val status: StateFlow<WallpaperStatus?> = _status.asStateFlow()

    fun setWallpaper(theme: Theme, target: WallpaperTarget) {
        viewModelScope.launch {
            _status.value = WallpaperStatus.APPLYING
            val result = runCatching { applier.setWallpaper(theme.info.wallpaper, target) }
            _status.value = if (result.isSuccess) WallpaperStatus.DONE else WallpaperStatus.FAILED
        }
    }

    fun statusShown() {
        _status.value = null
    }
}
