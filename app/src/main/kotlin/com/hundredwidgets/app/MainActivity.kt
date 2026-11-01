package com.hundredwidgets.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.navigation.AppRoot
import com.hundredwidgets.app.ui.motion.LocalReducedMotion
import com.hundredwidgets.app.ui.motion.rememberReducedMotion
import com.hundredwidgets.app.ui.theme.HundredWidgetsTheme
import com.hundredwidgets.core.data.SettingsRepository
import com.hundredwidgets.core.model.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settings: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            // Null until the saved choice is read (a few ms); the window background, which
            // follows the phone's mode, shows meanwhile, so the wrong palette never flashes.
            val choice by settings.appTheme.collectAsStateWithLifecycle(initialValue = null)
            val theme = choice ?: return@setContent
            val dark = when (theme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }
            DisposableEffect(dark) {
                val style = if (dark) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
                onDispose {}
            }
            HundredWidgetsTheme(darkTheme = dark) {
                CompositionLocalProvider(LocalReducedMotion provides rememberReducedMotion()) {
                    AppRoot()
                }
            }
        }
    }
}
