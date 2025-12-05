package com.example.myspamfilterapp

object SpamCallTracker {

    // Store last blocked call for debugging or UI display
    var lastBlockedCall: String? = null

    // Optional: central spam check, if you want the service to use it later
    private val spamNumbers = setOf(
//        "19252898473"
        "+18005551234"
    )

    fun isSpam(number: String): Boolean {
        val digits = number.filter { it.isDigit() }
        return spamNumbers.contains(digits)
    }
}
