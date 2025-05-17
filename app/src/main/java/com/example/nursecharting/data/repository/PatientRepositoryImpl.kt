package com.example.nursecharting.data.repository

import com.example.nursecharting.data.local.dao.PatientDao
import com.example.nursecharting.data.local.entity.Patient
import com.example.nursecharting.domain.repository.PatientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(
    private val patientDao: PatientDao
) : PatientRepository {

    override suspend fun insertPatient(patient: Patient) {
        patientDao.insertPatient(patient)
    }

    override suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    override suspend fun deletePatient(patient: Patient) {
        patientDao.deletePatient(patient)
    }

    override fun getPatient(patientId: String): Flow<Patient?> {
        return patientDao.getPatientById(patientId)
    }

    override fun getAllPatients(): Flow<List<Patient>> {
        return patientDao.getAllPatients()
    }
}