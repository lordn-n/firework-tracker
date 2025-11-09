package com.koome.fireworkstracker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun formatTimestamp(timestamp: Long): String
