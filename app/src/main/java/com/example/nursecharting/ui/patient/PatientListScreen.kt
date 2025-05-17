package com.example.nursecharting.ui.patient

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer // Added from mandatory list
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
// Add missing from mandatory list
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // Added from mandatory list
import androidx.compose.runtime.collectAsState // Added from mandatory list
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // Added from mandatory list
import androidx.compose.runtime.remember // Added from mandatory list
import androidx.compose.runtime.setValue // Added from mandatory list
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nursecharting.data.local.entity.Patient
import com.example.nursecharting.ui.navigation.Screen
// import com.example.nursecharting.ui.patient.PatientListViewModel // Implicitly imported

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    navController: NavController,
    viewModel: PatientListViewModel = hiltViewModel()
) {
    val patients by viewModel.patients.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Patients") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.AddEditPatient.createRoute())
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Patient")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(patients, key = { patient -> patient.patientId }) { patient -> // Changed to patientId
                PatientListItem(
                    patient = patient,
                    onItemClick = {
                        navController.navigate(Screen.PatientDetail.createRoute(patient.patientId))
                    },
                    onDeleteClick = {
                        viewModel.deletePatient(patient) // Assuming a delete function in ViewModel
                    }
                )
            }
        }
    }
}

@Composable
fun PatientListItem(
    patient: Patient,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = patient.fullName, style = MaterialTheme.typography.titleMedium)
                Text(text = "Room: ${patient.roomNumber}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Patient")
            }
        }
    }
}