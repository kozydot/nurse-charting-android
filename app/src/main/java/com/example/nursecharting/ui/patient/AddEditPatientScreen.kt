package com.example.nursecharting.ui.patient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
// Add missing from mandatory list even if not used in this specific file yet
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
// Add missing from mandatory list
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState // Added from mandatory list
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
// Removed: import com.example.nursecharting.data.local.entity.Patient (not directly used in this composable)
import kotlinx.coroutines.flow.collectLatest
// Added PatientDetailViewModel import assuming it's in the same package, adjust if not
// import com.example.nursecharting.ui.patient.PatientDetailViewModel // Already implicitly imported by hiltViewModel if in same package and correctly named
// Added SaveResult if it's a sealed class/interface used by the ViewModel
import com.example.nursecharting.ui.patient.SaveResult // Assuming this path, adjust if necessary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPatientScreen(
    navController: NavController,
    patientId: String?, // Null if adding a new patient, Changed to String?
    viewModel: PatientDetailViewModel = hiltViewModel()
) {
    // Local error message state
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Determine if we are editing an existing patient or adding a new one
    val isEditing = patientId != null && patientId.isNotEmpty() && patientId.lowercase() != "null"

    // Collect UI state from ViewModel
    val uiFullName by viewModel.uiFullName.collectAsStateWithLifecycle()
    val uiDob by viewModel.uiDob.collectAsStateWithLifecycle()
    val uiRoomNumber by viewModel.uiRoomNumber.collectAsStateWithLifecycle()
    // If you want to allow editing/setting patient ID for NEW patients:
    val uiPatientIdInput by viewModel.uiPatientIdInput.collectAsStateWithLifecycle()


    // Effect to load patient data if patientId is provided (i.e., editing existing patient)
    // This relies on PatientDetailViewModel's init block also reacting to savedStateHandle
    // or this explicit call if patientId is passed directly.
    LaunchedEffect(patientId, isEditing) {
        if (isEditing) {
            // patientId is asserted non-null here because isEditing is true
            viewModel.loadPatient(patientId!!)
        }
        // If !isEditing, ViewModel's init block should have set up for a new patient.
    }

    // Effect to handle save results (success or failure)
    LaunchedEffect(Unit) {
        viewModel.saveState.collectLatest { result ->
            when (result) {
                is SaveResult.Success -> {
                    navController.popBackStack()
                    viewModel.resetSaveState() // Reset state after handling
                }
                is SaveResult.Failure -> {
                    errorMessage = result.message
                    // Optionally reset saveState in ViewModel if error is displayed and handled
                    // viewModel.resetSaveState()
                }
                null -> {
                    // Initial state, do nothing
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Patient" else "Add New Patient") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient ID field - only for new patients if ID is not auto-generated and user needs to input
            // If patientId is always auto-generated (e.g. UUID) for new patients, this field can be hidden or removed.
            // For editing, it could be a read-only display of the existing ID.
            if (!isEditing) { // Example: Show Patient ID input only for new patients
                OutlinedTextField(
                    value = uiPatientIdInput,
                    onValueChange = { viewModel.updateUiPatientIdInput(it) },
                    label = { Text("Patient ID (Required for new, or leave blank for auto)") },
                    modifier = Modifier.fillMaxWidth(),
                    // isError = errorMessage != null && uiPatientIdInput.isBlank() // Add validation if strictly required before save
                )
            } else {
                // Optionally display existing patient ID (read-only)
                 patientId?.let {
                    Text("Patient ID: $it", style = MaterialTheme.typography.titleMedium)
                 }
            }


            OutlinedTextField(
                value = uiFullName,
                onValueChange = { viewModel.updateUiFullName(it) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null && uiFullName.isBlank()
            )
            OutlinedTextField(
                value = uiDob,
                onValueChange = { viewModel.updateUiDob(it) },
                label = { Text("Date of Birth (YYYY-MM-DD)") }, // Consider using a DatePicker
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null && uiDob.isBlank()
            )
            OutlinedTextField(
                value = uiRoomNumber,
                onValueChange = { viewModel.updateUiRoomNumber(it) },
                label = { Text("Room Number") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null && uiRoomNumber.isBlank()
            )

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // Clear previous error message
                    errorMessage = null
                    // ViewModel now handles validation and patient object creation internally
                    viewModel.savePatient()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Patient")
            }
        }
    }
}