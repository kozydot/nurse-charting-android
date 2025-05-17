package com.example.nursecharting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "input_output_entries",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["patientId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["patientId"])]
)
data class InputOutputEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: String,
    val timestamp: Long,
    val type: String, // e.g., "Oral", "IV", "Urine", "Emesis"
    val volume: Double // mL
)