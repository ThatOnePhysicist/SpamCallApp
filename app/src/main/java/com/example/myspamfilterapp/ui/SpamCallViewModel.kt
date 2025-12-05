package com.example.myspamfilterapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myspamfilterapp.data.SpamCall
import com.example.myspamfilterapp.data.SpamDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing and exposing spam call data
 * to the UI layer.
 *
 * Retrieves all spam calls from the [SpamDatabase] and provides
 * them as a [StateFlow] that can be observed by Compose or other
 * lifecycle-aware components.
 *
 * Provides a function to simulate a spam call for testing purposes.
 *
 * @param application Application context used to obtain the database instance.
 */
class SpamCallViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SpamDatabase.get(application)

    /**
     * StateFlow of all spam calls in the database.
     * Updated automatically when the underlying database changes.
     */
    val spamCalls: StateFlow<List<SpamCall>> =
        db.spamCallDao()
            .getAllCalls()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Inserts a simulated spam call into the database.
     *
     * This is primarily intended for testing or demo purposes.
     *
     * @param number The phone number of the simulated call.
     */
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
