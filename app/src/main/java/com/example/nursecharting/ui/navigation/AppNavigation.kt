package com.example.nursecharting.ui.navigation

import androidx.compose.foundation.layout.Column // Example, if Column is used, otherwise remove or add specific ones
import androidx.compose.foundation.layout.Row // Example, if Row is used
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn // If LazyColumn is used
import androidx.compose.foundation.lazy.items // If LazyColumn.items is used
import androidx.compose.material.icons.Icons // For default icons
import androidx.compose.material.icons.filled.Add // Example icon
import androidx.compose.material.icons.filled.ArrowBack // Example icon
import androidx.compose.material3.Button // If Button is used
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton // If FAB is used
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // If used
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // If used
import androidx.compose.runtime.remember // If used
import androidx.compose.runtime.setValue // If used
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // CRITICAL
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nursecharting.ui.charting.screen.AddIOForm
import com.example.nursecharting.ui.charting.screen.AddMedicationForm
import com.example.nursecharting.ui.charting.screen.AddNurseNoteForm
import com.example.nursecharting.ui.charting.screen.AddVitalSignForm
// import com.example.nursecharting.ui.charting.screen.ChartingScreen // Will be replaced by PatientChartingHostScreen
import com.example.nursecharting.ui.charting.screen.PatientChartingHostScreen
import com.example.nursecharting.ui.patient.AddEditPatientScreen
import com.example.nursecharting.ui.patient.PatientDetailScreen
import com.example.nursecharting.ui.patient.PatientListScreen


@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.PatientList.route,
        modifier = modifier
    ) {
        composable(Screen.PatientList.route) {
            PatientListScreen(navController = navController)
        }
        composable(
            route = Screen.PatientDetail.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            patientId?.let {
                PatientDetailScreen(navController = navController, patientId = it)
            }
        }
        composable(
            route = Screen.AddEditPatient.route,
            arguments = listOf(navArgument("patientId") {
                type = NavType.StringType
                nullable = true // Allow null for adding new patient
                defaultValue = null
            })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            // patientId will be null if not provided (adding new) or the actual string value
            AddEditPatientScreen(
                navController = navController,
                patientId = patientId // Pass as String?
            )
        }
        // composable(
        //     route = Screen.ChartingScreen.route, // DEPRECATED by PatientChartingHostScreen
        //     arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        // ) { backStackEntry ->
        //     val patientId = backStackEntry.arguments?.getString("patientId")
        //     patientId?.let {
        //         ChartingScreen(navController = navController, patientId = it)
        //     }
        // }

        composable(
            route = Screen.PatientChartingHostScreen.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            patientId?.let {
                PatientChartingHostScreen(patientId = it) // mainNavController removed
            }
        }

        // These "Add" routes are also defined in PatientChartingHostScreen's NavHost.
        // Keeping them here for now in case of other entry points, but primary navigation
        // to them for charting flow should be from within PatientChartingHostScreen.
        composable(
            route = Screen.AddVitalSignScreen.route, // Updated name from Screen.kt
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            patientId?.let {
                AddVitalSignForm(navController = navController, patientId = it)
            }
        }
        composable(
            route = Screen.AddMedicationScreen.route, // Updated name from Screen.kt
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            patientId?.let {
                AddMedicationForm(navController = navController, patientId = it)
            }
        }
        composable(
            route = Screen.AddNurseNoteScreen.route, // Updated name from Screen.kt
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            patientId?.let {
                AddNurseNoteForm(navController = navController, patientId = it)
            }
        }
        // Composable for AddIOScreen removed as its functionality is now inline in IsoScreen.kt
        // composable(
        //     route = Screen.AddIOScreen.route, // Updated name from Screen.kt
        //     arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        // ) { backStackEntry ->
        //     val patientId = backStackEntry.arguments?.getString("patientId")
        //     patientId?.let {
        //         AddIOForm(navController = navController, patientId = it)
        //     }
        // }
    }
}