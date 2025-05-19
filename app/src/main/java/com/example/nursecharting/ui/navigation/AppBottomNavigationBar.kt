package com.example.nursecharting.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Vaccines
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
    val screenRoute: String
)

@Composable
fun AppBottomNavigationBar(
    items: List<BottomNavigationItem>,
    currentRoute: String?,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = LightGrey,
        modifier = Modifier
    ) {
        items.forEach { item ->
            val currentBaseRoute = currentRoute?.substringBeforeLast("/")
            val itemBaseRoute = item.screenRoute.substringBefore("/{")

            val isSelected = currentBaseRoute == itemBaseRoute

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item.screenRoute) },
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
                    indicatorColor = LightGrey
                )
            )
        }
    }
}

fun getBottomNavigationItems(): List<BottomNavigationItem> {
    return listOf(
        BottomNavigationItem("Vitals", Icons.Filled.FavoriteBorder, Screen.VitalsScreen.route),
        BottomNavigationItem("Medications", Icons.Filled.Vaccines, Screen.MedicationsScreen.route),
        BottomNavigationItem("Notes", Icons.Filled.Description, Screen.NotesScreen.route),
        BottomNavigationItem("I&O", Icons.Filled.SwapHoriz, Screen.IntakeOutputScreen.route),
        BottomNavigationItem("Tasks", Icons.AutoMirrored.Filled.ListAlt, Screen.TasksScreen.route)
    )
}