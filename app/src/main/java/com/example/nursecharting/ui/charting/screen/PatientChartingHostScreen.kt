package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nursecharting.data.local.entity.Patient
import com.example.nursecharting.ui.charting.component.PersistentPatientHeader
import com.example.nursecharting.ui.navigation.AppBottomNavigationBar
import com.example.nursecharting.ui.navigation.Screen
import com.example.nursecharting.ui.navigation.getBottomNavigationItems
import com.example.nursecharting.ui.patient.PatientDetailViewModel

@Composable
fun PatientChartingHostScreen(
    mainNavController: NavHostController,
    patientId: String
) {
    val chartingNavController = rememberNavController()
    val navigationItems = getBottomNavigationItems()
    val patientDetailViewModel: PatientDetailViewModel = hiltViewModel()
    val patientState by patientDetailViewModel.patient.collectAsStateWithLifecycle()

    LaunchedEffect(patientId) {
        patientDetailViewModel.loadPatient(patientId)
    }

    val navBackStackEntry by chartingNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            PersistentPatientHeader(
                patient = patientState,
                onEditClick = {
                    if (patientId.isNotBlank()) {
                        mainNavController.navigate(Screen.AddEditPatient.createRoute(patientId))
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                items = navigationItems,
                currentRoute = currentRoute,
                onItemSelected = { route ->
                    val fullRoute = route.replace("{patientId}", patientId)
                    chartingNavController.navigate(fullRoute) {
                        popUpTo(chartingNavController.graph.id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        AppChartingNavHost(
            navController = chartingNavController,
            paddingValues = paddingValues,
            patientId = patientId
        )
    }
}

@Composable
fun AppChartingNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    patientId: String
) {
    NavHost(
        navController = navController,
        startDestination = Screen.VitalsScreen.createRoute(patientId),
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.VitalsScreen.route) {
            VitalsScreen(navController = navController, patientId = patientId)
        }
        composable(Screen.MedicationsScreen.route) {
            MedicationsScreen(patientId = patientId)
        }
        composable(Screen.NotesScreen.route) {
            NotesScreen(patientId = patientId)
        }
        composable(Screen.IntakeOutputScreen.route) {
            IntakeOutputScreen(patientId = patientId)
        }
        composable(Screen.TasksScreen.route) {
            TasksScreen(patientId = patientId)
        }

        composable(Screen.AddVitalSignScreen.route) {
            AddVitalSignForm(navController = navController, patientId = patientId)
        }
        composable(Screen.AddMedicationScreen.route) {
            AddMedicationForm(navController = navController, patientId = patientId)
        }
         composable(Screen.AddNurseNoteScreen.route) {
             AddNurseNoteForm(navController = navController, patientId = patientId)
         }
    }
}