package com.example.nursecharting.ui.patient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import com.example.nursecharting.ui.patient.SaveResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPatientScreen(
    navController: NavController,
    patientId: String?,
    viewModel: PatientDetailViewModel = hiltViewModel()
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isEditing = patientId != null && patientId.isNotEmpty() && patientId.lowercase() != "null"

    val uiFullName by viewModel.uiFullName.collectAsStateWithLifecycle()
    val uiDob by viewModel.uiDob.collectAsStateWithLifecycle()
    val uiRoomNumber by viewModel.uiRoomNumber.collectAsStateWithLifecycle()
    val uiPatientIdInput by viewModel.uiPatientIdInput.collectAsStateWithLifecycle()


    LaunchedEffect(patientId, isEditing) {
        if (isEditing) {
            viewModel.loadPatient(patientId!!)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.saveState.collectLatest { result ->
            when (result) {
                is SaveResult.Success -> {
                    navController.popBackStack()
                    viewModel.resetSaveState()
                }
                is SaveResult.Failure -> {
                    errorMessage = result.message
                }
                null -> {
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
            if (!isEditing) {
                OutlinedTextField(
                    value = uiPatientIdInput,
                    onValueChange = { viewModel.updateUiPatientIdInput(it) },
                    label = { Text("Patient ID (Required for new, or leave blank for auto)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
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
                label = { Text("Date of Birth (YYYY-MM-DD)") },
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
                    errorMessage = null
                    viewModel.savePatient()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Patient")
            }
        }
    }
}