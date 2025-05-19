package com.example.nursecharting.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            AddEditPatientScreen(
                navController = navController,
                patientId = patientId
            )
        }

        composable(
            route = Screen.PatientChartingHostScreen.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            patientId?.let {
                PatientChartingHostScreen(mainNavController = navController, patientId = it)
            }
        }

        composable(
            route = Screen.AddVitalSignScreen.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            patientId?.let {
                AddVitalSignForm(navController = navController, patientId = it)
            }
        }
        composable(
            route = Screen.AddMedicationScreen.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            patientId?.let {
                AddMedicationForm(navController = navController, patientId = it)
            }
        }
        composable(
            route = Screen.AddNurseNoteScreen.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            patientId?.let {
                AddNurseNoteForm(navController = navController, patientId = it)
            }
        }
    }
}