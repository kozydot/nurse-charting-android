package com.example.nursecharting.ui.navigation

sealed class Screen(val route: String) {
    object PatientList : Screen("patient_list_screen")
    object PatientDetail : Screen("patient_detail_screen/{patientId}") {
        fun createRoute(patientId: String) = "patient_detail_screen/$patientId"
    }
    object AddEditPatient : Screen("add_edit_patient_screen?patientId={patientId}") {
        fun createRoute(patientId: String? = null): String {
            return if (patientId != null) {
                "add_edit_patient_screen?patientId=$patientId"
            } else {
                "add_edit_patient_screen"
            }
        }
    }

    object PatientChartingHostScreen : Screen("patient_charting_host_screen/{patientId}") {
        fun createRoute(patientId: String) = "patient_charting_host_screen/$patientId"
    }

    object VitalsScreen : Screen("vitals_screen/{patientId}") {
        fun createRoute(patientId: String) = "vitals_screen/$patientId"
    }
    object MedicationsScreen : Screen("medications_screen/{patientId}") {
        fun createRoute(patientId: String) = "medications_screen/$patientId"
    }
    object NotesScreen : Screen("notes_screen/{patientId}") {
        fun createRoute(patientId: String) = "notes_screen/$patientId"
    }
    object IntakeOutputScreen : Screen("intake_output_screen/{patientId}") {
        fun createRoute(patientId: String) = "intake_output_screen/$patientId"
    }
    object TasksScreen : Screen("tasks_screen/{patientId}") {
        fun createRoute(patientId: String) = "tasks_screen/$patientId"
    }

    object AddVitalSignScreen : Screen("add_vital_sign_screen/{patientId}") {
        fun createRoute(patientId: String) = "add_vital_sign_screen/$patientId"
    }
    object AddMedicationScreen : Screen("add_medication_screen/{patientId}") {
        fun createRoute(patientId: String) = "add_medication_screen/$patientId"
    }
    object AddNurseNoteScreen : Screen("add_nurse_note_screen/{patientId}") {
        fun createRoute(patientId: String) = "add_nurse_note_screen/$patientId"
    }
}