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
import androidx.compose.foundation.lazy.items // Explicit import for items
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
import androidx.compose.runtime.LaunchedEffect // If used
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf // If used
import androidx.compose.runtime.remember // If used
import androidx.compose.runtime.setValue // If used
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// Added from the mandatory list, though some might not be directly used in *this* file's current state
import androidx.compose.material3.Button // If Button is used
import androidx.compose.material3.FloatingActionButton // If FAB is used
import androidx.compose.material3.OutlinedTextField // If OutlinedTextField is used
// End of added mandatory imports
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nursecharting.data.local.entity.NurseNote // Added for onAddNote lambda
import com.example.nursecharting.ui.charting.ChartingViewModel
import com.example.nursecharting.ui.navigation.Screen
import com.example.nursecharting.utils.toFormattedString // Use centralized util
// import java.text.SimpleDateFormat // No longer needed locally
// import java.util.Date // No longer needed locally
// import java.util.Locale // No longer needed locally

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartingScreen(
    navController: NavController,
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    // LaunchedEffect to set patientId in ViewModel if passed directly and not just via nav args
    // This assumes patientId is also a nav arg that SavedStateHandle picks up.
    // If patientId is *only* passed as a Composable parameter, ViewModel needs a way to receive it.
    // For now, removing the loadChartData call as ViewModel is reactive.
    // LaunchedEffect(patientId) {
    // viewModel.updatePatientId(patientId) // Example if ViewModel needs explicit update
    // }

    val vitalSigns by viewModel.vitalSigns.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val nurseNotes by viewModel.nurseNotes.collectAsStateWithLifecycle()
    val ioEntries by viewModel.ioEntries.collectAsStateWithLifecycle()
    // In a real app, you'd also fetch patient details to display patient name in TopAppBar
    // For now, we'll just use "Charting"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Charting") }, // Changed to avoid long patientId display issue
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            // This FAB could open a menu or navigate to a general "Add Chart Entry" screen
            // For simplicity, we'll assume individual add buttons per section for now.
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vital Signs Section - Replaced with new VitalSignsSection Composable
            item {
                VitalSignsSection(
                    vitalSigns = vitalSigns,
                    onDeleteVitalSign = { /* No-op for deprecated screen */ }
                )
            }
            // The ChartingSectionHeader for "Vital Signs" and its "Add" button are now part
            // of the VitalSignsSection if needed, or handled by new navigation patterns.
            // The old list display (items(vitalSigns...)) is removed.
            // The "Add Vital Sign" navigation is currently:
            // navController.navigate(Screen.AddVitalSign.createRoute(patientId))
            // This will likely be triggered by a global FAB or bottom navigation in future tasks.
            // For now, the explicit "Add" button next to the "Vital Signs" title is removed
            // as the new VitalSignsSection composable handles its own title and layout as per spec.

            // Medications Section
            item {
                ChartingSectionHeader(
                    title = "Medications Administered",
                    // onClick is now handled by MedicationsScreen directly with a modal
                    onAddClick = { /* navController.navigate(Screen.AddMedicationScreen.createRoute(patientId)) */ }
                )
            }
            if (medications.isEmpty()) {
                item { Text("No medications administered.", modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 8.dp)) }
            } else {
                items(medications, key = { it.timestamp }) { med -> // Assuming timestamp is unique enough
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Time: ${med.timestamp.toFormattedString()}", style = MaterialTheme.typography.bodySmall)
                            Text("Medication: ${med.medicationName}", style = MaterialTheme.typography.titleSmall)
                            Text("Dosage: ${med.dosage}", style = MaterialTheme.typography.bodyMedium) // med.dosage should contain the unit
                            Text("Route: ${med.route}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Nurse's Notes Section - Replaced with new NursesNotesSection Composable
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
                    // onClick is now handled by IsoScreen directly
                    onAddClick = { /* navController.navigate(Screen.AddIOScreen.createRoute(patientId)) */ }
                )
            }
            if (ioEntries.isEmpty()) {
                item { Text("No input/output entries recorded.", modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 8.dp)) }
            } else {
                items(ioEntries, key = { it.timestamp }) { io -> // Assuming timestamp is unique enough
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Time: ${io.timestamp.toFormattedString()}", style = MaterialTheme.typography.bodySmall)
                            Text("Type: ${io.type}", style = MaterialTheme.typography.titleSmall) // Removed io.ioType as it's not in entity
                            Text("Volume: ${io.volume} ml", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChartingSectionHeader( // Renamed and simplified
    title: String,
    onAddClick: () -> Unit
) {
    Column { // Keep Column to allow Spacer after if needed, or Row can be directly used.
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
        Spacer(modifier = Modifier.height(8.dp)) // Spacer after header before list items or empty text
    }
}

// Removed local toFormattedString, now using from com.example.nursecharting.utils