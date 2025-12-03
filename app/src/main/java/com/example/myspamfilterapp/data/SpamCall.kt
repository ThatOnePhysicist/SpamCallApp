package com.example.myspamfilterapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

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
