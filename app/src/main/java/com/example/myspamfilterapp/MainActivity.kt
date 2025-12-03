////////---------------------------------------------////////////////////////////////
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myspamfilterapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myspamfilterapp.ui.theme.MySpamFilterAppTheme
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.example.myspamfilterapp.data.SpamCall
import com.example.myspamfilterapp.ui.SpamCallViewModel
import com.example.myspamfilterapp.ui.SpamLogScreen

class MainActivity : ComponentActivity() {

    private val testNumber = "19252898473"
    private val viewModel: SpamCallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySpamFilterAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
//                    topBar = { TopAppBar(title = { Text("Blocked Calls Log") }) }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        SpamLogScreen(
                            viewModel=viewModel,
                            onSimulateCall = {simulateSpamCall(testNumber)},
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )

//                        CallLogList(
//                            modifier = Modifier
//                                .weight(1f)
//                                .background(Color.White)
//                        )
                    }
                }
            }
        }
    }

    // Write test number to the log file
    private fun simulateSpamCall(number: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val logEntry = "$timestamp - $number *TEST* simulation"
        // Log to SpamLogger so UI updates immediately
        SpamLogger.logNumber(logEntry)
        val logFile = File(filesDir, "spam_calls.log")
        logFile.appendText("$logEntry\n")
        viewModel.insertSimulatedCall(number)
    }
}

@Composable
fun CallLogList(modifier: Modifier = Modifier) {
    var logEntries by remember { mutableStateOf(listOf<String>()) }

    // Auto-refresh every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            val newEntries = SpamLogger.getLog().reversed()
            if (newEntries != logEntries){
                logEntries = newEntries
            }
            delay(2000)
        }
//        val logFile = File("/data/data/com.example.myspamfilterapp/files/spam_calls.log")
//        while (true) {
//            if (logFile.exists()) {
//                val lines = logFile.readLines().reversed()
//                if (lines != logEntries) {
//                    logEntries = lines
//                }
//            }
//            delay(2000)
//        }
    }

    LazyColumn(modifier = modifier) {
        itemsIndexed(logEntries) { index, entry ->
            val isLatest = index == 0
            Text(
                text = entry,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                color = if (isLatest) Color.Red else Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CallLogPreview() {
    MySpamFilterAppTheme {
        CallLogList()
    }
}
////////////////////////////////////////////////////////////////////////////

//@file:OptIn(ExperimentalMaterial3Api::class)
//
//package com.example.myspamfilterapp
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.myspamfilterapp.ui.theme.MySpamFilterAppTheme
//import kotlinx.coroutines.delay
//import java.io.File
//
//object SpamCallTracker {
//    var lastBlockedCall by mutableStateOf<String?>(null)
//}
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            MySpamFilterAppTheme {
//                Scaffold(
//                    modifier = Modifier.fillMaxSize(),
//                    topBar = { TopAppBar(title = { Text("Blocked Calls Log") }) }
//                ) { innerPadding ->
//                    CallLogList(modifier = Modifier.padding(innerPadding))
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun CallLogList(modifier: Modifier = Modifier) {
//    var logEntries by remember { mutableStateOf<List<String>>(listOf()) }
//
//    // Read Rust log every 2 seconds
//    LaunchedEffect(Unit) {
//        val logFile = File("/data/data/com.example.myspamfilterapp/files/spam_calls.log")
//        while (true) {
//            if (logFile.exists()) {
//                val lines = logFile.readLines().reversed()
//                if (lines != logEntries) {
//                    logEntries = lines
//                }
//            }
//            delay(2000)
//        }
//    }
//
//    val lastBlockedCall by remember { derivedStateOf { SpamCallTracker.lastBlockedCall } }
//
//    LazyColumn(modifier = modifier) {
//        lastBlockedCall?.let { lastCall ->
//            item {
//                Text(
//                    text = "Last blocked: $lastCall",
//                    color = Color.Red,
//                    modifier = Modifier.padding(8.dp)
//                )
//            }
//        }
//
//        itemsIndexed(logEntries) { index: Int, entry: String ->
//            val isLatest = index == 0
//            Text(
//                text = entry,
//                modifier = Modifier.padding(8.dp),
//                color = if (isLatest) Color.Red else Color.Black
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun CallLogPreview() {
//    MySpamFilterAppTheme {
//        CallLogList()
//    }
//}
