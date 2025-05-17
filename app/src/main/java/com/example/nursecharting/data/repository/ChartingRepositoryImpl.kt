package com.example.nursecharting.data.repository

import com.example.nursecharting.data.local.dao.InputOutputDao
import com.example.nursecharting.data.local.dao.MedicationAdministeredDao
import com.example.nursecharting.data.local.dao.NurseNoteDao
import com.example.nursecharting.data.local.dao.TaskDao // Added import
import com.example.nursecharting.data.local.dao.VitalSignDao
import com.example.nursecharting.data.local.entity.InputOutputEntry
import com.example.nursecharting.data.local.entity.MedicationAdministered
import com.example.nursecharting.data.local.entity.NurseNote
import com.example.nursecharting.data.local.entity.Task // Added import
import com.example.nursecharting.data.local.entity.VitalSign
import com.example.nursecharting.domain.repository.ChartingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChartingRepositoryImpl @Inject constructor(
    private val vitalSignDao: VitalSignDao,
    private val medicationAdministeredDao: MedicationAdministeredDao,
    private val nurseNoteDao: NurseNoteDao,
    private val inputOutputDao: InputOutputDao,
    private val taskDao: TaskDao // Added TaskDao to constructor
) : ChartingRepository {

    override suspend fun insertVitalSign(vitalSign: VitalSign): Long {
        return vitalSignDao.insertVitalSign(vitalSign)
    }

    override fun getVitalSigns(patientId: String): Flow<List<VitalSign>> {
        return vitalSignDao.getVitalSignsForPatient(patientId)
    }

    override suspend fun deleteVitalSign(vitalSign: VitalSign) {
        vitalSignDao.deleteVitalSign(vitalSign)
    }

    override suspend fun insertMedicationAdministered(medication: MedicationAdministered) {
        medicationAdministeredDao.insertMedicationAdministered(medication)
    }

    override fun getMedicationsAdministered(patientId: String): Flow<List<MedicationAdministered>> {
        return medicationAdministeredDao.getMedicationsAdministeredForPatient(patientId)
    }

    override suspend fun deleteMedication(medication: MedicationAdministered) {
        medicationAdministeredDao.deleteMedication(medication)
    }

    override suspend fun insertNurseNote(nurseNote: NurseNote) {
        nurseNoteDao.insertNurseNote(nurseNote)
    }

    override fun getNurseNotes(patientId: String): Flow<List<NurseNote>> {
        return nurseNoteDao.getNurseNotesForPatient(patientId)
    }

    override suspend fun deleteNurseNote(nurseNote: NurseNote) {
        nurseNoteDao.deleteNurseNote(nurseNote)
    }

    override suspend fun insertInputOutputEntry(entry: InputOutputEntry) {
        inputOutputDao.insertInputOutputEntry(entry)
    }

    override fun getInputOutputEntries(patientId: String): Flow<List<InputOutputEntry>> {
        return inputOutputDao.getInputOutputEntriesForPatient(patientId)
    }

    override suspend fun deleteInputOutputEntry(entry: InputOutputEntry) {
        inputOutputDao.deleteInputOutputEntry(entry)
    }

    // Task Methods
    override fun getTasksForPatient(patientId: String): Flow<List<Task>> {
        return taskDao.getTasksForPatient(patientId)
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)
    }

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    override suspend fun getPendingTasksWithReminders(): List<Task> {
        return taskDao.getPendingTasksWithReminders()
    }
}