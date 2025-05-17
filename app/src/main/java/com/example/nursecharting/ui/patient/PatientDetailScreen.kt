package com.example.nursecharting.ui.patient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
// Add missing from mandatory list even if not used in this specific file yet
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos // Restoring original icon
// import androidx.compose.material.icons.filled.ChevronRight // Removed specific import, relying on Icons.Filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
// Add missing from mandatory list
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState // Added from mandatory list
import androidx.compose.runtime.mutableStateOf // Added from mandatory list
import androidx.compose.runtime.remember // Added from mandatory list
import androidx.compose.runtime.setValue // Added from mandatory list
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily // Added this import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nursecharting.data.local.entity.Patient
import com.example.nursecharting.ui.navigation.Screen
import com.example.nursecharting.ui.theme.PrimaryBlue
// import com.example.nursecharting.ui.patient.PatientDetailViewModel // Implicitly imported
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    navController: NavController,
    patientId: String, // Changed to String
    viewModel: PatientDetailViewModel = hiltViewModel()
) {
    // ViewModel's init block should handle loading if patientId is from SavedStateHandle.
    // If patientId is passed directly and might differ from SavedStateHandle, explicit load is good.
    LaunchedEffect(patientId) {
        if (patientId.isNotEmpty() && patientId.lowercase() != "null") {
            viewModel.loadPatient(patientId)
        }
    }

    val patient by viewModel.patient.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            patient?.let {
                PatientHeader(
                    patient = it,
                    onNavigateToEdit = {
                        navController.navigate(Screen.AddEditPatient.createRoute(it.patientId))
                    }
                )
            }
        }
    ) { paddingValues ->
        patient?.let { pat ->
            // Main content of the screen
            Column(
                modifier = Modifier
                    .padding(paddingValues) // Apply padding from Scaffold
                    .padding(16.dp) // Additional screen padding
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Display other patient details or charting options here
                // For now, keeping the existing details for context,
                // but these might be redundant if the header is comprehensive enough
                // or if this screen's purpose changes.

                Text("Name: ${pat.fullName}", style = MaterialTheme.typography.headlineSmall)
                Text("DOB: ${pat.dateOfBirth}", style = MaterialTheme.typography.bodyLarge)
                Text("Patient ID: ${pat.patientId}", style = MaterialTheme.typography.bodyLarge)
                Text("Room: ${pat.roomNumber}", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(Screen.PatientChartingHostScreen.createRoute(pat.patientId)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Full Chart")
                }
            }
        } ?: run {
            // Show loading indicator or error message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun PatientHeader(
    patient: Patient,
    onNavigateToEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBlue)
            .clickable { onNavigateToEdit() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = patient.fullName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "DOB: ${patient.dateOfBirth} Room: ${patient.roomNumber}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Default
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, // Reverted to original icon
            contentDescription = "Edit patient details",
            tint = Color.White,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}