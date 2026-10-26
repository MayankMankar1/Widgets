package com.hundredwidgets.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hundredwidgets.app.navigation.AppRoot
import com.hundredwidgets.app.ui.theme.HundredWidgetsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            HundredWidgetsTheme {
                AppRoot()
            }
        }
    }
}
