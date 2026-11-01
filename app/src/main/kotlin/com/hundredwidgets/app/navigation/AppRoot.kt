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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

/**
 * Tabs swap with a fade and a short slide in the direction of travel. The design detail
 * page fades in while its wallpaper backdrop and title morph from the tapped card.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val haptics = LocalHapticFeedback.current
    val reduced = LocalReducedMotion.current
    // Pages opened from a tab (like design detail) keep that tab highlighted.
    val currentTab = tabs.indexOfFirst { currentDestination.isInHierarchy(it.routeClass) }
    var lastTab by rememberSaveable { mutableIntStateOf(0) }
    SideEffect { if (currentTab >= 0) lastTab = currentTab }
    val selectedTab = if (currentTab >= 0) currentTab else lastTab

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 0.dp,
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = index == selectedTab
                    val iconScale by animateFloatAsState(
                        targetValue = if (selected && !reduced) 1.08f else 1f,
                        animationSpec = Motion.gentle(),
                        label = "tabIcon",
                    )
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            navController.navigateToTab(tab.route)
                        },
                        icon = {
                            Icon(
                                painterResource(tab.icon),
                                contentDescription = null,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = iconScale
                                    scaleY = iconScale
                                },
                            )
                        },
                        label = { Text(stringResource(tab.label), style = MaterialTheme.typography.labelMedium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                NavHost(
                    navController = navController,
                    startDestination = HomeRoute,
                    modifier = Modifier.padding(padding),
                    enterTransition = {
                        if (reduced) EnterTransition.None else tabEnter(direction(initialState, targetState))
                    },
                    exitTransition = {
                        if (reduced) ExitTransition.None else tabExit(direction(initialState, targetState))
                    },
                    popEnterTransition = {
                        if (reduced) EnterTransition.None else tabEnter(direction(initialState, targetState))
                    },
                    popExitTransition = {
                        if (reduced) ExitTransition.None else tabExit(direction(initialState, targetState))
                    },
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

/** +1 when moving to a tab on the right, -1 to the left, 0 otherwise (detail pages fade only). */
private fun direction(from: NavBackStackEntry, to: NavBackStackEntry): Int {
    val a = tabs.indexOfFirst { from.destination.isInHierarchy(it.routeClass) }
    val b = tabs.indexOfFirst { to.destination.isInHierarchy(it.routeClass) }
    return if (a < 0 || b < 0 || a == b) 0 else if (b > a) 1 else -1
}

/** Tabs: a fade with a short slide (about 1/12 of the width) in the direction of travel. */
private fun tabEnter(direction: Int): EnterTransition =
    fadeIn(tween(Motion.MEDIUM, delayMillis = Motion.SHORT / 3, easing = Motion.EmphasizedDecelerate)) +
        slideInHorizontally(tween(Motion.MEDIUM, easing = Motion.EmphasizedDecelerate)) { it / 12 * direction }

private fun tabExit(direction: Int): ExitTransition =
    fadeOut(tween(Motion.SHORT, easing = Motion.EmphasizedAccelerate)) +
        slideOutHorizontally(tween(Motion.MEDIUM, easing = Motion.EmphasizedAccelerate)) { -it / 12 * direction }

private fun NavDestination?.isInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true

private fun NavController.navigateToTab(route: Any, restoreState: Boolean = true) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = restoreState }
        launchSingleTop = true
        this.restoreState = restoreState
    }
}
