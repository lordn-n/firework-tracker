package com.koome.fireworkstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ContextProvider.init(this)

        setContent {
            val hapticManager = AndroidHapticManager(LocalContext.current)
            CompositionLocalProvider(LocalHapticManager provides hapticManager) {
                MaterialTheme {
                    val systemUiController = rememberSystemUiController()
                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            color = Color.Transparent,
                            darkIcons = false
                        )
                    }
                    Navigator(MainScreen)
                }
            }
        }
    }
}

class PreviewHapticManager : HapticManager {
    override fun vibrateStrong() {}
}

@Preview
@Composable
fun AppAndroidPreview() {
    CompositionLocalProvider(LocalHapticManager provides PreviewHapticManager()) {
        MaterialTheme {
            Navigator(MainScreen)
        }
    }
}
