package com.example.nursecharting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey
    val patientId: String,
    val fullName: String,
    val dateOfBirth: String, // Consider using Long for timestamp for better sorting/querying
    val roomNumber: String
)