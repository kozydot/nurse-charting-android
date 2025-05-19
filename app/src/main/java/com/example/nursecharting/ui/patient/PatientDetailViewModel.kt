package com.example.nursecharting.ui.patient

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nursecharting.data.local.entity.Patient
import com.example.nursecharting.domain.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientDetailViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _currentPatientIdFromNav = MutableStateFlow(savedStateHandle.get<String>("patientId"))

    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient.asStateFlow()

    private val _uiFullName = MutableStateFlow("")
    val uiFullName: StateFlow<String> = _uiFullName.asStateFlow()

    private val _uiDob = MutableStateFlow("")
    val uiDob: StateFlow<String> = _uiDob.asStateFlow()

    private val _uiRoomNumber = MutableStateFlow("")
    val uiRoomNumber: StateFlow<String> = _uiRoomNumber.asStateFlow()

    private val _uiPatientIdInput = MutableStateFlow("")
    val uiPatientIdInput: StateFlow<String> = _uiPatientIdInput.asStateFlow()


    private val _saveState = MutableStateFlow<SaveResult?>(null)
    val saveState: StateFlow<SaveResult?> = _saveState.asStateFlow()

    init {
        _currentPatientIdFromNav.value?.let { id ->
            if (id.isNotEmpty() && id.lowercase() != "null") {
                loadPatient(id)
            } else {
                _uiFullName.value = ""
                _uiDob.value = ""
                _uiRoomNumber.value = ""
            }
        }
    }

    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            val fetchedPatient = patientRepository.getPatient(patientId).firstOrNull()
            _patient.value = fetchedPatient
            fetchedPatient?.let {
                _uiPatientIdInput.value = it.patientId
                _uiFullName.value = it.fullName
                _uiDob.value = it.dateOfBirth
                _uiRoomNumber.value = it.roomNumber
            }
        }
    }

    fun updateUiFullName(name: String) {
        _uiFullName.value = name
    }

    fun updateUiDob(dateOfBirth: String) {
        _uiDob.value = dateOfBirth
    }

    fun updateUiRoomNumber(room: String) {
        _uiRoomNumber.value = room
    }

    fun updateUiPatientIdInput(id: String) {
        _uiPatientIdInput.value = id
    }


    fun savePatient() {
        val name = _uiFullName.value
        val dob = _uiDob.value
        val room = _uiRoomNumber.value
        val existingPatientId = _currentPatientIdFromNav.value?.takeIf { it.isNotEmpty() && it.lowercase() != "null" }

        if (name.isBlank() || dob.isBlank() || room.isBlank()) {
            _saveState.value = SaveResult.Failure("Full Name, Date of Birth, and Room Number are required.")
            return
        }

        viewModelScope.launch {
            try {
                val patientToSave: Patient
                if (existingPatientId != null) {
                    patientToSave = Patient(
                        patientId = existingPatientId,
                        fullName = name,
                        dateOfBirth = dob,
                        roomNumber = room
                    )
                    patientRepository.updatePatient(patientToSave)
                } else {
                    val newPatientId = _uiPatientIdInput.value.takeIf { it.isNotBlank() }
                        ?: java.util.UUID.randomUUID().toString()

                    if (newPatientId.isBlank()){
                         _saveState.value = SaveResult.Failure("Patient ID is required for new patient.")
                        return@launch
                    }

                    patientToSave = Patient(
                        patientId = newPatientId,
                        fullName = name,
                        dateOfBirth = dob,
                        roomNumber = room
                    )
                    patientRepository.insertPatient(patientToSave)
                }
                _saveState.value = SaveResult.Success
            } catch (e: Exception) {
                _saveState.value = SaveResult.Failure(e.message ?: "An unexpected error occurred while saving.")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = null
    }
}

sealed class SaveResult {
    object Success : SaveResult()
    data class Failure(val message: String) : SaveResult()
}