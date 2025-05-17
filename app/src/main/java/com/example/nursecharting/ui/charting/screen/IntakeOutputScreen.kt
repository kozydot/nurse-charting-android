package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size // Added this import
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add // Changed for FAB
import androidx.compose.material.icons.filled.Delete
// import androidx.compose.material.icons.filled.Save // No longer used for FAB
// import androidx.compose.material3.Button // Not used directly, FAB is used
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nursecharting.data.local.entity.InputOutputEntry
import com.example.nursecharting.ui.charting.ChartingViewModel
// import com.example.nursecharting.ui.navigation.Screen // Not needed for navigation anymore
import com.example.nursecharting.utils.toFormattedString
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun IntakeOutputScreen( // For Input/Output
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    Log.d("IntakeOutputScreen", "Composing IntakeOutputScreen for patientId (prop): $patientId. ViewModel instance: $viewModel, ViewModel patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")

    // Call loadDataForPatient when patientId prop changes or ViewModel is first created for this screen
    LaunchedEffect(patientId, viewModel) {
        Log.d("IntakeOutputScreen", "LaunchedEffect: Calling viewModel.loadDataForPatient with patientId (prop): $patientId")
        viewModel.loadDataForPatient(patientId)
    }

    val ioEntries by viewModel.ioEntries.collectAsStateWithLifecycle()
    // var typeFieldValue by remember { mutableStateOf("") } // Moved to modal
    // var volumeValue by remember { mutableStateOf("") } // Moved to modal
    // var errorMessage by remember { mutableStateOf<String?>(null) } // Error handling will be in modal or via snackbar
    var showDialog by remember { mutableStateOf(false) } // For controlling the modal
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(ioEntries) { // Log when ioEntries list changes
        Log.d("IntakeOutputScreen", "ioEntries collected. Count: ${ioEntries.size}. PatientId (prop): $patientId, VM patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")
    }

    LaunchedEffect(Unit) { // This collects saveResult for snackbar messages
        viewModel.saveResult.collectLatest { success ->
            if (success) {
                // Clear fields and show success message (handled by modal closing and snackbar)
                scope.launch {
                    snackbarHostState.showSnackbar("I/O entry saved successfully.")
                }
                // errorMessage = null // Error state is managed within the modal now
            } else {
                // This branch handles backend/ViewModel save failures specifically if not caught by modal validation
                // The modal itself should show specific validation errors.
                // If saveResult is false and modal didn't show an error, this is a fallback.
                scope.launch {
                    snackbarHostState.showSnackbar("Failed to save I/O entry. Please try again.")
                }
            }
        }
    }

    if (showDialog) {
        AddIOEntryModal(
            patientId = patientId,
            onDismissRequest = { showDialog = false },
            onSaveIOEntry = { ioEntry ->
                viewModel.addIOEntry(ioEntry)
                showDialog = false // Close dialog on save
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) { // Toggle dialog
                Icon(Icons.Filled.Add, contentDescription = "Add I/O Entry") // Changed icon
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp) // Keep overall padding for the list area
                .fillMaxSize()
        ) {
            // Inline Input Fields REMOVED

            // Spacer(modifier = Modifier.height(16.dp)) // Adjusted or removed depending on final layout

            // List of Entries
            if (ioEntries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(), // Takes full available space after other elements (none here)
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Input/Output entries recorded.") // Simplified message
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f), // Takes remaining space
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ioEntries.sortedByDescending { it.timestamp }, key = { it.id }) { ioEntry ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text("Time: ${ioEntry.timestamp.toFormattedString()}", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Type: ${ioEntry.type}", style = MaterialTheme.typography.titleMedium)
                                    Text("Volume: ${ioEntry.volume} ml", style = MaterialTheme.typography.bodyLarge)
                                }
                                IconButton(
                                    onClick = { viewModel.deleteInputOutputEntry(ioEntry) },
                                    modifier = Modifier.size(40.dp) // Ensure tappable area
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete I/O Entry",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Removed local toFormattedString, now using from com.example.nursecharting.utils