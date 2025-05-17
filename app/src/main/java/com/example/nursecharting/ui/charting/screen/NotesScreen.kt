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
    // navController: NavController, // Removed as it's currently unused
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    Log.d("NotesScreen", "Composing NotesScreen for patientId (prop): $patientId. ViewModel instance: $viewModel, ViewModel patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")

    // Call loadDataForPatient when patientId prop changes or ViewModel is first created for this screen
    LaunchedEffect(patientId, viewModel) {
        Log.d("NotesScreen", "LaunchedEffect: Calling viewModel.loadDataForPatient with patientId (prop): $patientId")
        viewModel.loadDataForPatient(patientId)
    }

    val nurseNotes by viewModel.nurseNotes.collectAsStateWithLifecycle()

    LaunchedEffect(nurseNotes) {
        Log.d("NotesScreen", "nurseNotes collected. Count: ${nurseNotes.size}. PatientId (prop): $patientId, VM patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")
    }

    // The NursesNotesSection itself contains the input field and logic to add notes.
    // It also typically displays the list of notes.
    // No separate FAB is needed here as per its design.
    Scaffold { paddingValues ->
        // NursesNotesSection is expected to handle its own layout, including title, input, and list.
        // It might need patientId for adding notes and will use nurseNotes for display.
        // It also needs the onAddNote lambda.
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
             NursesNotesSection(
                patientId = patientId, // Pass patientId
                nurseNotes = nurseNotes,
                onAddNote = { newNote ->
                    viewModel.addNurseNote(newNote)
                },
                onDeleteNote = { noteToDelete ->
                    viewModel.deleteNurseNote(noteToDelete)
                }
            )
            // If NursesNotesSection *only* shows the input and button,
            // and a separate list is needed, that would be added here.
            // However, the spec for NursesNotesSection implies it handles display too.
            // If nurseNotes is empty and NursesNotesSection doesn't show a placeholder,
            // one could be added here:
            if (nurseNotes.isEmpty()) { // This check might be redundant if NursesNotesSection handles it
                 Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // This text might only appear if NursesNotesSection itself doesn't show anything when empty.
                    // Text("No nurse notes recorded. Add one above!")
                }
            }
        }
    }
}