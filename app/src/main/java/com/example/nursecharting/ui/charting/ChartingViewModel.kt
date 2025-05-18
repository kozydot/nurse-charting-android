package com.example.nursecharting.ui.charting

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nursecharting.data.local.entity.InputOutputEntry
import com.example.nursecharting.data.local.entity.MedicationAdministered
import com.example.nursecharting.data.local.entity.NurseNote
import com.example.nursecharting.data.local.entity.VitalSign
import com.example.nursecharting.data.local.entity.Task
import com.example.nursecharting.domain.repository.ChartingRepository
import com.example.nursecharting.workers.TaskReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.example.nursecharting.ui.charting.TaskSortOption
import com.example.nursecharting.ui.charting.TaskFilterStatus

@HiltViewModel
class ChartingViewModel @Inject constructor(
    private val chartingRepository: ChartingRepository,
    private val application: Application // Inject Application context
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    private val _internalPatientId = MutableStateFlow<String?>(null)
    val viewModelPatientIdForLogging: String get() = _internalPatientId.value ?: "UNINITIALIZED_INTERNAL"
    val currentPatientId: StateFlow<String?> = _internalPatientId.asStateFlow()

    private val _saveResult = MutableSharedFlow<Boolean>()
    val saveResult: SharedFlow<Boolean> = _saveResult.asSharedFlow()
// Expose the full list of filter options for the UI
    private val _taskFilterOptions: MutableStateFlow<List<TaskFilterStatus>>
    val taskFilterOptions: StateFlow<List<TaskFilterStatus>> get() = _taskFilterOptions

    init {
        Log.d("ChartingViewModel", "--- ChartingViewModel init start ---")
        val sourceOptions = TaskFilterStatus.allOptions
        Log.d("ChartingViewModel", "Source TaskFilterStatus.allOptions (size ${sourceOptions.size}):")
        sourceOptions.forEachIndexed { index, item ->
            val itemClass = item.javaClass.name
            Log.d("ChartingViewModel", "  Source item $index: ${item.displayName}, Class: $itemClass, Identity: ${System.identityHashCode(item)}")
            if (item == TaskFilterStatus.ALL) {
                Log.d("ChartingViewModel", "    Source item $index IS TaskFilterStatus.ALL. Identity of TaskFilterStatus.ALL: ${System.identityHashCode(TaskFilterStatus.ALL)}")
            }
        }

        _taskFilterOptions = MutableStateFlow(sourceOptions.toList()) // Defensive copy
        Log.d("ChartingViewModel", "Initializing ChartingViewModel. _taskFilterOptions assigned.")
        Log.d("ChartingViewModel", "Initial _taskFilterOptions.value (size ${_taskFilterOptions.value.size}): ${_taskFilterOptions.value.joinToString { it.displayName }}")
        _taskFilterOptions.value.forEachIndexed { index, item ->
            val itemClass = item.javaClass.name
            Log.d("ChartingViewModel", "  ViewModel item $index: ${item.displayName}, Class: $itemClass, Identity: ${System.identityHashCode(item)}")
            if (item == TaskFilterStatus.ALL) {
                Log.d("ChartingViewModel", "    ViewModel item $index IS TaskFilterStatus.ALL. Identity of TaskFilterStatus.ALL: ${System.identityHashCode(TaskFilterStatus.ALL)}")
            }
        }
        Log.d("ChartingViewModel", "--- ChartingViewModel init end ---")
    }

@OptIn(ExperimentalCoroutinesApi::class)
    val vitalSigns: StateFlow<List<VitalSign>> = _internalPatientId
        .onEach { pid -> Log.d("ChartingViewModel", "_internalPatientId emitted: $pid for vitalSigns") }
        .flatMapLatest { currentPatientId ->
            if (!currentPatientId.isNullOrBlank()) {
                Log.d("ChartingViewModel", "vitalSigns flatMapLatest: _internalPatientId is $currentPatientId, fetching vitals.")
                chartingRepository.getVitalSigns(currentPatientId)
                    .onStart { Log.d("ChartingViewModel", "vitalSigns flatMapLatest: collecting from repository for $currentPatientId") }
                    .onCompletion { Log.d("ChartingViewModel", "vitalSigns flatMapLatest: repository flow completed for $currentPatientId") }
            } else {
                Log.d("ChartingViewModel", "vitalSigns flatMapLatest: _internalPatientId is null/empty ('$currentPatientId'), emitting emptyList.")
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val medications: StateFlow<List<MedicationAdministered>> = _internalPatientId.flatMapLatest { currentPatientId ->
        if (!currentPatientId.isNullOrBlank()) {
            chartingRepository.getMedicationsAdministered(currentPatientId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val nurseNotes: StateFlow<List<NurseNote>> = _internalPatientId.flatMapLatest { currentPatientId ->
        if (!currentPatientId.isNullOrBlank()) {
            chartingRepository.getNurseNotes(currentPatientId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val ioEntries: StateFlow<List<InputOutputEntry>> = _internalPatientId.flatMapLatest { currentPatientId ->
        if (!currentPatientId.isNullOrBlank()) {
            chartingRepository.getInputOutputEntries(currentPatientId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // StateFlows for sort and filter criteria
    private val _taskSortOption = MutableStateFlow(TaskSortOption.BY_DUE_DATE_ASC)
    val taskSortOption: StateFlow<TaskSortOption> = _taskSortOption.asStateFlow()

    private val _taskFilterStatus = MutableStateFlow<TaskFilterStatus>(TaskFilterStatus.ALL)
    val taskFilterStatus: StateFlow<TaskFilterStatus> = _taskFilterStatus.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    private val _allTasksForPatient: StateFlow<List<Task>> = _internalPatientId.flatMapLatest { currentPatientId ->
        if (!currentPatientId.isNullOrBlank()) {
            chartingRepository.getTasksForPatient(currentPatientId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = combine(
        _allTasksForPatient,
        _taskSortOption,
        _taskFilterStatus
    ) { allTasks, sortOption, filterStatus ->
        Log.d("ChartingViewModel", "Combining tasks. AllTasks: ${allTasks.size}, Sort: $sortOption, Filter: $filterStatus")
        val filteredTasks = when (filterStatus) {
            TaskFilterStatus.ALL -> allTasks
            is TaskFilterStatus.Specific -> {
                allTasks.filter { task ->
                    filterStatus.status.displayName.equals(task.status, ignoreCase = true)
                }
            }
        }
        Log.d("ChartingViewModel", "Filtered tasks: ${filteredTasks.size}")

        when (sortOption) {
            TaskSortOption.BY_DUE_DATE_ASC -> filteredTasks.sortedBy { it.dueDateTime }
            TaskSortOption.BY_DUE_DATE_DESC -> filteredTasks.sortedByDescending { it.dueDateTime }
            TaskSortOption.BY_PRIORITY_HIGH_TO_LOW -> filteredTasks.sortedWith(compareBy { mapPriorityToInt(it.priority) })
            TaskSortOption.BY_PRIORITY_LOW_TO_HIGH -> filteredTasks.sortedWith(compareByDescending { mapPriorityToInt(it.priority) })
            TaskSortOption.BY_STATUS_ASC -> filteredTasks.sortedBy { it.status }
            TaskSortOption.BY_STATUS_DESC -> filteredTasks.sortedByDescending { it.status }
            TaskSortOption.BY_CREATION_DATE_NEWEST -> filteredTasks.sortedByDescending { it.createdAt }
            TaskSortOption.BY_CREATION_DATE_OLDEST -> filteredTasks.sortedBy { it.createdAt }
        }.also {
            Log.d("ChartingViewModel", "Sorted tasks: ${it.size}")
        }
    }.catch { e ->
        Log.e("ChartingViewModel", "Error in tasks combine operator", e)
        emit(emptyList()) // Emit empty list on error
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    private val _showTaskDialog = MutableStateFlow(false)
    val showTaskDialog: StateFlow<Boolean> = _showTaskDialog.asStateFlow()

    fun loadDataForPatient(patientId: String) {
        Log.d("ChartingViewModel", "loadDataForPatient called with: $patientId. Current _internalPatientId: ${_internalPatientId.value}")
        val actualPatientId = if (patientId.equals("null", ignoreCase = true)) null else patientId
        if (!actualPatientId.isNullOrBlank()) {
            _internalPatientId.value = actualPatientId
        } else {
            Log.w("ChartingViewModel", "loadDataForPatient called with invalid or 'null' string patientId: $patientId")
            _internalPatientId.value = null
        }
    }

    fun addVitalSign(vitalSignToAdd: VitalSign) {
        val currentVmPatientId = _internalPatientId.value
        Log.d("ChartingViewModel", "addVitalSign ENTERED. Received vitalSign for patientId: ${vitalSignToAdd.patientId}, timestamp: ${vitalSignToAdd.timestamp}. ViewModel's current _internalPatientId: $currentVmPatientId")
        viewModelScope.launch {
            Log.d("ChartingViewModel", "addVitalSign viewModelScope.launch STARTED for patientId: ${vitalSignToAdd.patientId}")
            Log.d("ChartingViewModel", "Current _internalPatientId in ViewModel (inside launch): $currentVmPatientId")
            try {
                if (!vitalSignToAdd.patientId.isBlank() && vitalSignToAdd.timestamp > 0) {
                    if (vitalSignToAdd.patientId != currentVmPatientId) {
                        Log.w("ChartingViewModel", "Mismatch between vitalSignToAdd.patientId (${vitalSignToAdd.patientId}) and ViewModel's currentVmPatientId ($currentVmPatientId). Proceeding with vitalSignToAdd.patientId.")
                    }
                    Log.i("ChartingViewModel", "Attempting to save vital sign: $vitalSignToAdd")
                    val rowId = chartingRepository.insertVitalSign(vitalSignToAdd)
                    Log.d("ChartingViewModel", "Repository insertVitalSign returned rowId: $rowId")
                    if (rowId > 0) {
                        Log.i("ChartingViewModel", "Vital sign insert successful for patientId: ${vitalSignToAdd.patientId}, rowId: $rowId")
                        _saveResult.emit(true)
                    } else {
                        Log.e("ChartingViewModel", "Insert vital sign failed, rowId: $rowId, for vitalSign: $vitalSignToAdd")
                        _saveResult.emit(false)
                    }
                } else {
                    Log.e("ChartingViewModel", "Skipping vital sign insert due to invalid vitalSignToAdd.patientId ('${vitalSignToAdd.patientId}') or timestamp (${vitalSignToAdd.timestamp}). VM patientId from flow for reference: $currentVmPatientId")
                    _saveResult.emit(false)
                }
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error saving vital sign for patientId: ${vitalSignToAdd.patientId}. VitalSign details: $vitalSignToAdd", e)
                _saveResult.emit(false)
            }
            Log.d("ChartingViewModel", "addVitalSign viewModelScope.launch FINISHED for patientId: ${vitalSignToAdd.patientId}")
        }
        Log.d("ChartingViewModel", "addVitalSign EXITED for patientId: ${vitalSignToAdd.patientId}")
    }

    fun deleteVitalSign(vitalSign: VitalSign) {
        viewModelScope.launch {
            try {
                chartingRepository.deleteVitalSign(vitalSign)
                Log.i("ChartingViewModel", "Vital sign deleted: $vitalSign")
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error deleting vital sign: $vitalSign", e)
            }
        }
    }

    fun addMedication(medication: MedicationAdministered) {
        val currentVmPatientId = _internalPatientId.value
        viewModelScope.launch {
            try {
                if (!medication.patientId.isBlank() && medication.timestamp > 0 && medication.medicationName.isNotBlank()) {
                    if (medication.patientId != currentVmPatientId) {
                        Log.w("ChartingViewModel", "Mismatch during addMedication for patientId. Med: ${medication.patientId}, VM _internalPatientId: $currentVmPatientId. Proceeding with Med's patientId.")
                    }
                    chartingRepository.insertMedicationAdministered(medication)
                    _saveResult.emit(true)
                } else {
                    _saveResult.emit(false)
                }
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error saving medication: $medication", e)
                _saveResult.emit(false)
            }
        }
    }

    fun deleteMedication(medication: MedicationAdministered) {
        viewModelScope.launch {
            try {
                chartingRepository.deleteMedication(medication)
                Log.i("ChartingViewModel", "Medication deleted: $medication")
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error deleting medication: $medication", e)
            }
        }
    }

    fun addNurseNote(nurseNote: NurseNote) {
        val currentVmPatientId = _internalPatientId.value
        viewModelScope.launch {
            try {
                if (!nurseNote.patientId.isBlank() && nurseNote.timestamp > 0 && nurseNote.noteText.isNotBlank()) {
                     if (nurseNote.patientId != currentVmPatientId) {
                        Log.w("ChartingViewModel", "Mismatch during addNurseNote for patientId. Note: ${nurseNote.patientId}, VM _internalPatientId: $currentVmPatientId. Proceeding with Note's patientId.")
                    }
                    chartingRepository.insertNurseNote(nurseNote)
                    _saveResult.emit(true)
                } else {
                    _saveResult.emit(false)
                }
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error saving nurse note: $nurseNote", e)
                _saveResult.emit(false)
            }
        }
    }

    fun deleteInputOutputEntry(entry: InputOutputEntry) {
        viewModelScope.launch {
            try {
                chartingRepository.deleteInputOutputEntry(entry)
                Log.i("ChartingViewModel", "I/O entry deleted: $entry")
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error deleting I/O entry: $entry", e)
            }
        }
    }

    fun deleteNurseNote(nurseNote: NurseNote) {
        viewModelScope.launch {
            try {
                chartingRepository.deleteNurseNote(nurseNote)
                Log.i("ChartingViewModel", "Nurse note deleted: $nurseNote")
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error deleting nurse note: $nurseNote", e)
            }
        }
    }

    fun addIOEntry(entry: InputOutputEntry) {
        val currentVmPatientId = _internalPatientId.value
        viewModelScope.launch {
            try {
                if (!entry.patientId.isBlank() && entry.timestamp > 0 && entry.type.isNotBlank()) {
                    if (entry.patientId != currentVmPatientId) {
                        Log.w("ChartingViewModel", "Mismatch during addIOEntry for patientId. Entry: ${entry.patientId}, VM _internalPatientId: $currentVmPatientId. Proceeding with Entry's patientId.")
                    }
                    chartingRepository.insertInputOutputEntry(entry)
                    _saveResult.emit(true)
                } else {
                    _saveResult.emit(false)
                }
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error saving I/O entry: $entry", e)
                _saveResult.emit(false)
            }
        }
    }

    // Helper function to map priority strings to integers for sorting
    private fun mapPriorityToInt(priority: String): Int {
        return when (priority.lowercase()) {
            "high" -> 0
            "medium" -> 1
            "low" -> 2
            else -> 3 // Unknown priorities last
        }
    }

    // Event Handlers for Sort and Filter
    fun setTaskSortOption(sortOption: TaskSortOption) {
        Log.d("ChartingViewModel", "Setting task sort option to: $sortOption")
        _taskSortOption.value = sortOption
    }

    fun setTaskFilterStatus(filterStatus: TaskFilterStatus) {
        Log.d("ChartingViewModel", "Setting task filter status to: $filterStatus")
        _taskFilterStatus.value = filterStatus
    }

    // Task Event Handlers
    fun onAddTaskClicked() {
        _selectedTask.value = null
        _showTaskDialog.value = true
    }

    fun onTaskClicked(task: Task) {
        _selectedTask.value = task
        _showTaskDialog.value = true
    }

    fun onDismissTaskDialog() {
        _showTaskDialog.value = false
        _selectedTask.value = null
    }

    private fun scheduleTaskReminder(task: Task) {
        val reminderTime = task.reminderDateTime ?: return
        val currentTime = System.currentTimeMillis()
        val delay = reminderTime - currentTime

        if (delay > 0) {
            val taskData = Data.Builder()
                .putLong(TaskReminderWorker.TASK_ID_KEY, task.id)
                .putString(TaskReminderWorker.TASK_DESC_KEY, task.description)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInputData(taskData)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("task_reminder_tag_${task.id}") // Tag for general cancellation by tag if needed
                .build()

            val uniqueWorkName = "task_reminder_work_${task.id}"
            workManager.enqueueUniqueWork(
                uniqueWorkName,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
            Log.d("ChartingViewModel", "Scheduled reminder for task ${task.id} with delay $delay ms. UniqueWorkName: $uniqueWorkName")
        } else {
            Log.d("ChartingViewModel", "Task ${task.id} reminder time is in the past. Not scheduling.")
        }
    }

    private fun cancelTaskReminder(taskId: Long) {
        val uniqueWorkName = "task_reminder_work_${taskId}"
        workManager.cancelUniqueWork(uniqueWorkName)
        // Optionally, also cancel by tag if you use tags for broader cancellation scenarios
        // workManager.cancelAllWorkByTag("task_reminder_tag_${taskId}")
        Log.d("ChartingViewModel", "Cancelled reminder for task $taskId. UniqueWorkName: $uniqueWorkName")
    }

    fun saveTask(task: Task) {
        viewModelScope.launch {
            try {
                val savedTask: Task
                if (task.id == 0L) { // New task
                    val newTaskId = chartingRepository.insertTask(task)
                    savedTask = task.copy(id = newTaskId) // Get the task with the generated ID
                } else { // Existing task
                    chartingRepository.updateTask(task)
                    savedTask = task
                }

                if (savedTask.reminderDateTime != null && savedTask.status != "Completed" && savedTask.status != "Cancelled") {
                    scheduleTaskReminder(savedTask)
                } else {
                    cancelTaskReminder(savedTask.id)
                }
                onDismissTaskDialog()
                _saveResult.emit(true)
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error saving task: $task", e)
                _saveResult.emit(false)
            }
        }
    }

    @Suppress("UNUSED_FUNCTION") // TODO: Implement UI for task deletion or remove if not planned for future use.
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                chartingRepository.deleteTask(task)
                cancelTaskReminder(task.id)
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error deleting task: $task", e)
            }
        }
    }

    fun toggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            try {
                val newStatus = if (task.status == "Completed") "Pending" else "Completed"
                val newCompletedAt = if (newStatus == "Completed") System.currentTimeMillis() else null
                val updatedTask = task.copy(status = newStatus, completedAt = newCompletedAt)
                chartingRepository.updateTask(updatedTask)

                if (updatedTask.status == "Completed" || updatedTask.status == "Cancelled") {
                    cancelTaskReminder(updatedTask.id)
                } else {
                    // Reschedule only if reminderDateTime is set and task is not completed/cancelled
                    if (updatedTask.reminderDateTime != null) {
                        scheduleTaskReminder(updatedTask)
                    } else {
                        // If reminderDateTime is null, ensure any existing reminder is cancelled
                        cancelTaskReminder(updatedTask.id)
                    }
                }
            } catch (e: Exception) {
                Log.e("ChartingViewModel", "Error toggling task completion: $task", e)
            }
        }
    }
}