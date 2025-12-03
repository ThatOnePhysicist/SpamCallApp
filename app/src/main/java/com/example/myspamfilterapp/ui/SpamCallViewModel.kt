package com.example.myspamfilterapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myspamfilterapp.data.SpamCall
import com.example.myspamfilterapp.data.SpamDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SpamCallViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SpamDatabase.get(application)
    val spamCalls: StateFlow<List<SpamCall>> =
        db.spamCallDao()
            .getAllCalls()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    fun insertSimulatedCall(number: String) {
        val spamCall = SpamCall(
            phoneNumber = number,
            timestamp = System.currentTimeMillis(),
            isSystemFlagged = false,
            isHeuristicFlagged = true,
            isFinalSpam = true,
            rawDetails = "SIMULATED CALL",
            reason = "Simulated",
            blocked = true
        )

        viewModelScope.launch {
            db.spamCallDao().insert(spamCall)
        }
    }
}
