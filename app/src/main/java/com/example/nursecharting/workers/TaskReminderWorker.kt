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
import com.example.nursecharting.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(TASK_ID_KEY, -1L)
        val taskDescription = inputData.getString(TASK_DESC_KEY) ?: "Task Reminder"

        if (taskId == -1L) {
            Log.e("TaskReminderWorker", "Invalid task ID received.")
            return Result.failure()
        }

        Log.d("TaskReminderWorker", "Worker executing for task ID: $taskId, Description: $taskDescription")

        try {
            val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel(notificationManager)

            val smallIcon = R.drawable.ic_task_notification

            val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
                .setContentTitle("Task Due: $taskDescription")
                .setContentText("Task ID: $taskId is due soon. Please check the app.")
                .setSmallIcon(smallIcon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

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
            val name = "Task Reminders"
            val descriptionText = "Notifications for upcoming tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
            Log.d("TaskReminderWorker", "Notification channel '$CHANNEL_ID' created or already exists.")
        }
    }

    companion object {
        const val TASK_ID_KEY = "TASK_ID_KEY"
        const val TASK_DESC_KEY = "TASK_DESC_KEY"
        const val CHANNEL_ID = "TASK_REMINDER_CHANNEL_ID"
    }
}
