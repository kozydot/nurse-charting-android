package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nursecharting.ui.charting.ChartingViewModel
import com.example.nursecharting.ui.charting.TaskFilterStatus
import com.example.nursecharting.ui.charting.TaskSortOption
import android.util.Log // Added for logging

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    patientId: String,
    viewModel: ChartingViewModel = hiltViewModel()
) {
    LaunchedEffect(patientId) {
        if (patientId.isNotEmpty() && patientId.lowercase() != "null") {
            viewModel.loadDataForPatient(patientId)
        }
    }

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val selectedTask by viewModel.selectedTask.collectAsStateWithLifecycle()
    val showTaskDialog by viewModel.showTaskDialog.collectAsStateWithLifecycle()
    val currentPatientId by viewModel.currentPatientId.collectAsStateWithLifecycle()
    val currentSortOption by viewModel.taskSortOption.collectAsStateWithLifecycle()
    val currentFilterStatus by viewModel.taskFilterStatus.collectAsStateWithLifecycle()
    val taskFilterOptions by viewModel.taskFilterOptions.collectAsStateWithLifecycle() // Added

    var sortMenuExpanded by remember { mutableStateOf(false) }
    var filterMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    // Redundant icons removed as per CRASH-TASKDROPDOWN-001
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onAddTaskClicked() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Filter and Sort Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter Dropdown
                Box {
                    Button(onClick = { filterMenuExpanded = true }) {
                        Text(currentFilterStatus.displayName)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Filter Options")
                    }
                    DropdownMenu(
                        expanded = filterMenuExpanded,
                        onDismissRequest = { filterMenuExpanded = false }
                    ) {
                        taskFilterOptions.forEach { filterOption ->
                            Log.d("TasksScreen", "Populating filter dropdown item: $filterOption, displayName: ${filterOption?.displayName ?: "NULL"}")
                            // Ensure filterOption is not null before accessing its properties
                            if (filterOption != null) {
                                DropdownMenuItem(
                                    text = { Text(filterOption.displayName) },
                                    onClick = {
                                        viewModel.setTaskFilterStatus(filterOption)
                                        filterMenuExpanded = false
                                    }
                                )
                            } else {
                                Log.e("TasksScreen", "Encountered a null filterOption in taskFilterOptions list.")
                                // Optionally, you could render a placeholder or skip this item
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Sort Dropdown
                Box {
                    Button(onClick = { sortMenuExpanded = true }) {
                        Text(currentSortOption.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Sort Options")
                    }
                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        TaskSortOption.entries.forEach { sortOption ->
                            DropdownMenuItem(
                                text = { Text(sortOption.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    viewModel.setTaskSortOption(sortOption)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TasksList(
                tasks = tasks,
                onTaskClick = { task -> viewModel.onTaskClicked(task) },
                onToggleComplete = { task -> viewModel.toggleTaskCompleted(task) },
                // Modifier.padding(paddingValues) is now applied to the Column
            )
        }

        if (showTaskDialog) {
            AddEditTaskFormDialog(
                taskToEdit = selectedTask,
                patientId = currentPatientId ?: "",
                onSaveTask = viewModel::saveTask,
                onDismiss = { viewModel.onDismissTaskDialog() }
            )
        }
    }
}