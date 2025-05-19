package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nursecharting.data.local.entity.NurseNote
import com.example.nursecharting.ui.charting.ChartingViewModel
import com.example.nursecharting.ui.navigation.Screen
import com.example.nursecharting.utils.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartingScreen(
    navController: NavController,
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    val vitalSigns by viewModel.vitalSigns.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val nurseNotes by viewModel.nurseNotes.collectAsStateWithLifecycle()
    val ioEntries by viewModel.ioEntries.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Charting") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                VitalSignsSection(
                    vitalSigns = vitalSigns,
                    onDeleteVitalSign = { /* No-op for deprecated screen */ }
                )
            }

            // Medications Section
            item {
                ChartingSectionHeader(
                    title = "Medications Administered",
                    onAddClick = { /* navController.navigate(Screen.AddMedicationScreen.createRoute(patientId)) */ }
                )
            }
            if (medications.isEmpty()) {
                item { Text("No medications administered.", modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 8.dp)) }
            } else {
                items(medications, key = { it.timestamp }) { med ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Time: ${med.timestamp.toFormattedString()}", style = MaterialTheme.typography.bodySmall)
                            Text("Medication: ${med.medicationName}", style = MaterialTheme.typography.titleSmall)
                            Text("Dosage: ${med.dosage}", style = MaterialTheme.typography.bodyMedium)
                            Text("Route: ${med.route}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                NursesNotesSection(
                    patientId = patientId,
                    nurseNotes = nurseNotes,
                    onAddNote = { newNote -> viewModel.addNurseNote(newNote) },
                    onDeleteNote = { /* No-op for deprecated screen */ }
                )
            }

            // Input/Output Section
            item {
                ChartingSectionHeader(
                    title = "Input/Output",
                    onAddClick = { /* navController.navigate(Screen.AddIOScreen.createRoute(patientId)) */ }
                )
            }
            if (ioEntries.isEmpty()) {
                item { Text("No input/output entries recorded.", modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 8.dp)) }
            } else {
                items(ioEntries, key = { it.timestamp }) { io ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Time: ${io.timestamp.toFormattedString()}", style = MaterialTheme.typography.bodySmall)
                            Text("Type: ${io.type}", style = MaterialTheme.typography.titleSmall)
                            Text("Volume: ${io.volume} ml", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChartingSectionHeader(
    title: String,
    onAddClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add $title")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}