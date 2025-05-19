package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nursecharting.data.local.entity.MedicationAdministered
import com.example.nursecharting.ui.charting.ChartingViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationForm(
    navController: NavController,
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    var medicationNameState by remember { mutableStateOf("") }
    var dosageValueState by remember { mutableStateOf("") }
    var dosageUnitState by remember { mutableStateOf("") } // e.g., mg, mL
    var routeState by remember { mutableStateOf("") } // e.g., PO, IV
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.saveResult.collectLatest { success ->
            if (success) {
                navController.popBackStack()
            } else {
                errorMessage = "Failed to save medication. Please try again."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medication") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = medicationNameState,
                onValueChange = { medicationNameState = it },
                label = { Text("Medication Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null && medicationNameState.isBlank()
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dosageValueState,
                    onValueChange = { dosageValueState = it },
                    label = { Text("Dosage Value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    isError = errorMessage != null && dosageValueState.isBlank()
                )
                OutlinedTextField(
                    value = dosageUnitState,
                    onValueChange = { dosageUnitState = it },
                    label = { Text("Unit (e.g., mg)") },
                    modifier = Modifier.weight(1f),
                    isError = errorMessage != null && dosageUnitState.isBlank()
                )
            }
            OutlinedTextField(
                value = routeState,
                onValueChange = { routeState = it },
                label = { Text("Route (e.g., PO, IV)") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null && routeState.isBlank()
            )

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (medicationNameState.isBlank() || dosageValueState.isBlank() || dosageUnitState.isBlank() || routeState.isBlank()) {
                        errorMessage = "Please fill in all fields."
                        return@Button
                    }
                    val dosageDoubleCheck = dosageValueState.toDoubleOrNull()
                    if (dosageDoubleCheck == null) {
                        errorMessage = "Please enter a valid number for dosage value."
                        return@Button
                    }
                     if (patientId.isEmpty() || patientId.lowercase() == "null") {
                        errorMessage = "Invalid Patient ID."
                        return@Button
                    }
                    errorMessage = null

                    val combinedDosage = "$dosageValueState $dosageUnitState"

                    val medication = MedicationAdministered(
                        patientId = patientId,
                        timestamp = System.currentTimeMillis(),
                        medicationName = medicationNameState,
                        dosage = combinedDosage,
                        route = routeState
                    )
                    viewModel.addMedication(medication)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Medication")
            }
        }
    }
}