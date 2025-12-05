@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myspamfilterapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Composable screen that displays a log of blocked spam calls.
 *
 * Shows all spam calls from the provided [SpamCallViewModel] in a scrollable list.
 * Each call can be expanded to reveal details such as timestamp, system and heuristic flags,
 * blocked status, and number of times the call has been seen.
 *
 * Provides a button to simulate a spam call for testing purposes.
 *
 * @param viewModel The [SpamCallViewModel] that provides the list of spam calls.
 * @param onSimulateCall Lambda invoked when the "Simulate Spam Call" button is pressed.
 * @param modifier Optional [Modifier] for styling and layout adjustments.
 */
@Composable
fun SpamLogScreen(
    viewModel: SpamCallViewModel,
    onSimulateCall: () -> Unit,
    modifier: Modifier = Modifier
){
    val spamCalls by viewModel.spamCalls.collectAsState()
    val expandedItems = remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Blocked Calls Log SpamLogScreen.kt") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            //  BUTTON IS VISIBLE NOW
            Button(
                onClick = onSimulateCall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Simulate Spam Call")
            }

            //  LIST IS NOW BELOW THE BUTTON IN THE SAME COLUMN
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(spamCalls, key = { it.id }) { call ->
                    val isExpanded = expandedItems.value.contains(call.id)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Number: ${call.phoneNumber}",
                                color = if (call.isFinalSpam) Color.Red else Color.Green
                            )

                            Text(
                                text = if (call.isFinalSpam) "Blocked" else "Allowed",
                                color = if (call.isFinalSpam) Color.Red else Color.Green
                            )


                            TextButton(
                                onClick = {
                                    expandedItems.value =
                                        if (isExpanded)
                                            expandedItems.value - call.id
                                        else
                                            expandedItems.value + call.id
                                }
                            ) {
                                Text(if (isExpanded) "Collapse" else "Expand")
                            }
                        }

                        if (isExpanded) {
                            Text(
                                "Time: ${
                                    java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .format(java.util.Date(call.timestamp))
                                }"
                            )
                            Text("System flagged: ${call.isSystemFlagged}")
                            Text("Heuristic: ${call.isHeuristicFlagged}")
                            Text("Blocked: ${call.isFinalSpam}", color = if (call.isFinalSpam) Color.Red else Color.Green)
                            Text("Times seen: ${call.callCount}")
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}