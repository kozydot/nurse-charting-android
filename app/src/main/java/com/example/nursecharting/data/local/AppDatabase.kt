package com.example.nursecharting.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nursecharting.data.local.dao.InputOutputDao
import com.example.nursecharting.data.local.dao.MedicationAdministeredDao
import com.example.nursecharting.data.local.dao.NurseNoteDao
import com.example.nursecharting.data.local.dao.PatientDao
import com.example.nursecharting.data.local.dao.TaskDao
import com.example.nursecharting.data.local.dao.VitalSignDao
import com.example.nursecharting.data.local.entity.InputOutputEntry
import com.example.nursecharting.data.local.entity.MedicationAdministered
import com.example.nursecharting.data.local.entity.NurseNote
import com.example.nursecharting.data.local.entity.Patient
import com.example.nursecharting.data.local.entity.Task
import com.example.nursecharting.data.local.entity.VitalSign

@Database(
    entities = [
        Patient::class,
        VitalSign::class,
        MedicationAdministered::class,
        NurseNote::class,
        InputOutputEntry::class,
        Task::class
    ],
    version = 5, // Incremented version for Task indices
    exportSchema = false // Set to true if you plan to export schema for testing or migrations
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun vitalSignDao(): VitalSignDao
    abstract fun medicationAdministeredDao(): MedicationAdministeredDao
    abstract fun nurseNoteDao(): NurseNoteDao
    abstract fun inputOutputDao(): InputOutputDao
    abstract fun taskDao(): TaskDao

    // Companion object removed as Hilt manages the singleton instance
}