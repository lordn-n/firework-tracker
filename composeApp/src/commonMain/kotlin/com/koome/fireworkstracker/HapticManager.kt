package com.koome.fireworkstracker

import androidx.compose.runtime.staticCompositionLocalOf

interface HapticManager {
    fun vibrateStrong()
}

val LocalHapticManager = staticCompositionLocalOf<HapticManager?> { null }
