package com.example.nursecharting.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
// import android.R // For android.R.drawable.ic_dialog_info
import com.example.nursecharting.R // Import for app's resources
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    // private val repository: ChartingRepository // Inject if direct data fetching is needed
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(TASK_ID_KEY, -1L)
        val taskDescription = inputData.getString(TASK_DESC_KEY) ?: "Task Reminder"
        // val taskDueDate = inputData.getLong(TASK_DUE_DATE_KEY, 0L) // If needed

        if (taskId == -1L) {
            Log.e("TaskReminderWorker", "Invalid task ID received.")
            return Result.failure()
        }

        Log.d("TaskReminderWorker", "Worker executing for task ID: $taskId, Description: $taskDescription")

        try {
            val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel(notificationManager) // Ensure channel is created

            // TODO: Replace with actual icon from R.drawable.ic_notification_icon or similar
            // For now, using a system default if available or a placeholder that might cause a build error if not present.
            // This should be replaced with a proper icon in the app's drawables.
            val smallIcon = R.drawable.ic_task_notification // Using the app's custom icon

            val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
                .setContentTitle("Task Due: $taskDescription")
                .setContentText("Task ID: $taskId is due soon. Please check the app.") // Customize as per requirements
                .setSmallIcon(smallIcon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true) // Dismiss notification when tapped
                .build()

            // Using taskId.toInt() as notificationId. Ensure it's unique enough or manage IDs carefully.
            // If taskId can be very large, consider a different strategy for notificationId.
            notificationManager.notify(taskId.toInt(), notification)
            Log.d("TaskReminderWorker", "Notification displayed for task ID: $taskId")

        } catch (e: Exception) {
            Log.e("TaskReminderWorker", "Error displaying notification for task ID: $taskId", e)
            return Result.failure()
        }

        return Result.success()
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Reminders" // Channel name visible to user
            val descriptionText = "Notifications for upcoming tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                // Optionally, configure other channel properties like sound, vibration, etc.
                // enableLights(true)
                // lightColor = Color.RED
                // enableVibration(true)
                // vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d("TaskReminderWorker", "Notification channel '$CHANNEL_ID' created or already exists.")
        }
    }

    companion object {
        const val TASK_ID_KEY = "TASK_ID_KEY"
        const val TASK_DESC_KEY = "TASK_DESC_KEY"
        // const val TASK_DUE_DATE_KEY = "TASK_DUE_DATE_KEY" // If needed for notification content
        const val CHANNEL_ID = "TASK_REMINDER_CHANNEL_ID" // Unique ID for the notification channel
    }
}
