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
import com.example.nursecharting.data.local.entity.InputOutputEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIOEntryModal(
    patientId: String,
    onDismissRequest: () -> Unit,
    onSaveIOEntry: (InputOutputEntry) -> Unit
) {
    var typeState by remember { mutableStateOf("") }
    var volumeState by remember { mutableStateOf("") }
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
                Text("Add New I/O Entry", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = typeState,
                    onValueChange = { typeState = it; errorMessage = null },
                    label = { Text("Type (e.g., Oral, IVF, Urine)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && typeState.isBlank()
                )

                OutlinedTextField(
                    value = volumeState,
                    onValueChange = { volumeState = it; errorMessage = null },
                    label = { Text("Volume (mL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && (volumeState.isBlank() || volumeState.toDoubleOrNull() == null || volumeState.toDoubleOrNull()!! <= 0)
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
                            if (typeState.isBlank() || volumeState.isBlank()) {
                                errorMessage = "Please fill in all fields."
                                return@Button
                            }
                            val volumeDouble = volumeState.toDoubleOrNull()
                            if (volumeDouble == null || volumeDouble <= 0) {
                                errorMessage = "Please enter a valid positive volume."
                                return@Button
                            }
                            if (patientId.isEmpty() || patientId.lowercase() == "null") {
                                errorMessage = "Invalid Patient ID."
                                return@Button
                            }
                            errorMessage = null

                            val ioEntry = InputOutputEntry(
                                patientId = patientId,
                                timestamp = System.currentTimeMillis(),
                                type = typeState,
                                volume = volumeDouble
                            )
                            onSaveIOEntry(ioEntry)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}