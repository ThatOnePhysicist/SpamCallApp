@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myspamfilterapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myspamfilterapp.data.SpamCall

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

            // ✅ BUTTON IS VISIBLE NOW
            Button(
                onClick = onSimulateCall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Simulate Spam Call")
            }

            // ✅ LIST IS NOW BELOW THE BUTTON IN THE SAME COLUMN
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
                            Text("Number: ${call.phoneNumber}")

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
                            Text("Blocked: ${call.isFinalSpam}")
                            Text("Times seen: ${call.callCount}")
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}


//@file:OptIn(ExperimentalMaterial3Api::class)
//
//package com.example.myspamfilterapp.ui
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.myspamfilterapp.data.SpamCall
//
//@Composable
//fun SpamLogScreen(
//    viewModel: SpamCallViewModel,
//    onSimulateCall: () -> Unit,
//    modifier: Modifier = Modifier
//){
//    val spamCalls by viewModel.spamCalls.collectAsState()
//    val expandedItems = remember { mutableStateOf(setOf<Int>()) }
//
//    Scaffold(
//        modifier = modifier,
//        topBar = {TopAppBar(title = {Text("Blocked Calls Log SpamLogScreen.kt") }) }
//    ) {
//        padding ->
//        Column(modifier = Modifier
//            .fillMaxSize()
//            .padding()
//        ) {
//            Button(
//                onClick = onSimulateCall,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
//            ) {
//                Text("Simulate Spam Call")
//            }
//
//
//        LazyColumn(
//            modifier = Modifier
//            .fillMaxSize()
//            .padding(padding)
//        ) {
//            items(spamCalls, key = {it.id}) { call ->
//                val isExpanded = expandedItems.value.contains(call.id)
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp)
//                ) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text("Number: ${call.phoneNumber}")
//                        TextButton(
//                            onClick = {
//                                expandedItems.value =
//                                    if (isExpanded)
//                                        expandedItems.value - call.id
//                                    else
//                                        expandedItems.value + call.id
//                            }
//                        ) {
//                            Text(if (isExpanded) "Collapse" else "Expand")
//                        }
//                    }
//                    if (isExpanded) {
////                    Text("Number: ${call.phoneNumber}")
//                        Text(
//                            "Time: ${
//                                java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                                    .format(java.util.Date(call.timestamp))
//                            }"
//                        )
//                        Text("System flagged: ${call.isSystemFlagged}")
//                        Text("Heuristic: ${call.isHeuristicFlagged}")
//                        Text("Blocked: ${call.isFinalSpam}")
//                        Text("Times seen: ${call.callCount}")
//                    }
//                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
//                }
//            }
//        }
//    }
//}
//}
//
//
////package com.example.myspamfilterapp.ui
////
////import androidx.compose.foundation.layout.*
////import androidx.compose.foundation.lazy.LazyColumn
////import androidx.compose.foundation.lazy.items
////import androidx.compose.material3.*
////import androidx.compose.runtime.*
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.unit.dp
////import com.example.myspamfilterapp.data.SpamCall
////
////
////@Composable
////fun SpamLogScreen(calls: List<SpamCall>) {
////
////    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
////        items(calls) { call ->
////            Card(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(vertical = 6.dp)
////            ) {
////                Column(Modifier.padding(16.dp)) {
////                    Text("Number: ${call.phoneNumber}")
////                    Text("Time: ${java.util.Date(call.timestamp)}")
////                    Text("Reason: ${call.reason}")
////                    Text("Blocked: ${call.blocked}")
////                }
////            }
////        }
////    }
////}
