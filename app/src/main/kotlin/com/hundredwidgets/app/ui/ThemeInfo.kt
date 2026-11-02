package com.hundredwidgets.app.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hundredwidgets.app.R
import com.hundredwidgets.core.model.Theme

/**
 * App-side presentation of a theme: the image its designs are shown on (Home scene cards,
 * widget previews) and a one-line description. Backdrops are 900×1350 crops of bundled
 * wallpapers, made by tools/wallpapers/bundle.py (BACKDROPS there must match this list).
 */
data class ThemeInfo(
    val theme: Theme,
    @DrawableRes val backdrop: Int,
    @StringRes val tagline: Int,
)

private val themeInfos = listOf(
    ThemeInfo(Theme.HORIZON, R.drawable.wpb_horizon, R.string.theme_horizon_tagline),
    ThemeInfo(Theme.NOIR, R.drawable.wpb_noir, R.string.theme_noir_tagline),
    ThemeInfo(Theme.PAPER, R.drawable.wpb_paper, R.string.theme_paper_tagline),
    ThemeInfo(Theme.SIGNAL, R.drawable.wpb_signal, R.string.theme_signal_tagline),
    ThemeInfo(Theme.BAUHAUS, R.drawable.wpb_bauhaus, R.string.theme_bauhaus_tagline),
    ThemeInfo(Theme.COMIC, R.drawable.wpb_comic, R.string.theme_comic_tagline),
)

val Theme.info: ThemeInfo get() = themeInfos.first { it.theme == this }
