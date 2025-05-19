package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.nursecharting.data.local.entity.MedicationAdministered

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationModal(
    patientId: String,
    onDismissRequest: () -> Unit,
    onSaveMedication: (MedicationAdministered) -> Unit
) {
    var medicationNameState by remember { mutableStateOf("") }
    var dosageValueState by remember { mutableStateOf("") }
    var dosageUnitState by remember { mutableStateOf("") }
    var routeState by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add New Medication", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
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
                            onSaveMedication(medication)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}