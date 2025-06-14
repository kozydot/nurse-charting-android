package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nursecharting.data.local.entity.NurseNote
import com.example.nursecharting.ui.charting.ChartingViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNurseNoteForm(
    navController: NavController,
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    var noteTextState by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.saveResult.collectLatest { success ->
            if (success) {
                navController.popBackStack()
            } else {
                errorMessage = "Failed to save nurse's note. Please try again."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Nurse's Note") },
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
                value = noteTextState,
                onValueChange = { noteTextState = it },
                label = { Text("Nurse's Note") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                singleLine = false,
                isError = errorMessage != null && noteTextState.isBlank()
            )

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    if (noteTextState.isBlank()) {
                        errorMessage = "Note cannot be empty."
                        return@Button
                    }
                     if (patientId.isEmpty() || patientId.lowercase() == "null") {
                        errorMessage = "Invalid Patient ID."
                        return@Button
                    }
                    errorMessage = null

                    val nurseNote = NurseNote(
                        patientId = patientId,
                        timestamp = System.currentTimeMillis(),
                        noteText = noteTextState
                    )
                    viewModel.addNurseNote(nurseNote)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Note")
            }
        }
    }
}