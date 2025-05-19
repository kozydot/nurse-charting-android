package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nursecharting.ui.charting.ChartingViewModel

@Composable
fun NotesScreen(
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    Log.d("NotesScreen", "Composing NotesScreen for patientId (prop): $patientId. ViewModel instance: $viewModel, ViewModel patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")

    LaunchedEffect(patientId, viewModel) {
        Log.d("NotesScreen", "LaunchedEffect: Calling viewModel.loadDataForPatient with patientId (prop): $patientId")
        viewModel.loadDataForPatient(patientId)
    }

    val nurseNotes by viewModel.nurseNotes.collectAsStateWithLifecycle()

    LaunchedEffect(nurseNotes) {
        Log.d("NotesScreen", "nurseNotes collected. Count: ${nurseNotes.size}. PatientId (prop): $patientId, VM patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")
    }

    Scaffold { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
             NursesNotesSection(
                patientId = patientId,
                nurseNotes = nurseNotes,
                onAddNote = { newNote ->
                    viewModel.addNurseNote(newNote)
                },
                onDeleteNote = { noteToDelete ->
                    viewModel.deleteNurseNote(noteToDelete)
                }
            )
            if (nurseNotes.isEmpty()) {
                 Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                }
            }
        }
    }
}