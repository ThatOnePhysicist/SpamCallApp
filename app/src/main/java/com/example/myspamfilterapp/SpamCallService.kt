package com.example.myspamfilterapp

import android.content.ContentValues
import android.provider.BlockedNumberContract
import android.telecom.CallScreeningService
import android.telecom.Call.Details
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.myspamfilterapp.data.PreferencesRepository
import com.example.myspamfilterapp.data.SpamCall
import com.example.myspamfilterapp.data.SpamDatabase

/**
 * Service that screens incoming calls to detect and block potential spam.
 *
 * Extends [CallScreeningService] to allow system-level spam call screening.
 * Combines system spam flags, heuristic detection, and user preferences
 * to determine whether a call should be blocked.
 *
 * Detected spam calls are logged to both the database ([SpamDatabase])
 * and a file log via [SpamLogger].
 */
class SpamCallService : CallScreeningService() {

    private companion object {

        /** Extra key used by Android for potential system spam calls. */
        const val EXTRA_CALL_SCREENING_IS_POTENTIAL_SPAM =
            "android.telecom.extra.CALL_SCREENING_IS_POTENTIAL_SPAM"
    }

    /** Coroutine scope for performing I/O tasks asynchronously. */
    private val ioScope = CoroutineScope(Dispatchers.IO)

    /**
     * Callback invoked when a new incoming call is detected.
     *
     * Evaluates whether the call is spam using:
     * 1. System-provided spam flag (Pixel devices only)
     * 2. Heuristic checks on the phone number
     * 3. User preferences from [PreferencesRepository]
     *
     * Logs all relevant call information, inserts it into the database,
     * updates call counts, and blocks the call if determined to be spam.
     *
     * @param callDetails The details of the incoming call.
     */
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
            val logPath = applicationContext.filesDir.absolutePath + "/spam_calls.log"
            val fileEntry = "$timestamp - $phoneNumber - system = $systemSpamFlag - heuristic = $heuristicFlag - finalSpam = $finalSpam"
            SpamLogger.logNumber(fileEntry, logPath)
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

    /**
     * Adds a phone number to the system's blocked numbers list.
     *
     * Does nothing if the number is `"Unknown"`. Handles missing permissions
     * gracefully by logging errors.
     *
     * @param number The phone number to block.
     */
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

    /**
     * Determines whether a phone number is suspicious based on heuristics.
     *
     * Heuristics used:
     * - Unknown or private numbers
     * - Numbers shorter than 7 digits
     * - Numbers with only 1–2 unique digits
     * - Toll-free prefixes (800, 833, 844, 855, 866, 877, 888)
     *
     * @param number The phone number to check.
     * @return `true` if the number is suspicious, `false` otherwise.
     */
    private fun isSuspiciousNumber(number: String): Boolean {
        if (number == "Unknown") return true
        if (number.length < 7) return true
        if (number.toSet().size <= 2) return true

        val tollFreePrefixes = listOf("+1800", "+1833", "+1844", "+1855", "+1866", "+1877", "+1888")
        if (tollFreePrefixes.any { number.startsWith(it) }) return true

        return false
    }
}