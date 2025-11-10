package com.koome.fireworkstracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
class AndroidPermissionHandler : PermissionHandler {

    @Composable
    override fun askForLocationPermission(onPermissionGranted: @Composable () -> Unit) {
        val locationPermissionsState = rememberMultiplePermissionsState(
            listOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

        if (locationPermissionsState.allPermissionsGranted) {
            onPermissionGranted()
        } else {
            LaunchedEffect(locationPermissionsState) {
                locationPermissionsState.launchMultiplePermissionRequest()
            }
        }
    }
}

@Composable
actual fun rememberPermissionHandler(): PermissionHandler {
    return AndroidPermissionHandler()
}
