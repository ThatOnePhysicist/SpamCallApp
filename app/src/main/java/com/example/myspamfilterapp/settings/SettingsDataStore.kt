package com.example.myspamfilterapp.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


/**
 * Extension property to access the [DataStore] for user settings.
 */
val Context.settingsDataStore by preferencesDataStore("settings")


/**
 * Keys used for storing and retrieving user settings in the [settingsDataStore].
 */
object SettingsKeys {
    /** Preference key for blocking spam calls. */
    val BLOCK_SPAM = booleanPreferencesKey("block_spam")

    /** Preference key for enabling heuristic spam detection. */
    val USE_HEURISTIC = booleanPreferencesKey("use_heuristic")

    /** Preference key for blocking calls from unknown numbers. */
    val BLOCK_UNKNOWN = booleanPreferencesKey("block_unknown")
}


/**
 * Repository for reading and updating user settings stored in [settingsDataStore].
 *
 * Provides reactive access via [kotlinx.coroutines.flow.Flow] and suspend functions
 * for updating individual preferences.
 *
 * @property context The [Context] used to access the [settingsDataStore].
 */
class SettingsRepository(private val context: Context) {

    /** Flow emitting whether spam call blocking is enabled (default true). */
    val blockSpam = context.settingsDataStore.data.map { it[SettingsKeys.BLOCK_SPAM] ?: true }

    /** Flow emitting whether heuristic detection is enabled (default true). */
    val useHeuristic = context.settingsDataStore.data.map { it[SettingsKeys.USE_HEURISTIC] ?: true }

    /** Flow emitting whether unknown calls should be blocked (default false). */
    val blockUnknown = context.settingsDataStore.data.map { it[SettingsKeys.BLOCK_UNKNOWN] ?: false }

    /**
     * Update the block spam preference.
     *
     * @param enabled Whether spam calls should be blocked.
     */
    suspend fun setBlockSpam(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.BLOCK_SPAM] = enabled }
    }


    /**
     * Update the use heuristic preference.
     *
     * @param enabled Whether heuristic detection should be used.
     */
    suspend fun setUseHeuristic(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.USE_HEURISTIC] = enabled }
    }


    /**
     * Update the block unknown calls preference.
     *
     * @param enabled Whether calls from unknown numbers should be blocked.
     */
    suspend fun setBlockUnknown(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.BLOCK_UNKNOWN] = enabled }
    }
}
