package com.koome.fireworkstracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop

@Composable
fun FireworkTrackerScreen() {
    var rotation by remember { mutableStateOf(135f) } // Initial volume is 0
    val volume = 100 - ((rotation + 135) / 270 * 100).toInt().coerceIn(0, 100)
    val koomeOrange = Color(0xFFF15A21)
    var lastEvent by remember { mutableStateOf<FireworkEvent?>(null) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(haptic) {
        snapshotFlow { volume }
            .drop(1) // Ignore the initial value
            .collect {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF303030),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Firework Tracker",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = koomeOrange
            )
            Text(
                text = "Set the volume, then tap the center to log the event.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF374151))
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                val centerX = size.width / 2
                                val centerY = size.height / 2
                                val angle =
                                    atan2(
                                        change.position.y - centerY,
                                        change.position.x - centerX
                                    ) * (180 / Math.PI).toFloat()
                                rotation = angle.coerceIn(-135f, 135f)
                            }
                        }
                        .rotate(rotation)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = 10.dp)
                            .clip(CircleShape)
                            .background(koomeOrange)
                    )
                }
                IconButton(
                    onClick = {
                        lastEvent = FireworkEvent(
                            detectionTime = System.currentTimeMillis(),
                            volumeLevel = volume,
                            latitude = 0.0, // Placeholder
                            longitude = 0.0, // Placeholder
                            notes = "Putos cohetes..."
                        )
                        rotation = 135f // Reset volume to 0
                    },
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1F2937))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log Firework",
                        tint = koomeOrange,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Volume Level (0-100)",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = "$volume",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(visible = lastEvent != null) {
                lastEvent?.let { event ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1F2937),
                            tonalElevation = 4.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Thanks for your report!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = koomeOrange
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Datetime: ${formatTimestamp(event.detectionTime)}", color = Color.White)
                                Text("Volume: ${event.volumeLevel}", color = Color.White)
                                Text("Location: (${event.latitude}, ${event.longitude})", color = Color.White)
                                Text("Notes: ${event.notes}", color = Color.White)
                            }
                        }
                        IconButton(
                            onClick = { lastEvent = null },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
