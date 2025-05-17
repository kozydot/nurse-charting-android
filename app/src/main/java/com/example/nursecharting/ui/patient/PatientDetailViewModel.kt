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
    savedStateHandle: SavedStateHandle // Removed private val as it's used only in init
) : ViewModel() {

    // Holds the ID of the patient being viewed/edited. Comes from navigation args.
    private val _currentPatientIdFromNav = MutableStateFlow(savedStateHandle.get<String>("patientId"))
    // val currentPatientIdFromNav: StateFlow<String?> = _currentPatientIdFromNav.asStateFlow() // Expose if needed elsewhere

    // Holds the full Patient object, fetched from the repository
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient.asStateFlow()

    // These StateFlows are for binding to UI input fields.
    // They are initialized from _patient when it's loaded, or are empty for a new patient.
    private val _uiFullName = MutableStateFlow("")
    val uiFullName: StateFlow<String> = _uiFullName.asStateFlow()

    private val _uiDob = MutableStateFlow("")
    val uiDob: StateFlow<String> = _uiDob.asStateFlow()

    private val _uiRoomNumber = MutableStateFlow("")
    val uiRoomNumber: StateFlow<String> = _uiRoomNumber.asStateFlow()

    // This would be for a user-editable Patient ID field if creating a NEW patient
    // and the ID isn't auto-generated or pre-assigned.
    // For now, we'll generate a UUID for new patients if _currentPatientIdFromNav is null.
    private val _uiPatientIdInput = MutableStateFlow("")
    val uiPatientIdInput: StateFlow<String> = _uiPatientIdInput.asStateFlow()


    // For UI state, e.g., to navigate back after save or show error
    private val _saveState = MutableStateFlow<SaveResult?>(null)
    val saveState: StateFlow<SaveResult?> = _saveState.asStateFlow()

    init {
        _currentPatientIdFromNav.value?.let { id ->
            // Ensure "null" string from nav args isn't treated as a valid ID
            if (id.isNotEmpty() && id.lowercase() != "null") {
                loadPatient(id)
            } else {
                // This means we are adding a new patient, patientId from nav was null or "null"
                // Initialize UI fields as empty or with defaults for a new patient.
                // If patient ID needs to be input for new patient:
                // _uiPatientIdInput.value = "" // Or some default
                _uiFullName.value = ""
                _uiDob.value = ""
                _uiRoomNumber.value = ""
            }
        }
    }

    // Public function to load patient details if ID is known
    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            // Assuming patientRepository.getPatient expects a String ID
            val fetchedPatient = patientRepository.getPatient(patientId).firstOrNull()
            _patient.value = fetchedPatient
            fetchedPatient?.let {
                // Populate UI fields from the fetched patient
                _uiPatientIdInput.value = it.patientId // If displaying existing ID
                _uiFullName.value = it.fullName
                _uiDob.value = it.dateOfBirth
                _uiRoomNumber.value = it.roomNumber
            }
        }
    }

    // Functions to update UI-bound StateFlows from user input
    fun updateUiFullName(name: String) {
        _uiFullName.value = name
    }

    fun updateUiDob(dateOfBirth: String) {
        _uiDob.value = dateOfBirth
    }

    fun updateUiRoomNumber(room: String) {
        _uiRoomNumber.value = room
    }

    fun updateUiPatientIdInput(id: String) { // If patient ID is an input field for new patients
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
                    // Editing existing patient
                    patientToSave = Patient(
                        patientId = existingPatientId,
                        fullName = name,
                        dateOfBirth = dob,
                        roomNumber = room
                    )
                    patientRepository.updatePatient(patientToSave)
                } else {
                    // Adding new patient
                    // Use _uiPatientIdInput if it's an editable field for new patient ID,
                    // otherwise generate one.
                    val newPatientId = _uiPatientIdInput.value.takeIf { it.isNotBlank() }
                        ?: java.util.UUID.randomUUID().toString()

                    if (newPatientId.isBlank()){ // Should not happen if UUID is fallback
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