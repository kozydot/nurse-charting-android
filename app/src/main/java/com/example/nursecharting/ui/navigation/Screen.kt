package com.example.nursecharting.ui.navigation

sealed class Screen(val route: String) {
    // Existing Screens (Patient List, Detail, Add/Edit Patient)
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

    // Host screen for bottom navigation
    object PatientChartingHostScreen : Screen("patient_charting_host_screen/{patientId}") {
        fun createRoute(patientId: String) = "patient_charting_host_screen/$patientId"
    }

    // New Top-Level Tab Screens for Bottom Navigation
    // These will be hosted within a NavHost inside PatientChartingHostScreen.
    // All require patientId.
    object VitalsScreen : Screen("vitals_screen/{patientId}") {
        fun createRoute(patientId: String) = "vitals_screen/$patientId"
    }
    object MedicationsScreen : Screen("medications_screen/{patientId}") {
        fun createRoute(patientId: String) = "medications_screen/$patientId"
    }
    object NotesScreen : Screen("notes_screen/{patientId}") {
        fun createRoute(patientId: String) = "notes_screen/$patientId"
    }
    object IntakeOutputScreen : Screen("intake_output_screen/{patientId}") { // Input/Output
        fun createRoute(patientId: String) = "intake_output_screen/$patientId"
    }
    object TasksScreen : Screen("tasks_screen/{patientId}") {
        fun createRoute(patientId: String) = "tasks_screen/$patientId"
    }

    // Supporting "Add" Form Screens
    // Used by VitalsScreen for full-form entry.
    // AddMedication and AddIO will also be used from their respective tab screens as per AC.
    object AddVitalSignScreen : Screen("add_vital_sign_screen/{patientId}") { // Renamed from AddVitalSign for clarity
        fun createRoute(patientId: String) = "add_vital_sign_screen/$patientId"
    }
    object AddMedicationScreen : Screen("add_medication_screen/{patientId}") { // Renamed from AddMedication
        fun createRoute(patientId: String) = "add_medication_screen/$patientId"
    }
    object AddNurseNoteScreen : Screen("add_nurse_note_screen/{patientId}") { // Renamed from AddNurseNote
        fun createRoute(patientId: String) = "add_nurse_note_screen/$patientId"
    }
    // object AddIOScreen : Screen("add_io_screen/{patientId}") { // Renamed from AddIO
    //     fun createRoute(patientId: String) = "add_io_screen/$patientId"
    // }

    // DEPRECATED:
    // object ChartingScreen : Screen("charting_screen/{patientId}") {
    //     fun createRoute(patientId: String) = "charting_screen/$patientId"
    // }
    // This route is superseded by the individual tab screens hosted by PatientChartingHostScreen.
}