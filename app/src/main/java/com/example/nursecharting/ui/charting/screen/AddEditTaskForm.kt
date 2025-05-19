package com.example.nursecharting.ui.charting.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.nursecharting.data.local.entity.Task
import com.example.nursecharting.utils.DateTimeUtils
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskFormDialog(
    taskToEdit: Task?,
    patientId: String,
    onSaveTask: (Task) -> Unit,
    onDismiss: () -> Unit
) {
    var descriptionState by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var dueDateTimeState by remember { mutableStateOf(taskToEdit?.dueDateTime) }
    var priorityState by remember { mutableStateOf(taskToEdit?.priority ?: "Medium") }
    var statusState by remember { mutableStateOf(taskToEdit?.status ?: "Pending") }
    var notesState by remember { mutableStateOf(taskToEdit?.notes ?: "") }
    var reminderDateTimeState by remember { mutableStateOf(taskToEdit?.reminderDateTime) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val priorities = listOf("High", "Medium", "Low")
    val statuses = listOf("Pending", "In Progress", "Completed", "Cancelled")

    var priorityExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    var showDueDatePickerDialog by remember { mutableStateOf(false) }
    val dueDatePickerState = rememberDatePickerState(initialSelectedDateMillis = dueDateTimeState)
    var showDueTimePickerDialog by remember { mutableStateOf(false) }
    val dueTimePickerState = rememberTimePickerState(
        initialHour = dueDateTimeState?.let { Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.HOUR_OF_DAY) } ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        initialMinute = dueDateTimeState?.let { Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.MINUTE) } ?: Calendar.getInstance().get(Calendar.MINUTE),
        is24Hour = false // Or determine from system settings
    )
    var tempDueDateMillis by remember { mutableStateOf<Long?>(null) }

    var showReminderDatePickerDialog by remember { mutableStateOf(false) }
    val reminderDatePickerState = rememberDatePickerState(initialSelectedDateMillis = reminderDateTimeState)
    var showReminderTimePickerDialog by remember { mutableStateOf(false) }
    val reminderTimePickerState = rememberTimePickerState(
        initialHour = reminderDateTimeState?.let { Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.HOUR_OF_DAY) } ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        initialMinute = reminderDateTimeState?.let { Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.MINUTE) } ?: Calendar.getInstance().get(Calendar.MINUTE),
        is24Hour = false
    )
    var tempReminderDateMillis by remember { mutableStateOf<Long?>(null) }



    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (taskToEdit == null) "Add New Task" else "Edit Task",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descriptionState,
                    onValueChange = { descriptionState = it },
                    label = { Text("Description*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && descriptionState.isBlank(),
                    singleLine = false,
                    maxLines = 3
                )

                // Due Date / Time
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Log.d("AddEditTaskForm", "Due Date/Time Box clicked. Current showDueDatePickerDialog: $showDueDatePickerDialog")
                            showDueDatePickerDialog = true
                            Log.d("AddEditTaskForm", "showDueDatePickerDialog after click: $showDueDatePickerDialog")
                        }
                ) {
                    OutlinedTextField(
                        value = dueDateTimeState?.let { DateTimeUtils.toFormattedDateTimeString(it) } ?: "Select Due Date/Time",
                        onValueChange = { /* Read-only, updated by picker */ },
                        label = { Text("Due Date/Time") },
                        modifier = Modifier.fillMaxWidth(), // Clickable is now on the Box
                        enabled = false, // Make it non-interactive itself, click handled by Box
                        colors = OutlinedTextFieldDefaults.colors( // Optional: Adjust colors to make it look less like a disabled field
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Filled.DateRange, contentDescription = "Select Due Date/Time") }
                    )
                }

                // Priority Dropdown
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded }
                ) {
                    OutlinedTextField(
                        value = priorityState,
                        onValueChange = {},
                        label = { Text("Priority") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        priorities.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    priorityState = selectionOption
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

                // Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = statusState,
                        onValueChange = {},
                        label = { Text("Status") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statuses.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    statusState = selectionOption
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notesState,
                    onValueChange = { notesState = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 5
                )

                // Reminder Date/Time
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Log.d("AddEditTaskForm", "Reminder Box clicked. Current showReminderDatePickerDialog: $showReminderDatePickerDialog")
                            showReminderDatePickerDialog = true
                            Log.d("AddEditTaskForm", "showReminderDatePickerDialog after click: $showReminderDatePickerDialog")
                        }
                ) {
                    OutlinedTextField(
                        value = reminderDateTimeState?.let { DateTimeUtils.toFormattedDateTimeString(it) } ?: "Select Reminder Time",
                        onValueChange = { /* Read-only, updated by picker */ },
                        label = { Text("Reminder") },
                        modifier = Modifier.fillMaxWidth(), // Clickable is now on the Box
                        enabled = false, // Make it non-interactive itself, click handled by Box
                        colors = OutlinedTextFieldDefaults.colors( // Optional: Adjust colors to make it look less like a disabled field
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Filled.DateRange, contentDescription = "Select Reminder Date/Time") }
                    )
                }


                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (descriptionState.isBlank()) {
                                errorMessage = "Description cannot be empty."
                                return@Button
                            }
                            errorMessage = null
                            val constructedTask = Task(
                                id = taskToEdit?.id ?: 0L,
                                // For new tasks, patientId will be properly set in TASKS-014.2
                                // For existing tasks, taskToEdit.patientId is used.
                                patientId = taskToEdit?.patientId ?: patientId,
                                description = descriptionState,
                                dueDateTime = dueDateTimeState,
                                priority = priorityState,
                                status = statusState,
                                notes = notesState.ifBlank { null },
                                createdAt = taskToEdit?.createdAt ?: System.currentTimeMillis(),
                                completedAt = if (statusState == "Completed") taskToEdit?.completedAt ?: System.currentTimeMillis() else null, // Preserve existing completedAt if editing and still completed
                                reminderDateTime = reminderDateTimeState
                            )
                            onSaveTask(constructedTask)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

    if (showDueDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDueDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDatePickerState.selectedDateMillis?.let {
                        tempDueDateMillis = it
                        showDueTimePickerDialog = true
                    }
                    showDueDatePickerDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDueDatePickerDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = dueDatePickerState)
        }
    }

    if (showDueTimePickerDialog) {
        AlertDialog(
            onDismissRequest = { showDueTimePickerDialog = false },
            title = { Text("Select Due Time") },
            text = {
                TimePicker(state = dueTimePickerState)
            },
            confirmButton = {
                TextButton(onClick = {
                    tempDueDateMillis?.let { dateMillis ->
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = dateMillis
                        cal.set(Calendar.HOUR_OF_DAY, dueTimePickerState.hour)
                        cal.set(Calendar.MINUTE, dueTimePickerState.minute)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        dueDateTimeState = cal.timeInMillis
                    }
                    showDueTimePickerDialog = false
                    tempDueDateMillis = null
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDueTimePickerDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showReminderDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showReminderDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    reminderDatePickerState.selectedDateMillis?.let {
                        tempReminderDateMillis = it
                        showReminderTimePickerDialog = true
                    }
                    showReminderDatePickerDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showReminderDatePickerDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = reminderDatePickerState)
        }
    }

    if (showReminderTimePickerDialog) {
        AlertDialog(
            onDismissRequest = { showReminderTimePickerDialog = false },
            title = { Text("Select Reminder Time") },
            text = {
                TimePicker(state = reminderTimePickerState)
            },
            confirmButton = {
                TextButton(onClick = {
                    tempReminderDateMillis?.let { dateMillis ->
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = dateMillis
                        cal.set(Calendar.HOUR_OF_DAY, reminderTimePickerState.hour)
                        cal.set(Calendar.MINUTE, reminderTimePickerState.minute)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        reminderDateTimeState = cal.timeInMillis
                    }
                    showReminderTimePickerDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showReminderTimePickerDialog = false }) { Text("Cancel") }
            }
        )
    }
}