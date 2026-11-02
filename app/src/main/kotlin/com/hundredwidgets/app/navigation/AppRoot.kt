package com.hundredwidgets.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
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
import com.hundredwidgets.app.ui.motion.LocalReducedMotion
import com.hundredwidgets.app.ui.motion.LocalSharedTransitionScope
import com.hundredwidgets.app.ui.motion.Motion
import com.hundredwidgets.app.ui.motion.ProvideNavAnimatedScope
import com.hundredwidgets.app.ui.settings.SettingsScreen
import com.hundredwidgets.app.ui.wallpapers.WallpapersScreen
import com.hundredwidgets.app.ui.widgets.DesignDetailScreen
import com.hundredwidgets.app.ui.widgets.WidgetsScreen
import kotlin.math.roundToInt
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

private val tabBarItems = tabs.map { TabBarItem(it.label, it.icon) }

/**
 * Tabs swap with a fade and a short rise from below. The design detail
 * page fades in while its wallpaper backdrop and title morph from the tapped card.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val reduced = LocalReducedMotion.current
    // Pages opened from a tab (like design detail) keep that tab highlighted.
    val currentTab = tabs.indexOfFirst { currentDestination.isInHierarchy(it.routeClass) }
    var lastTab by rememberSaveable { mutableIntStateOf(0) }
    SideEffect { if (currentTab >= 0) lastTab = currentTab }
    val selectedTab = if (currentTab >= 0) currentTab else lastTab

    Scaffold(
        bottomBar = {
            TabBar(
                items = tabBarItems,
                selected = selectedTab,
                onSelect = { index -> navController.navigateToTab(tabs[index].route) },
            )
        },
    ) { padding ->
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                NavHost(
                    navController = navController,
                    startDestination = HomeRoute,
                    modifier = Modifier.padding(padding),
                    enterTransition = { if (reduced) EnterTransition.None else tabEnter(initialState, targetState) },
                    exitTransition = { if (reduced) ExitTransition.None else tabExit() },
                    popEnterTransition = { if (reduced) EnterTransition.None else tabEnter(initialState, targetState) },
                    popExitTransition = { if (reduced) ExitTransition.None else tabExit() },
                ) {
                    composable<HomeRoute> {
                        ProvideNavAnimatedScope(this) {
                            HomeScreen(
                                onOpenTheme = { theme ->
                                    navController.navigateToTab(WidgetsRoute(theme.id), restoreState = false)
                                },
                                onOpenDesign = { designId ->
                                    navController.navigate(DesignDetailRoute(designId.value))
                                },
                            )
                        }
                    }
                    composable<WidgetsRoute> { entry ->
                        ProvideNavAnimatedScope(this) {
                            WidgetsScreen(
                                initialThemeId = entry.toRoute<WidgetsRoute>().themeId,
                                onOpenDesign = { designId ->
                                    navController.navigate(DesignDetailRoute(designId.value))
                                },
                            )
                        }
                    }
                    composable<WallpapersRoute> {
                        ProvideNavAnimatedScope(this) { WallpapersScreen() }
                    }
                    composable<SettingsRoute> {
                        ProvideNavAnimatedScope(this) { SettingsScreen() }
                    }
                    composable<DesignDetailRoute>(
                        enterTransition = { if (reduced) EnterTransition.None else fadeIn(tween(Motion.MEDIUM)) },
                        exitTransition = { if (reduced) ExitTransition.None else fadeOut(tween(Motion.SHORT)) },
                        popEnterTransition = { if (reduced) EnterTransition.None else fadeIn(tween(Motion.MEDIUM)) },
                        popExitTransition = { if (reduced) ExitTransition.None else fadeOut(tween(Motion.MEDIUM)) },
                    ) {
                        ProvideNavAnimatedScope(this) {
                            DesignDetailScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}

private fun NavBackStackEntry.isTab(): Boolean = tabs.any { destination.isInHierarchy(it.routeClass) }

/**
 * Switching tabs: the new screen fades in while rising a short way (4 % of its height, about
 * 30 dp) and settles softly. Coming back from a detail page only fades, so its hero morph reads
 * cleanly.
 */
private fun tabEnter(from: NavBackStackEntry, to: NavBackStackEntry): EnterTransition {
    val fade = fadeIn(tween(Motion.MEDIUM, delayMillis = Motion.SHORT / 2, easing = Motion.EmphasizedDecelerate))
    if (!from.isTab() || !to.isTab()) return fade
    return fade + slideInVertically(tween(Motion.LONG, delayMillis = Motion.SHORT / 2, easing = Motion.EmphasizedDecelerate)) {
        (it * 0.04f).roundToInt()
    }
}

/** The old screen just fades out quickly, so two screens never move against each other. */
private fun tabExit(): ExitTransition = fadeOut(tween(Motion.SHORT, easing = Motion.EmphasizedAccelerate))

private fun NavDestination?.isInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true

private fun NavController.navigateToTab(route: Any, restoreState: Boolean = true) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = restoreState }
        launchSingleTop = true
        this.restoreState = restoreState
    }
}
