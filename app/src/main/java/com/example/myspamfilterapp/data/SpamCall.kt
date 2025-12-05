package com.example.myspamfilterapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single call entry that may be considered spam.
 *
 * This class is stored in the Room database table "spam_calls" and contains
 * all relevant information about the call, including how it was flagged,
 * whether it was blocked, and metadata for tracking call frequency.
 *
 * @property id
 * Unique identifier for this call entry in the database.
 *
 * @property phoneNumber
 * The phone number associated with the call.
 *
 * @property timestamp
 * The time the call was received (ms).
 *
 * @property isSystemFlagged
 * True if the call was automatically flagged as spam by the system.
 *
 * @property isHeuristicFlagged
 * True if the call was flagged as spam by a heuristic algorithm.
 *
 * @property isFinalSpam
 * True if the call is ultimately classified as spam after all checks.
 *
 * @property rawDetails
 * Raw call metadata as received from the system or provider.
 *
 * @property reason
 * Human-readable reason explaining why the call was flagged.
 *
 * @property blocked
 * True if the call was automatically blocked.
 *
 * @property callCount
 * Number of times this phone number has attempted to call (default is 1).
 */
@Entity(tableName = "spam_calls")
data class SpamCall(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phoneNumber: String,
    val timestamp: Long,
    val isSystemFlagged: Boolean,
    val isHeuristicFlagged: Boolean,
    val isFinalSpam: Boolean,
    val rawDetails: String,
    val reason: String,
    val blocked: Boolean,
    val callCount: Int = 1
)
