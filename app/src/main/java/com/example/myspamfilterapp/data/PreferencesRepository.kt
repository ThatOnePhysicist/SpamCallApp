package com.example.myspamfilterapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class PreferencesRepository(private val context: Context) {

    companion object {
        private val BLOCK_SPAM_KEY = booleanPreferencesKey("block_spam_calls")
    }

    // Read user preference
    val blockSpamCalls: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[BLOCK_SPAM_KEY] ?: true   // default ON
        }

    // Update user preference
    suspend fun setBlockSpamCalls(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BLOCK_SPAM_KEY] = enabled
        }
    }
}
