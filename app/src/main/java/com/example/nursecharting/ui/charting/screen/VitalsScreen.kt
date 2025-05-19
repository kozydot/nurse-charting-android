package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nursecharting.ui.charting.ChartingViewModel
import com.example.nursecharting.ui.navigation.Screen

@Composable
fun VitalsScreen(
    navController: NavController,
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    Log.d("VitalsScreen", "Composing VitalsScreen for patientId (prop): $patientId. ViewModel instance: $viewModel, ViewModel patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")

    LaunchedEffect(patientId, viewModel) {
        Log.d("VitalsScreen", "LaunchedEffect: Calling viewModel.loadDataForPatient with patientId (prop): $patientId")
        viewModel.loadDataForPatient(patientId)
    }

    val vitalSigns by viewModel.vitalSigns.collectAsStateWithLifecycle()

    LaunchedEffect(vitalSigns) {
        Log.d("VitalsScreen", "vitalSigns collected. Count: ${vitalSigns.size}. PatientId (prop): $patientId, VM patientId (from VM getter): ${viewModel.viewModelPatientIdForLogging}")
        if (vitalSigns.isNotEmpty()) {
            Log.d("VitalsScreen", "First vital sign: ${vitalSigns.firstOrNull()}")
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.AddVitalSignScreen.createRoute(patientId))
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Vital Sign")
            }
        }
    ) { paddingValues ->
        if (vitalSigns.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No vital signs recorded for this patient.")
            }
        } else {
            Box(modifier = Modifier.padding(paddingValues)) {
                VitalSignsSection(
                    vitalSigns = vitalSigns,
                    onDeleteVitalSign = { vitalSign ->
                        viewModel.deleteVitalSign(vitalSign)
                    }
                )
            }
        }
    }
}