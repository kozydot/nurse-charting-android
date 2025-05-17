package com.example.nursecharting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nursecharting.data.local.entity.InputOutputEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface InputOutputDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInputOutputEntry(entry: InputOutputEntry)

    @Query("SELECT * FROM input_output_entries WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getInputOutputEntriesForPatient(patientId: String): Flow<List<InputOutputEntry>>

    @Delete
    suspend fun deleteInputOutputEntry(entry: InputOutputEntry)
}