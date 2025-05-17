package com.example.nursecharting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medications_administered",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["patientId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["patientId"])]
)
data class MedicationAdministered(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: String,
    val timestamp: Long,
    val medicationName: String,
    val dosage: String,
    val route: String
)