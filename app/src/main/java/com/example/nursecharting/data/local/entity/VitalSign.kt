package com.example.nursecharting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vital_signs",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["patientId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["patientId"])]
)
data class VitalSign(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: String,
    val timestamp: Long,
    val heartRate: Int,
    val systolicBP: Int,
    val diastolicBP: Int,
    val temperature: Double,
    val temperatureUnit: String, // "°C" or "°F"
    val respiratoryRate: Int,
    val spO2: Int,
    val painScore: String? = null // e.g., "3/10", "N/A"
)