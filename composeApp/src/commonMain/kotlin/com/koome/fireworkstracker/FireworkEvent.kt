package com.koome.fireworkstracker

data class FireworkEvent(
    val id: String? = null,
    val userId: String? = null,
    val occurredAt: Long,
    val volume: Int,
    val notes: String?,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val source: String = "app"
)
