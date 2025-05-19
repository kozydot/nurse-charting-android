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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.example.nursecharting.utils.toFormattedString
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun IntakeOutputScreen(
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    Log.d("IntakeOutputScreen", "Composing IntakeOutputScreen for patientId (prop): $patientId. ViewModel instance: $viewModel, ViewModel patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")

    LaunchedEffect(patientId, viewModel) {
        Log.d("IntakeOutputScreen", "LaunchedEffect: Calling viewModel.loadDataForPatient with patientId (prop): $patientId")
        viewModel.loadDataForPatient(patientId)
    }

    val ioEntries by viewModel.ioEntries.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(ioEntries) {
        Log.d("IntakeOutputScreen", "ioEntries collected. Count: ${ioEntries.size}. PatientId (prop): $patientId, VM patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")
    }

    LaunchedEffect(Unit) {
        viewModel.saveResult.collectLatest { success ->
            if (success) {
                scope.launch {
                    snackbarHostState.showSnackbar("I/O entry saved successfully.")
                }
            } else {
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
                showDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add I/O Entry")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            if (ioEntries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Input/Output entries recorded.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
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
                                    modifier = Modifier.size(40.dp)
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