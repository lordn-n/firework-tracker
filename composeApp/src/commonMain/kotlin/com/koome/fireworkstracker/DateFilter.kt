package com.koome.fireworkstracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DateFilter(onFilterChanged: (LongRange) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { /* TODO: Implement last week filter */ }) {
            Text("Last Week")
        }
        Button(onClick = { /* TODO: Implement last month filter */ }) {
            Text("Last Month")
        }
        Button(onClick = { /* TODO: Implement a year ago filter */ }) {
            Text("A Year Ago")
        }
    }
}
