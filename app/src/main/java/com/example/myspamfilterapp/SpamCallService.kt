package com.example.myspamfilterapp

import android.content.ContentValues
import android.provider.BlockedNumberContract
import android.telecom.CallScreeningService
import android.telecom.Call
import android.telecom.Call.Details
import android.util.Log
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.myspamfilterapp.data.PreferencesRepository
import kotlinx.coroutines.flow.first
import com.example.myspamfilterapp.data.SpamDatabase
import com.example.myspamfilterapp.data.SpamCall

class SpamCallService : CallScreeningService() {

    private companion object {
        const val EXTRA_CALL_SCREENING_IS_POTENTIAL_SPAM =
            "android.telecom.extra.CALL_SCREENING_IS_POTENTIAL_SPAM"
    }

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onScreenCall(callDetails: Details) {

        // --- Extract caller info ---
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: "Unknown"
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())

        // --- System spam flag (Pixel-only) ---
        val systemSpamFlag =
            callDetails.extras?.getBoolean(EXTRA_CALL_SCREENING_IS_POTENTIAL_SPAM, false)
                ?: false

        // --- Heuristic detection ---
        val heuristicFlag = isSuspiciousNumber(phoneNumber)

        // --- Load user preference (blockSpamCalls: Boolean) ---
        val prefs = PreferencesRepository(applicationContext)
        val blockEnabled: Boolean = runBlocking { prefs.blockSpamCalls.first() }

        // --- Final spam decision considering user preference ---
        val finalSpam = blockEnabled && (systemSpamFlag || heuristicFlag)

        // --- Build log entry ---
        val logEntry = buildString {
            append("$timestamp — $phoneNumber\n")
            append("Block enabled: $blockEnabled\n")
            append("System flag: $systemSpamFlag\n")
            append("Heuristic: $heuristicFlag\n")
            append("Final spam verdict: $finalSpam\n")
            append("RAW DETAILS: $callDetails\n")
            append("---------------------------\n")
        }

        Log.d("SpamCallService", logEntry)

        // --- Save database + file log asynchronously ---
        ioScope.launch {
            val db = SpamDatabase.get(applicationContext)
            val dao = db.spamCallDao()

            val previousEntry = dao.getLastCall(phoneNumber)
            val newCount = (previousEntry?.callCount ?: 0) + 1
            dao.insert(
                SpamCall(
                    phoneNumber = phoneNumber,
                    timestamp = System.currentTimeMillis(),
                    isSystemFlagged = systemSpamFlag,
                    isHeuristicFlagged = heuristicFlag,
                    isFinalSpam = finalSpam,
                    rawDetails = callDetails.toString(),
                    reason = when {
                        systemSpamFlag -> "System flagged"
                        heuristicFlag -> "Heuristic match"
                        else -> "Allowed call"
                    },
                    blocked = finalSpam,
                    callCount = newCount
                )
            )

            SpamLogger.logNumber(logEntry)
        }

        // --- Option B: block + reject + prevent voicemail ---
        if (finalSpam) {
            blockNumber(phoneNumber)

            val response = CallResponse.Builder()
                .setDisallowCall(true)        // Block immediately
                .setRejectCall(true)          // Prevent connection
                .setSkipCallLog(true)         // Do not show in call-log
                .setSkipNotification(true)    // No missed-call notification
                .build()

            respondToCall(callDetails, response)
            return
        }

        // Non-spam call → allow it
        respondToCall(callDetails, CallResponse.Builder().build())
    }

    // --------------------------------------------------------------------
    // BLOCK NUMBER USING SYSTEM BLOCK LIST
    // --------------------------------------------------------------------
    private fun blockNumber(number: String) {
        if (number == "Unknown") return

        try {
            val values = ContentValues().apply {
                put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
            }

            contentResolver.insert(
                BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                values
            )

        } catch (e: SecurityException) {
            Log.e("SpamCallService", "Missing permission: BLOCKED_NUMBERS", e)
        } catch (e: Exception) {
            Log.e("SpamCallService", "Error blocking number", e)
        }
    }

    // --------------------------------------------------------------------
    // HEURISTIC SUSPICIOUS NUMBER DETECTOR
    // --------------------------------------------------------------------
    private fun isSuspiciousNumber(number: String): Boolean {
        // unknown or private numbers
        if (number == "Unknown") return true
        // phone numbers less than 7 digits
        if (number.length < 7) return true
        // repetitive digits
        if (number.toSet().size <= 2) return true

        // toll-free prefix
        val tollFreePrefixes = listOf("800", "833", "844", "855", "866", "877", "888")
        if (tollFreePrefixes.any { number.startsWith(it) }) return true

        return false
    }
}



