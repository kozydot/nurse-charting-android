package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import android.util.Log
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nursecharting.ui.charting.ChartingViewModel
import com.example.nursecharting.utils.toFormattedString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MedicationsScreen(
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    Log.d("MedicationsScreen", "Composing MedicationsScreen for patientId (prop): $patientId. ViewModel instance: $viewModel, ViewModel patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")

    LaunchedEffect(patientId, viewModel) {
        Log.d("MedicationsScreen", "LaunchedEffect: Calling viewModel.loadDataForPatient with patientId (prop): $patientId")
        viewModel.loadDataForPatient(patientId)
    }

    val medications by viewModel.medications.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(medications) {
        Log.d("MedicationsScreen", "medications collected. Count: ${medications.size}. PatientId (prop): $patientId, VM patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Medication")
            }
        }
    ) { paddingValues ->
        if (showDialog) {
            AddMedicationModal(
                patientId = patientId,
                onDismissRequest = { showDialog = false },
                onSaveMedication = { medication ->
                    viewModel.addMedication(medication)
                    showDialog = false
                }
            )
        }

        if (medications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No medications administered for this patient.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(medications, key = { it.id }) { med ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Time: ${med.timestamp.toFormattedString()}", style = MaterialTheme.typography.bodySmall)
                                Text("Medication: ${med.medicationName}", style = MaterialTheme.typography.titleMedium)
                                Text("Dosage: ${med.dosage}", style = MaterialTheme.typography.bodyLarge)
                                Text("Route: ${med.route}", style = MaterialTheme.typography.bodyLarge)
                            }
                            IconButton(onClick = { viewModel.deleteMedication(med) }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete Medication",
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