package com.example.myspamfilterapp.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore("settings")

object SettingsKeys {
    val BLOCK_SPAM = booleanPreferencesKey("block_spam")
    val USE_HEURISTIC = booleanPreferencesKey("use_heuristic")
    val BLOCK_UNKNOWN = booleanPreferencesKey("block_unknown")
}

class SettingsRepository(private val context: Context) {

    val blockSpam = context.settingsDataStore.data.map { it[SettingsKeys.BLOCK_SPAM] ?: true }
    val useHeuristic = context.settingsDataStore.data.map { it[SettingsKeys.USE_HEURISTIC] ?: true }
    val blockUnknown = context.settingsDataStore.data.map { it[SettingsKeys.BLOCK_UNKNOWN] ?: false }

    suspend fun setBlockSpam(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.BLOCK_SPAM] = enabled }
    }

    suspend fun setUseHeuristic(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.USE_HEURISTIC] = enabled }
    }

    suspend fun setBlockUnknown(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.BLOCK_UNKNOWN] = enabled }
    }
}
