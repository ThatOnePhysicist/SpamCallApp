////////---------------------------------------------////////////////////////////////
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myspamfilterapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.example.myspamfilterapp.ui.SpamCallViewModel
import com.example.myspamfilterapp.ui.SpamLogScreen
import com.example.myspamfilterapp.ui.theme.MySpamFilterAppTheme


/**
 * MainActivity is the entry point for the Spam Filter app.
 *
 * Displays the blocked calls log via [SpamLogScreen] and provides functionality
 * to simulate spam calls using a test number.
 *
 * The activity sets up the UI using Jetpack Compose and a Material3 theme.
 */
class MainActivity : ComponentActivity() {

    /** Test phone number used for simulating spam calls. */
    private val testNumber = "+18005551234"//"19252898473"

    /** ViewModel providing the spam call data. */
    private val viewModel: SpamCallViewModel by viewModels()

    /**
     * Android lifecycle method called when the activity is created.
     *
     * Sets up the edge-to-edge display and Compose content with
     * [MySpamFilterAppTheme], containing a [SpamLogScreen].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (ContextCompat.checkSelfPermission(this, "android.permission.SCREEN_CALLS") != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, "android.permission.WRITE_BLOCKED_NUMBERS") != PackageManager.PERMISSION_GRANTED
            ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    "android.permission.SCREEN_CALLS",
                    "android.permission.WRITE_BLOCKED_NUMBERS"
                ),
                123 // Request code
            )
        }
        setContent {
            MySpamFilterAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
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
                    }
                }
            }
        }
    }

    /**
     * Simulates a spam call by logging it and updating the [viewModel].
     *
     * Writes a timestamped test number entry to the spam log file
     * and logs it using [SpamLogger] so the UI updates immediately.
     *
     * @param number The phone number to simulate as a spam call.
     */
    private fun simulateSpamCall(number: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val logEntry = "$timestamp - $number *TEST* simulation"
        // Log to SpamLogger so UI updates immediately
        val logFile = File(filesDir, "spam_calls.log")
        SpamLogger.logNumber(logEntry, logFile.absolutePath)
        logFile.appendText("$logEntry\n")
        viewModel.insertSimulatedCall(number)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED}) {
            } else {
            }
        }
    }
}

/**
 * Composable that displays the list of spam call log entries.
 *
 * Automatically refreshes every 2 seconds to show the latest entries.
 * Highlights the most recent log entry in red.
 *
 * @param modifier Optional [Modifier] for styling and layout adjustments.
 */
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

/**
 * Preview of the [CallLogList] composable for Compose tooling.
 */
@Preview(showBackground = true)
@Composable
fun CallLogPreview() {
    MySpamFilterAppTheme {
        CallLogList()
    }
}
