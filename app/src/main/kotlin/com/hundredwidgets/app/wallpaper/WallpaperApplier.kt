package com.hundredwidgets.app.wallpaper

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import androidx.annotation.DrawableRes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class WallpaperTarget { HOME, LOCK, BOTH }

/** Sets a bundled wallpaper. Online wallpapers (R2 catalogue, README §13) come later. */
class WallpaperApplier @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    // wp_* are WebP files in drawable-nodpi, so openRawResource streams the original
    // full-resolution bytes without decoding a bitmap here. They must stay bitmap files:
    // an XML drawable would come back as compiled XML.
    @SuppressLint("ResourceType")
    suspend fun setWallpaper(@DrawableRes resId: Int, target: WallpaperTarget) {
        val which = when (target) {
            WallpaperTarget.HOME -> WallpaperManager.FLAG_SYSTEM
            WallpaperTarget.LOCK -> WallpaperManager.FLAG_LOCK
            WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
        }
        withContext(Dispatchers.IO) {
            context.resources.openRawResource(resId).use { stream ->
                WallpaperManager.getInstance(context).setStream(stream, null, true, which)
            }
        }
    }
}
