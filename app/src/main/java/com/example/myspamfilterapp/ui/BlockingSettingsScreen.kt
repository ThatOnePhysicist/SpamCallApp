package com.example.myspamfilterapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myspamfilterapp.data.PreferencesRepository
import kotlinx.coroutines.launch

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


//package com.example.myspamfilterapp.ui
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
////import androidx.compose.material3.RadioButton
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.myspamfilterapp.data.PreferencesRepository
//import kotlinx.coroutines.launch
//
//@Composable
//fun BlockingSettingScreen(prefs: PreferencesRepository) {
//    val mode by prefs.blockSpamCalls.collectAsState(initial = "off")
//    val scope = rememberCoroutineScope()
//
//    Column(modifier = Modifier
//        .fillMaxSize()
//        .padding(16.dp)) {
//        Text("Select Blocking Mode", style = MaterialTheme.typography.titleMedium)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        listOf("off", "medium", "strict").forEach { option ->
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(vertical = 4.dp)
//            ) {
//                RadioButton(
//                    selected = (mode == option),
//                    onClick = {
//                         scope.launch {
//                             prefs.setBlockSpamCalls(option)
//                         }
//                    }
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(option.replaceFirstChar { it.uppercase() })
//            }
//        }
//    }
//}