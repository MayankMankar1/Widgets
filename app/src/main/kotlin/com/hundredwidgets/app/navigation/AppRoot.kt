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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
 * Tabs swap with a quick fade-through (out, then in with a slight zoom). The design detail
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
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    val selected = index == selectedTab
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            navController.navigateToTab(tab.route)
                        },
                        icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                        label = { Text(stringResource(tab.label)) },
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
                    enterTransition = { if (reduced) EnterTransition.None else fadeThroughIn() },
                    exitTransition = { if (reduced) ExitTransition.None else fadeThroughOut() },
                    popEnterTransition = { if (reduced) EnterTransition.None else fadeThroughIn() },
                    popExitTransition = { if (reduced) ExitTransition.None else fadeThroughOut() },
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

private fun fadeThroughIn(): EnterTransition =
    fadeIn(tween(Motion.MEDIUM, delayMillis = Motion.SHORT / 2, easing = Motion.EmphasizedDecelerate)) +
        scaleIn(tween(Motion.MEDIUM, delayMillis = Motion.SHORT / 2, easing = Motion.EmphasizedDecelerate), initialScale = 0.96f)

private fun fadeThroughOut(): ExitTransition =
    fadeOut(tween(Motion.SHORT, easing = Motion.EmphasizedAccelerate)) +
        scaleOut(tween(Motion.SHORT, easing = Motion.EmphasizedAccelerate), targetScale = 1.02f)

private fun NavDestination?.isInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true

private fun NavController.navigateToTab(route: Any, restoreState: Boolean = true) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = restoreState }
        launchSingleTop = true
        this.restoreState = restoreState
    }
}
