package com.example.myspamfilterapp.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myspamfilterapp.settings.SettingsRepository
import kotlinx.coroutines.launch

/**
 * Composable screen that displays and allows the user to modify
 * spam-related settings.
 *
 * The settings include:
 * - Enabling/disabling spam blocking.
 * - Using heuristic detection for spam calls.
 * - Automatically blocking unknown callers.
 *
 * All changes are persisted via [SettingsRepository].
 *
 * @param repo Instance of [SettingsRepository] used to read and update settings.
 */
@Composable
fun SettingsScreen(repo: SettingsRepository) {
    val scope = rememberCoroutineScope()

    val blockSpam by repo.blockSpam.collectAsState(initial = true)
    val useHeuristic by repo.useHeuristic.collectAsState(initial = true)
    val blockUnknown by repo.blockUnknown.collectAsState(initial = false)

    Column(Modifier.padding(20.dp)) {

        Text("Spam Blocking Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(20.dp))

        SettingSwitch(
            title = "Enable spam blocking",
            value = blockSpam
        ) { scope.launch { repo.setBlockSpam(it) } }

        SettingSwitch(
            title = "Use heuristic detection",
            value = useHeuristic
        ) { scope.launch { repo.setUseHeuristic(it) } }

        SettingSwitch(
            title = "Auto-block unknown callers",
            value = blockUnknown
        ) { scope.launch { repo.setBlockUnknown(it) } }
    }
}

/**
 * Composable for a labeled switch setting.
 *
 * Displays a title and a switch that can be toggled on/off.
 *
 * @param title The label of the setting.
 * @param value Current state of the switch.
 * @param onChange Lambda invoked when the switch value changes.
 */
@Composable
fun SettingSwitch(title: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title)
        Switch(checked = value, onCheckedChange = onChange)
    }
}
