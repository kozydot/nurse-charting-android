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
// import com.example.nursecharting.ui.charting.ChartingViewModel // Keep if used for other charting logic
import com.example.nursecharting.ui.charting.component.PersistentPatientHeader
import com.example.nursecharting.ui.navigation.AppBottomNavigationBar
import com.example.nursecharting.ui.navigation.Screen
import com.example.nursecharting.ui.navigation.getBottomNavigationItems
import com.example.nursecharting.ui.patient.PatientDetailViewModel

@Composable
fun PatientChartingHostScreen(
    mainNavController: NavHostController, // The NavController from AppNavigation
    patientId: String
) {
    val chartingNavController = rememberNavController() // NavController for the inner NavHost
    val navigationItems = getBottomNavigationItems()
    val patientDetailViewModel: PatientDetailViewModel = hiltViewModel()
    val patientState by patientDetailViewModel.patient.collectAsStateWithLifecycle()

    LaunchedEffect(patientId) {
        patientDetailViewModel.loadPatient(patientId)
    }

    // Observe current route for bottom bar selection state
    val navBackStackEntry by chartingNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            PersistentPatientHeader(
                patient = patientState,
                onEditClick = {
                    // Ensure patientId is not null or empty before navigating
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
                    // The 'route' from BottomNavigationItem is the base route definition (e.g., "vitals_screen/{patientId}")
                    // We need to replace {patientId} with the actual patientId.
                    val fullRoute = route.replace("{patientId}", patientId)
                    chartingNavController.navigate(fullRoute) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(chartingNavController.graph.id) { // Changed from findStartDestination().id
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
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
            // mainNavController = mainNavController // Removed as it's unused in AppChartingNavHost
        )
    }
}

@Composable
fun AppChartingNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    patientId: String
    // mainNavController: NavHostController // Removed as it's unused
) {
    // Obtain ChartingViewModel here if screens within this NavHost need a shared instance
    // and are not using hiltViewModel() individually.
    // For this setup, individual screens use hiltViewModel(), which should provide
    // the correct ViewModel instance scoped appropriately (e.g., to the NavBackStackEntry
    // of this NavHost if ChartingViewModel is annotated with @HiltViewModel and
    // this NavHost is considered a distinct navigation graph for Hilt).
    // If a single instance shared across all tabs is strictly needed and Hilt scoping isn't
    // achieving that, viewModel could be created here and passed down.
    // However, the current setup with hiltViewModel() in each tab screen is common.

    NavHost(
        navController = navController,
        // Start destination should be one of the tab screens, e.g., Vitals.
        // The route needs the patientId.
        // Explicitly create the route with the patientId for the start destination.
        startDestination = Screen.VitalsScreen.createRoute(patientId),
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.VitalsScreen.route) { // Route definition is "vitals_screen/{patientId}"
            VitalsScreen(navController = navController, patientId = patientId)
        }
        composable(Screen.MedicationsScreen.route) {
            MedicationsScreen(patientId = patientId) // navController removed
        }
        composable(Screen.NotesScreen.route) {
            NotesScreen(patientId = patientId) // navController removed
        }
        composable(Screen.IntakeOutputScreen.route) {
            IntakeOutputScreen(patientId = patientId)
        }
        composable(Screen.TasksScreen.route) {
            // patientId is available from the route arguments for Screen.TasksScreen.route
            // as defined in Screen.kt (e.g., "tasks_screen/{patientId}").
            // PatientChartingHostScreen receives patientId as a parameter.
            // This patientId should be passed to TasksScreen if its composable signature requires it,
            // or if it's needed to construct further navigation routes from within TasksScreen.
            // Currently, TasksScreen signature does not require patientId, but its route does.
            // For consistency with other tab screens that take patientId, we pass it.
            TasksScreen(patientId = patientId)
        }

        // Navigation for "Add" screens from tabs, if they are part of this nested NavHost
        // The FABs in VitalsScreen, MedicationsScreen, IsoScreen use their navController
        // (which is this chartingNavController) to navigate.
        // So, these routes should be defined here if those screens are to be hosted within this
        // nested navigation graph.

        composable(Screen.AddVitalSignScreen.route) {
            // Pass chartingNavController, as this form is part of the charting flow
            AddVitalSignForm(navController = navController, patientId = patientId)
        }
        composable(Screen.AddMedicationScreen.route) {
            AddMedicationForm(navController = navController, patientId = patientId)
        }
        // Composable for AddIOScreen removed as its functionality is now inline in IsoScreen.kt
        // composable(Screen.AddIOScreen.route) {
        //     AddIOForm(navController = navController, patientId = patientId)
        // }
        // AddNurseNoteForm is not directly linked via FAB in NotesScreen as per current plan
        // but can be added here if needed for other entry points.
         composable(Screen.AddNurseNoteScreen.route) {
             AddNurseNoteForm(navController = navController, patientId = patientId)
         }
    }
}