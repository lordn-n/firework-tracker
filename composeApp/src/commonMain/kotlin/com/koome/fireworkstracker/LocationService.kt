package com.koome.fireworkstracker

data class Location(
    val latitude: Double,
    val longitude: Double
)

interface LocationService {
    suspend fun getCurrentLocation(): Location?
}

expect fun getLocationService(): LocationService
