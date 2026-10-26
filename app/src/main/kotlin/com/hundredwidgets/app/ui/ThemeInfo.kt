package com.hundredwidgets.app.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hundredwidgets.app.R
import com.hundredwidgets.core.model.Theme

/** App-side presentation of a theme: its bundled wallpaper and one-line description. */
data class ThemeInfo(
    val theme: Theme,
    @DrawableRes val wallpaper: Int,
    @StringRes val tagline: Int,
)

private val themeInfos = listOf(
    ThemeInfo(Theme.HORIZON, R.drawable.wp_horizon, R.string.theme_horizon_tagline),
    ThemeInfo(Theme.NOIR, R.drawable.wp_noir, R.string.theme_noir_tagline),
    ThemeInfo(Theme.BAUHAUS, R.drawable.wp_bauhaus, R.string.theme_bauhaus_tagline),
    ThemeInfo(Theme.COMIC, R.drawable.wp_comic, R.string.theme_comic_tagline),
)

val Theme.info: ThemeInfo get() = themeInfos.first { it.theme == this }
