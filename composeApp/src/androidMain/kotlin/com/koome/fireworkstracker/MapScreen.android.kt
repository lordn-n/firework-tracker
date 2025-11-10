package com.koome.fireworkstracker

import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
actual fun MapScreen() {
    val events by FireworkEventsRepository.events.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        // Default position
        position = CameraPosition.fromLatLngZoom(LatLng(37.4221, -122.0841), 10f)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationService = rememberLocationService()

    LaunchedEffect(Unit) {
        scope.launch {
            val location = locationService.getCurrentLocation()
            location?.let {
                val userLocation = LatLng(it.latitude, it.longitude)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(userLocation, 12f)
                )
            }
        }
    }

    val ai = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    val apiKey = ai.metaData.getString("com.google.android.geo.API_KEY")

    if (apiKey.isNullOrEmpty() || apiKey == "YOUR_KEY_HERE") {
        Log.e("MapScreen", "API Key is missing. Please add it to your AndroidManifest.xml file.")
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("API Key is missing. Please add it to your AndroidManifest.xml file.")
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DateFilter {
            // TODO: Filter events
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            events.forEach { event ->
                // 0 volume = 100m, 100 volume = 5km (5000m)
                val radiusInMeters = 100.0 + (2000.0 - 100.0) / 100.0 * event.volume
                Circle(
                    center = LatLng(event.latitude, event.longitude),
                    radius = radiusInMeters,
                    fillColor = Color.Red.copy(alpha = 0.5f),
                    strokeColor = Color.Black,
                    strokeWidth = 2f
                )
            }
        }
    }
}

// Helper function to remember the location service
@Composable
private fun rememberLocationService(): LocationService {
    val context = LocalContext.current
    return remember(context) {
        AndroidLocationService(context)
    }
}