//package com.example.myspamfilterapp
//
//import android.net.Uri
//import android.provider.BlockedNumberContract
//import android.telecom.CallScreeningService
//import android.telecom.Call
//import android.util.Log
//import kotlinx.coroutines.*
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import android.content.ContentValues
//
//class SpamCallService : CallScreeningService() {
//
//    private companion object {
//        const val EXTRA_CALL_SCREENING_IS_POTENTIAL_SPAM =
//            "android.telecom.extra.CALL_SCREENING_IS_POTENTIAL_SPAM"
//    }
//
//    private val scope = CoroutineScope(Dispatchers.Default)
//
//    override fun onScreenCall(callDetails: Call.Details) {
//
//        scope.launch {
//
//            val phoneNumber = callDetails.handle?.schemeSpecificPart ?: "Unknown"
//            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
//
//            // ------------------------------------------------------------
//            // (1) SYSTEM FLAG — Pixel may provide this (rare)
//            // ------------------------------------------------------------
//            val systemSpamFlag =
//                callDetails.extras?.getBoolean(EXTRA_CALL_SCREENING_IS_POTENTIAL_SPAM, false)
//                    ?: false
//
//            // ------------------------------------------------------------
//            // (2) HEURISTIC DETECTOR
//            // ------------------------------------------------------------
//            val heuristicFlag = isSuspiciousNumber(phoneNumber)
//
//            // ------------------------------------------------------------
//            // (3) FINAL DECISION
//            // ------------------------------------------------------------
//            val finalSpam = systemSpamFlag || heuristicFlag
//
//            // ------------------------------------------------------------
//            // (4) Build log entry
//            // ------------------------------------------------------------
//            val logEntry = buildString {
//                append("$timestamp — $phoneNumber\n")
//                append("System flag: $systemSpamFlag\n")
//                append("Heuristic: $heuristicFlag\n")
//                append("FINAL SPAM VERDICT: $finalSpam\n")
//                append("RAW: $callDetails\n")
//                append("---------------------------\n")
//            }
//            val db = SpamDatabase.get(applicationContext)
//            db.spamCallDao().insert(
//                SpamCall(
//                    phoneNumber = phoneNumber,
//                    timestamp = System.currentTimeMillis(),
//                    isSystemFlagged = systemSpamFlag,
//                    isHeuristicFlagged = heuristicFlag,
//                    isFinalSpam = finalSpam,
//                    rawDetails = callDetails.toString(),
//                    reason = when {
//                        systemSpamFlag -> "System flagged"
//                        heuristicFlag -> "Heuristic rule triggered"
//                        else -> "Unknown"
//                    },
//                    blocked = finalSpam
//                )
//            )
//
//            // Save log entry persistently
//            SpamLogger.logNumber(logEntry)
//
//            Log.d("SpamCallService", logEntry)
//
//            // ------------------------------------------------------------
//            // (5) If spam => block + reject + prevent voicemail
//            // ------------------------------------------------------------
//            if (finalSpam) {
//                blockNumber(phoneNumber)
//                rejectAsSpam(callDetails)
//            }
//        }
//    }
//
//    // --------------------------------------------------------------------
//    // Reject call + prevent voicemail
//    // --------------------------------------------------------------------
//    private fun rejectAsSpam(details: Call.Details) {
//        val response = CallResponse.Builder()
//            .setDisallowCall(true)                // Reject call
//            .setRejectCall(true)                  // Hang up immediately
//            .setSkipCallLog(true)                 // Do not show in call log
//            .setSkipNotification(true)            // Silence missed-call notification
//            .build()
//
//        respondToCall(details, response)
//    }
//
//    // --------------------------------------------------------------------
//    // Block the number using Android's system block list
//    // --------------------------------------------------------------------
//    private fun blockNumber(number: String) {
//        if (number == "Unknown") return
//
//        try {
//            val values = ContentValues().apply {
//                put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
//            }
//
//            contentResolver.insert(
//                BlockedNumberContract.BlockedNumbers.CONTENT_URI,
//                values
//            )
//
//        } catch (e: SecurityException) {
//            Log.e("SpamCallService", "Missing BLOCKED_NUMBERS permission", e)
//        } catch (e: Exception) {
//            Log.e("SpamCallService", "Block insert failed", e)
//        }
//    }
//
//    private fun isSuspiciousNumber(number: String): Boolean {
//        if (number == "Unknown") return true
//        if (number.length < 7) return true
//        if (number.toSet().size <= 2) return true
//        return false
//    }
//}
