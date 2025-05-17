package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nursecharting.data.local.entity.Task
import com.example.nursecharting.utils.DateTimeUtils
import java.util.Calendar

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.status == "Completed"
    val isOverdue = remember(task.dueDateTime, task.status) {
        if (task.status == "Completed" || task.dueDateTime == null) {
            false
        } else {
            val dueCalendar = Calendar.getInstance().apply {
                timeInMillis = task.dueDateTime
            }
            // If dueDateTime only contains date (time is 00:00:00),
            // we might want to consider it overdue if the current time is past that day.
            // For simplicity here, direct comparison. If specific end-of-day logic is needed, adjust.
            dueCalendar.before(Calendar.getInstance())
        }
    }

    val priorityColor = when (task.priority.lowercase()) {
        "high" -> Color.Red.copy(alpha = 0.7f)
        "medium" -> Color.Yellow.copy(alpha = 0.7f) // Consider a darker yellow for better visibility
        "low" -> Color.Green.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTaskClick(task) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete(task) },
                modifier = Modifier.padding(end = 8.dp)
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(50.dp) // Adjust height as needed
                    .background(priorityColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                    ),
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                val dueDateTimeString = remember(task.dueDateTime) {
                    task.dueDateTime?.let {
                        "Due: ${DateTimeUtils.toFormattedDateTimeString(it)}"
                    } ?: "No due date"
                }

                Text(
                    text = dueDateTimeString,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverdue && !isCompleted) Color.Red else (if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Priority: ${task.priority}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Status: ${task.status}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (task.status == "Pending" || task.status == "In Progress") FontWeight.Bold else FontWeight.Normal,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}