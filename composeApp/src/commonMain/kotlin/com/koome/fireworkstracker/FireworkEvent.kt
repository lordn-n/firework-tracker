package com.koome.fireworkstracker

data class FireworkEvent(
    val detectionTime: Long,
    val volumeLevel: Int,
    val latitude: Double,
    val longitude: Double,
    val notes: String
)
