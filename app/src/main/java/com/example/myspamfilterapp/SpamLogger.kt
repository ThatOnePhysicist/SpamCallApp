package com.example.myspamfilterapp

import android.util.Log

object SpamLogger {
    private val logList = mutableListOf<String>()

    // Add a new entry
    fun logNumber(number: String) {
        logList.add(number)
    }

    // Get a copy of current entries (for Compose)
    fun getLog(): List<String> {
        return logList.toList()
    }
}

