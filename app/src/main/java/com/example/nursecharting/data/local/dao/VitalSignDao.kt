package com.example.nursecharting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nursecharting.data.local.entity.VitalSign
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalSignDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitalSign(vitalSign: VitalSign): Long

    @Query("SELECT * FROM vital_signs WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getVitalSignsForPatient(patientId: String): Flow<List<VitalSign>>

    @Delete
    suspend fun deleteVitalSign(vitalSign: VitalSign)
}