package com.koome.fireworkstracker

import androidx.compose.runtime.Composable

interface PermissionHandler {
    @Composable
    fun askForLocationPermission(onPermissionGranted: @Composable () -> Unit)
}

@Composable
expect fun rememberPermissionHandler(): PermissionHandler
