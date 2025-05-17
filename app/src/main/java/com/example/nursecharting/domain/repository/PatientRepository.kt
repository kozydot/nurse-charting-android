package com.example.nursecharting.domain.repository

import com.example.nursecharting.data.local.entity.Patient
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    suspend fun insertPatient(patient: Patient)
    suspend fun updatePatient(patient: Patient)
    suspend fun deletePatient(patient: Patient)
    fun getPatient(patientId: String): Flow<Patient?>
    fun getAllPatients(): Flow<List<Patient>>
}