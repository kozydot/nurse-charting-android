package com.example.nursecharting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "nurse_notes",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["patientId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["patientId"])]
)
data class NurseNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: String,
    val timestamp: Long,
    val noteText: String
)