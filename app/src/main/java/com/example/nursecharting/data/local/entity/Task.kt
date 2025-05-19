package com.example.nursecharting.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["patientId"]),
        Index(value = ["due_date_time"]),
        Index(value = ["status"]),
        Index(value = ["priority"])
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "patientId")
    val patientId: String,

    val description: String,

    @ColumnInfo(name = "due_date_time")
    val dueDateTime: Long?, // Combined due date and time

    val priority: String, // e.g., "High", "Medium", "Low"

    val status: String, // e.g., "Pending", "In Progress", "Completed", "Cancelled"

    val notes: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "completed_at")
    val completedAt: Long?,

    @ColumnInfo(name = "reminder_date_time")
    val reminderDateTime: Long? // For notification scheduling
)