package com.hundredwidgets.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.home.HomeScreen
import com.hundredwidgets.app.ui.settings.SettingsScreen
import com.hundredwidgets.app.ui.wallpapers.WallpapersScreen
import com.hundredwidgets.app.ui.widgets.DesignDetailScreen
import com.hundredwidgets.app.ui.widgets.WidgetsScreen
import kotlin.reflect.KClass

private data class TopLevelTab(
    val route: Any,
    val routeClass: KClass<*>,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
)

private val tabs = listOf(
    TopLevelTab(HomeRoute, HomeRoute::class, R.string.tab_home, R.drawable.ic_tab_home),
    TopLevelTab(WidgetsRoute(), WidgetsRoute::class, R.string.tab_widgets, R.drawable.ic_tab_widgets),
    TopLevelTab(WallpapersRoute, WallpapersRoute::class, R.string.tab_wallpapers, R.drawable.ic_tab_wallpapers),
    TopLevelTab(SettingsRoute, SettingsRoute::class, R.string.tab_settings, R.drawable.ic_tab_settings),
)

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination.isInHierarchy(tab.routeClass),
                        onClick = { navController.navigateToTab(tab.route) },
                        icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                        label = { Text(stringResource(tab.label)) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(padding),
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    onOpenTheme = { theme ->
                        navController.navigateToTab(WidgetsRoute(theme.id), restoreState = false)
                    },
                    onOpenDesign = { designId -> navController.navigate(DesignDetailRoute(designId.value)) },
                )
            }
            composable<WidgetsRoute> { entry ->
                WidgetsScreen(
                    initialThemeId = entry.toRoute<WidgetsRoute>().themeId,
                    onOpenDesign = { designId -> navController.navigate(DesignDetailRoute(designId.value)) },
                )
            }
            composable<WallpapersRoute> {
                WallpapersScreen()
            }
            composable<SettingsRoute> {
                SettingsScreen()
            }
            composable<DesignDetailRoute> {
                DesignDetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun NavDestination?.isInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true

private fun NavController.navigateToTab(route: Any, restoreState: Boolean = true) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = restoreState }
        launchSingleTop = true
        this.restoreState = restoreState
    }
}
