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
import android.util.Log
import com.example.nursecharting.data.local.entity.VitalSign
import com.example.nursecharting.ui.charting.ChartingViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVitalSignForm(
    navController: NavController,
    patientId: String, // Changed to String
    viewModel: ChartingViewModel = hiltViewModel()
) {
    var temperatureState by remember { mutableStateOf("") }
    var heartRateState by remember { mutableStateOf("") }
    var bpSystolicState by remember { mutableStateOf("") }
    var bpDiastolicState by remember { mutableStateOf("") }
    var respiratoryRateState by remember { mutableStateOf("") }
    var oxygenSaturationState by remember { mutableStateOf("") } // This is for SpO2
    var painScoreState by remember { mutableStateOf("") } // Added for pain score
    var temperatureUnitState by remember { mutableStateOf("°C") } // Default C, could be a dropdown/radio
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Log.d("AddVitalSignForm", "LaunchedEffect for saveResult collection started.")
        viewModel.saveResult.collectLatest { success ->
            Log.d("AddVitalSignForm", "saveResult collected: $success")
            if (success) {
                Log.i("AddVitalSignForm", "Save successful, navigating back.")
                navController.popBackStack()
            } else {
                Log.w("AddVitalSignForm", "Save failed, displaying error message.")
                errorMessage = "Failed to save vital sign. Please try again."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Vital Sign") },
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
                value = temperatureState,
                onValueChange = { temperatureState = it },
                label = { Text("Temperature") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Text(temperatureUnitState) } // Basic unit display
            )
            // TODO: Add Radio buttons or Dropdown for Temperature Unit (C/F)

            OutlinedTextField(
                value = heartRateState,
                onValueChange = { heartRateState = it },
                label = { Text("Heart Rate (bpm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top // Explicitly align to top
            ) {
                OutlinedTextField(
                    value = bpSystolicState,
                    onValueChange = { bpSystolicState = it },
                    label = { Text("BP Systolic (mmHg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = bpDiastolicState,
                    onValueChange = { bpDiastolicState = it },
                    label = { Text("BP Diastolic (mmHg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = respiratoryRateState,
                onValueChange = { respiratoryRateState = it },
                label = { Text("Respiratory Rate (breaths/min)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = oxygenSaturationState,
                onValueChange = { oxygenSaturationState = it },
                label = { Text("Oxygen Saturation (SpO2 %)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = painScoreState,
                onValueChange = { painScoreState = it },
                label = { Text("Pain Score (e.g., 3/10 or N/A)") }, // Added Pain Score Field
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    Log.d("AddVitalSignForm", "Save Vital Sign button clicked.")
                    errorMessage = null // Clear previous error

                    val tempDouble = temperatureState.toDoubleOrNull()
                    val hrInt = heartRateState.toIntOrNull()
                    val bpSysInt = bpSystolicState.toIntOrNull()
                    val bpDiaInt = bpDiastolicState.toIntOrNull()
                    val rrInt = respiratoryRateState.toIntOrNull()
                    val spo2Int = oxygenSaturationState.toIntOrNull()

                    // SpO2 is optional in some contexts, but required by entity here.
                    if (tempDouble == null || hrInt == null || bpSysInt == null || bpDiaInt == null || rrInt == null || spo2Int == null) {
                        errorMessage = "Please fill in all fields with valid numbers."
                        return@Button
                    }
                     if (patientId.isEmpty() || patientId.lowercase() == "null") {
                        errorMessage = "Invalid Patient ID."
                        return@Button
                    }
                    errorMessage = null

                    val vitalSign = VitalSign(
                        // id is autoGenerated by Room
                        patientId = patientId,
                        timestamp = System.currentTimeMillis(),
                        temperature = tempDouble, // Changed to Double
                        temperatureUnit = temperatureUnitState,
                        heartRate = hrInt,
                        systolicBP = bpSysInt, // Changed to systolicBP
                        diastolicBP = bpDiaInt, // Changed to diastolicBP
                        respiratoryRate = rrInt,
                        spO2 = spo2Int, // Changed to spO2
                        painScore = painScoreState.ifBlank { null } // Added painScore
                    )
                    Log.d("AddVitalSignForm", "VitalSign object created: $vitalSign")
                    Log.i("AddVitalSignForm", "Calling viewModel.addVitalSign()")
                    viewModel.addVitalSign(vitalSign)
                    Log.d("AddVitalSignForm", "Called viewModel.addVitalSign()")
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Vital Sign")
            }
        }
    }
}