# Nurse Charting Android Application

A native Android application designed to help nurses and healthcare professionals efficiently manage patient charting tasks, notes, vital signs, and other essential clinical data.

## Key Features

*   **Patient Management:**
    *   Add, edit, and view patient details.
    *   List all patients.
*   **Task Management:**
    *   Create, view, edit, and delete tasks for patients.
    *   Filter tasks by status: All, Pending, In Progress, Completed, Cancelled.
    *   Sort tasks by various criteria (Due Date, Priority, Status, Creation Date).
    *   Mark tasks as complete or pending.
    *   Set reminders for tasks, which trigger notifications.
*   **Vital Signs Charting:**
    *   Record and view patient vital signs (e.g., Temperature, Blood Pressure, Heart Rate, Respiratory Rate, SpO2, Pain).
*   **Medications Administered:**
    *   Log medications administered to patients.
*   **Nurse's Notes:**
    *   Create, view, and manage textual notes for patients.
*   **Input/Output (I&O) Charting:**
    *   Record and track patient input and output volumes.

## Tech Stack

*   **Language:** Kotlin
*   **UI:** Jetpack Compose
*   **Architecture:** Model-View-ViewModel (MVVM)
*   **Database:** Room Persistence Library (local SQLite database)
*   **Asynchronous Programming:** Kotlin Coroutines & Flow
*   **Dependency Injection:** Hilt (Dagger Hilt)
*   **Background Tasks:** Android WorkManager (for task reminders)
*   **Navigation:** Jetpack Navigation Compose
*   **Key AndroidX Libraries:**
    *   Lifecycle (ViewModel, LiveData, Lifecycle-aware components)
    *   Activity
    *   Core KTX
*   **Build System:** Gradle

## Project Structure

The project is primarily organized by layers, with feature-specific packages within those layers:

*   `app/src/main/java/com/example/nursecharting/`
    *   `data/`: Contains data sources, repositories, DAOs (Data Access Objects), and entity definitions.
        *   `local/`: Room database setup, DAOs, and entities.
        *   `repository/`: Repository implementations.
    *   `di/`: Dependency injection modules (Hilt).
    *   `domain/`: Contains repository interfaces and use cases (though use cases might be implicitly handled in ViewModels for this project size).
        *   `repository/`: Repository interfaces.
    *   `ui/`: Contains UI-related components, including Composable screens, ViewModels, and navigation.
        *   `charting/`: UI and ViewModel for charting features (Tasks, Vitals, Notes, Medications, I&O).
        *   `navigation/`: Navigation graph and screen definitions.
        *   `patient/`: UI and ViewModels for patient management.
        *   `theme/`: Compose theme (colors, typography, shapes).
    *   `workers/`: `WorkManager` worker implementations (e.g., `TaskReminderWorker`).
    *   `utils/`: Utility classes (e.g., `DateTimeUtils`).
    *   `MainApplication.kt`: Application class (used for Hilt setup).
    *   `MainActivity.kt`: Main entry point activity.

## Setup Instructions

### Prerequisites

*   Android Studio (latest stable version recommended, e.g., Hedgehog or newer)
*   JDK 17 (Java Development Kit) - typically bundled with modern Android Studio versions.

### Cloning the Repository

```bash
git clone https://github.com/kozydot/nurse-charting-android
cd nurse-charting-android
```
### Building and Running the Application

1.  Open the project in Android Studio.
2.  Allow Gradle to sync and download dependencies. This might take a few minutes.
3.  Select an Android emulator or connect a physical Android device (ensure USB debugging is enabled).
4.  Click the "Run" button (green play icon) in Android Studio, or use the `Run > Run 'app'` menu option.

## Usage

Once the application is running:

1.  **Patient List:** The initial screen typically shows a list of patients. You can add new patients or select an existing patient to view their details.
2.  **Patient Charting:** After selecting a patient, you will be taken to their charting screen. This screen usually has bottom navigation to switch between different charting sections:
    *   **Tasks:** View, add, edit, filter, and sort tasks.
    *   **Vitals:** View and add vital signs.
    *   **Meds:** View and add administered medications.
    *   **Notes:** View and add nurse's notes.
    *   **I&O:** View and add input/output entries.
3.  **Adding Data:** Use the Floating Action Button (FAB) or specific "Add" buttons within each section to record new data.

## Contributing

Contributions are welcome! Please follow these basic guidelines:

*   Adhere to the existing coding style and patterns.
*   Ensure your code is well-commented, especially for complex logic.
*   Write unit tests for new functionality if applicable.
*   Submit pull requests for review.

## License

This project is licensed under the MIT License - see the [`LICENSE.md`](LICENSE.md) file for details.