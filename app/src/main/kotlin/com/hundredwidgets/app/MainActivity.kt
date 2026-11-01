package com.hundredwidgets.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.hundredwidgets.app.navigation.AppRoot
import com.hundredwidgets.app.ui.motion.LocalReducedMotion
import com.hundredwidgets.app.ui.motion.rememberReducedMotion
import com.hundredwidgets.app.ui.theme.HundredWidgetsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // The app is dark-only, so system bar icons stay light even when the phone is in light mode.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            HundredWidgetsTheme {
                CompositionLocalProvider(LocalReducedMotion provides rememberReducedMotion()) {
                    AppRoot()
                }
            }
        }
    }
}
