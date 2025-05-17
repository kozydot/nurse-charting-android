package com.example.nursecharting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nursecharting.data.local.entity.MedicationAdministered
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationAdministeredDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationAdministered(medication: MedicationAdministered)

    @Query("SELECT * FROM medications_administered WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getMedicationsAdministeredForPatient(patientId: String): Flow<List<MedicationAdministered>>

    @Delete
    suspend fun deleteMedication(medication: MedicationAdministered)
}