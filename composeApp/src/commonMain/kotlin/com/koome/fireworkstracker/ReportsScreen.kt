package com.koome.fireworkstracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator

@Composable
fun ReportsScreen() {
    var events by remember {
        mutableStateOf(
            listOf(
                FireworkEvent(
                    detectionTime = System.currentTimeMillis(),
                    volumeLevel = 85,
                    latitude = 34.0522,
                    longitude = -118.2437,
                    notes = "Loud ones tonight!"
                ),
                FireworkEvent(
                    detectionTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
                    volumeLevel = 60,
                    latitude = 34.0522,
                    longitude = -118.2437,
                    notes = "Smaller ones"
                ),
                FireworkEvent(
                    detectionTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2,
                    volumeLevel = 95,
                    latitude = 34.0522,
                    longitude = -118.2437,
                    notes = "Very loud"
                )
            )
        )
    }
    var showDialog by remember { mutableStateOf<FireworkEvent?>(null) }
    val tabNavigator = LocalTabNavigator.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF303030)
    ) {
        if (events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No events", color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { tabNavigator.current = ReportTab },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1F2937),
                            contentColor = Color(0xFFF15A21)
                        )
                    ) {
                        Text("New Report")
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(events) { event ->
                    FireworkEventItem(event = event) {
                        showDialog = event
                    }
                }
            }
        }
    }

    showDialog?.let { event ->
        AlertDialog(
            onDismissRequest = { showDialog = null },
            title = { Text("Delete Report") },
            text = { Text("Are you sure you want to delete this report?") },
            confirmButton = {
                Button(
                    onClick = {
                        events = events.filter { it != event }
                        showDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937))
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1F2937),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}
