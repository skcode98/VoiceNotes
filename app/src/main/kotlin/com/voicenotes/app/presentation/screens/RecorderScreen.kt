package com.voicenotes.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.voicenotes.app.presentation.viewmodel.RecorderViewModel
import com.voicenotes.app.utils.FormatUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecorderScreen(
    navController: NavHostController,
    viewModel: RecorderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var recordingTime by remember { mutableStateOf(0L) }
    var title by remember { mutableStateOf("") }
    var transcript by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("en") }
    var showSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isRecording) {
        while (uiState.isRecording && !uiState.isPaused) {
            delay(1000)
            recordingTime += 1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Voice Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(0.dp))

            // Recording visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.large
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.isRecording) {
                        // Waveform visualization (simplified)
                        Row(
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Center
                        ) {
                            repeat(10) { index ->
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .fillMaxHeight(fraction = (Math.random() * 0.8f + 0.2f).toFloat())
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = MaterialTheme.shapes.small
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = FormatUtils.formatDuration(recordingTime),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (recordingTime > 0) FormatUtils.formatDuration(recordingTime) else "Ready to record",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Recording controls
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!uiState.isRecording) {
                    // Start recording
                    FloatingActionButton(
                        onClick = {
                            viewModel.startRecording()
                            recordingTime = 0
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Start Recording",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    // Pause/Resume
                    FloatingActionButton(
                        onClick = {
                            if (uiState.isPaused) {
                                viewModel.resumeRecording()
                            } else {
                                viewModel.pauseRecording()
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Text(
                            text = if (uiState.isPaused) "Resume" else "Pause",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Stop recording
                    FloatingActionButton(
                        onClick = {
                            viewModel.stopRecording("", recordingTime)
                            showSaveDialog = true
                        },
                        containerColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Stop,
                            contentDescription = "Stop Recording",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Cancel
                    IconButton(
                        onClick = {
                            viewModel.stopRecording("", 0)
                            recordingTime = 0
                        },
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (showSaveDialog) {
                AlertDialog(
                    onDismissRequest = { showSaveDialog = false },
                    title = { Text("Save Voice Note") },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Duration: ${FormatUtils.formatDuration(recordingTime)}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Title (optional)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            )
                            OutlinedTextField(
                                value = transcript,
                                onValueChange = { transcript = it },
                                label = { Text("Transcript") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.saveRecording(title, transcript, selectedLanguage)
                                showSaveDialog = false
                                navController.popBackStack()
                            }
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showSaveDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
