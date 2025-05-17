package com.example.nursecharting.domain.repository

import com.example.nursecharting.data.local.entity.InputOutputEntry
import com.example.nursecharting.data.local.entity.MedicationAdministered
import com.example.nursecharting.data.local.entity.NurseNote
import com.example.nursecharting.data.local.entity.Task // Added import
import com.example.nursecharting.data.local.entity.VitalSign
import kotlinx.coroutines.flow.Flow

interface ChartingRepository {
    // Vital Signs
    suspend fun insertVitalSign(vitalSign: VitalSign): Long
    fun getVitalSigns(patientId: String): Flow<List<VitalSign>>
    suspend fun deleteVitalSign(vitalSign: VitalSign)

    // Medications Administered
    suspend fun insertMedicationAdministered(medication: MedicationAdministered)
    fun getMedicationsAdministered(patientId: String): Flow<List<MedicationAdministered>>
    suspend fun deleteMedication(medication: MedicationAdministered)

    // Nurse Notes
    suspend fun insertNurseNote(nurseNote: NurseNote)
    fun getNurseNotes(patientId: String): Flow<List<NurseNote>>
    suspend fun deleteNurseNote(nurseNote: NurseNote)

    // Input/Output
    suspend fun insertInputOutputEntry(entry: InputOutputEntry)
    fun getInputOutputEntries(patientId: String): Flow<List<InputOutputEntry>>
    suspend fun deleteInputOutputEntry(entry: InputOutputEntry)

    // Tasks
    fun getTasksForPatient(patientId: String): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun getPendingTasksWithReminders(): List<Task> // Add suspend (already suspend, ensuring it's correct)
}