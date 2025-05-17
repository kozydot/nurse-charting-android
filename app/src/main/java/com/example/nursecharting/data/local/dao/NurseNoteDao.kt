package com.example.nursecharting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nursecharting.data.local.entity.NurseNote
import kotlinx.coroutines.flow.Flow

@Dao
interface NurseNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNurseNote(note: NurseNote)

    @Query("SELECT * FROM nurse_notes WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getNurseNotesForPatient(patientId: String): Flow<List<NurseNote>>

    @Delete
    suspend fun deleteNurseNote(note: NurseNote)
}