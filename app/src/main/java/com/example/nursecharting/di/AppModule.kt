package com.example.nursecharting.di

import android.content.Context
import androidx.room.Room
import com.example.nursecharting.data.local.AppDatabase
import com.example.nursecharting.data.local.dao.*
import com.example.nursecharting.data.repository.ChartingRepositoryImpl
import com.example.nursecharting.data.repository.PatientRepositoryImpl
import com.example.nursecharting.domain.repository.ChartingRepository
import com.example.nursecharting.domain.repository.PatientRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "nurse_charting_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePatientDao(appDatabase: AppDatabase): PatientDao {
        return appDatabase.patientDao()
    }

    @Provides
    @Singleton
    fun provideVitalSignDao(appDatabase: AppDatabase): VitalSignDao {
        return appDatabase.vitalSignDao()
    }

    @Provides
    @Singleton
    fun provideMedicationAdministeredDao(appDatabase: AppDatabase): MedicationAdministeredDao {
        return appDatabase.medicationAdministeredDao()
    }

    @Provides
    @Singleton
    fun provideNurseNoteDao(appDatabase: AppDatabase): NurseNoteDao {
        return appDatabase.nurseNoteDao()
    }

    @Provides
    @Singleton
    fun provideInputOutputDao(appDatabase: AppDatabase): InputOutputDao {
        return appDatabase.inputOutputDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(appDatabase: AppDatabase): TaskDao {
        return appDatabase.taskDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPatientRepository(
        patientRepositoryImpl: PatientRepositoryImpl
    ): PatientRepository

    @Binds
    @Singleton
    abstract fun bindChartingRepository(
        chartingRepositoryImpl: ChartingRepositoryImpl
    ): ChartingRepository
}