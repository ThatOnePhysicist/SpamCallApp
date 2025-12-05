package com.example.myspamfilterapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myspamfilterapp.data.PreferencesRepository
import kotlinx.coroutines.launch

/**
 * Composable screen for configuring spam call blocking settings.
 *
 * Displays the current spam blocking preference as a switch (ON/OFF) and allows
 * the user to toggle it. Updates are persisted via [PreferencesRepository].
 *
 * @param prefs An instance of [PreferencesRepository] used to read and write
 * user preferences from DataStore.
 */
@Composable
fun BlockingSettingScreen(prefs: PreferencesRepository) {
    // Collect current blocking mode from DataStore as State
    val blockEnabled by prefs.blockSpamCalls.collectAsState(initial = true)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Block Spam Calls", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Options: off / medium / strict
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = blockEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            prefs.setBlockSpamCalls(enabled)
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (blockEnabled) "ON" else "OFF")
            }

    }
}