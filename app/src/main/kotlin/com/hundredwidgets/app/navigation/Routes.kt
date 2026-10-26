package com.hundredwidgets.app.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class WidgetsRoute(val themeId: String? = null)

@Serializable
data object WallpapersRoute

@Serializable
data object SettingsRoute

@Serializable
data class DesignDetailRoute(val designId: String)
