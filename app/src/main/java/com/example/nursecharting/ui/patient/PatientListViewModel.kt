package com.example.nursecharting.ui.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nursecharting.data.local.entity.Patient
import com.example.nursecharting.domain.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientListViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {

    val patients: StateFlow<List<Patient>> = patientRepository.getAllPatients()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            patientRepository.deletePatient(patient)
        }
    }
}