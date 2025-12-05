package com.example.myspamfilterapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extension property to create a DataStore instance for user preferences.
 */
private val Context.dataStore by preferencesDataStore(name = "user_preferences")


/**
 * Repository class for managing user preferences using [DataStore].
 *
 * Currently manages the preference for blocking spam calls.
 *
 * @property context The Android [Context] used to access the DataStore.
 */
class PreferencesRepository(private val context: Context) {

    companion object {
        /** Key for storing the "block spam calls" preference. */
        private val BLOCK_SPAM_KEY = booleanPreferencesKey("block_spam_calls")
    }

    /**
     * A [Flow] of the user's preference for blocking spam calls.
     *
     * Emits `true` if blocking is enabled, `false` otherwise.
     * Defaults to `true` if no preference has been set yet.
     */
    val blockSpamCalls: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[BLOCK_SPAM_KEY] ?: true   // default ON
        }


    /**
     * Updates the user's preference for blocking spam calls.
     *
     * @param enabled `true` to enable blocking spam calls, `false` to disable.
     */
    suspend fun setBlockSpamCalls(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BLOCK_SPAM_KEY] = enabled
        }
    }
}
