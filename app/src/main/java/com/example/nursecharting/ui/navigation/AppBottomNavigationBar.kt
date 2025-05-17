package com.example.nursecharting.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt // Correct import for AutoMirrored
import androidx.compose.material.icons.filled.Description // Notes
import androidx.compose.material.icons.filled.FavoriteBorder // Vitals (MonitorHeart as alt)
// import androidx.compose.material.icons.filled.ListAlt // Tasks (Checklist as alt) - Remove this if conflicting
import androidx.compose.material.icons.filled.SwapHoriz // ISO (ImportExport as alt)
import androidx.compose.material.icons.filled.Vaccines // Medications (Medication as alt)
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nursecharting.ui.theme.DarkGrey
import com.example.nursecharting.ui.theme.LightGrey
import com.example.nursecharting.ui.theme.PrimaryBlue

data class BottomNavigationItem(
    val label: String,
    val icon: ImageVector,
    val screenRoute: String // Base route definition from Screen.kt
)

@Composable
fun AppBottomNavigationBar(
    items: List<BottomNavigationItem>,
    currentRoute: String?,
    onItemSelected: (String) -> Unit // Parameter is the base route string
) {
    NavigationBar(
        containerColor = LightGrey, // #FFF2F2F7
        modifier = Modifier
    ) {
        items.forEach { item ->
            // Example: currentRoute might be "vitals_screen/123", item.screenRoute is "vitals_screen/{patientId}"
            // We need to compare the base part of the route.
            val currentBaseRoute = currentRoute?.substringBeforeLast("/")
            val itemBaseRoute = item.screenRoute.substringBefore("/{")

            val isSelected = currentBaseRoute == itemBaseRoute

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item.screenRoute) }, // Navigate using the base route definition
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) PrimaryBlue else DarkGrey
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) PrimaryBlue else DarkGrey
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    unselectedIconColor = DarkGrey,
                    selectedTextColor = PrimaryBlue,
                    unselectedTextColor = DarkGrey,
                    indicatorColor = LightGrey // Blends with background
                )
            )
        }
    }
}

// Helper function to create the list of navigation items.
// This can be defined in PatientChartingHostScreen or passed to it.
fun getBottomNavigationItems(): List<BottomNavigationItem> {
    return listOf(
        BottomNavigationItem("Vitals", Icons.Filled.FavoriteBorder, Screen.VitalsScreen.route),
        BottomNavigationItem("Medications", Icons.Filled.Vaccines, Screen.MedicationsScreen.route),
        BottomNavigationItem("Notes", Icons.Filled.Description, Screen.NotesScreen.route),
        BottomNavigationItem("I&O", Icons.Filled.SwapHoriz, Screen.IntakeOutputScreen.route),
        BottomNavigationItem("Tasks", Icons.AutoMirrored.Filled.ListAlt, Screen.TasksScreen.route)
    )
}