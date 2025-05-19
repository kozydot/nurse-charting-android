package com.example.nursecharting.ui.charting

// Enum for individual status values, used for mapping
enum class TaskStatusValue(val displayName: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(statusString: String?): TaskStatusValue? {
            return entries.find { it.displayName.equals(statusString, ignoreCase = true) }
        }
    }
}

// Sealed class for filter options, allowing for "ALL"
sealed class TaskFilterStatus(val displayName: String) {
    object ALL : TaskFilterStatus("All")
    data class Specific(val status: TaskStatusValue) : TaskFilterStatus(status.displayName)

    fun matches(taskStatus: String): Boolean {
        return when (this) {
            ALL -> true
            is Specific -> this.status.displayName.equals(taskStatus, ignoreCase = true)
        }
    }

    companion object {
        val allOptions: List<TaskFilterStatus> = buildList {
            add(ALL)
            TaskStatusValue.entries.forEach { statusValue ->
                add(Specific(statusValue))
            }
        }

        fun fromStringValue(statusString: String?): TaskFilterStatus {
            if (statusString == null || statusString.equals("All", ignoreCase = true)) {
                return ALL
            }
            return TaskStatusValue.fromString(statusString)?.let { Specific(it) } ?: ALL
        }
    }
}