package com.koome.fireworkstracker

import android.content.Context

object ContextProvider {
    lateinit var context: Context
        private set

    fun init(context: Context) {
        this.context = context.applicationContext
    }
}
