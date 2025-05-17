package com.example.nursecharting.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    // Consider making locale and zoneId configurable or passed as parameters if needed
    private val systemZoneId: ZoneId = ZoneId.systemDefault()

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a", Locale.getDefault()) // Changed format for TaskItem
    private val isoDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())


    fun toFormattedDateString(timeInMillis: Long): String {
        val instant = Instant.ofEpochMilli(timeInMillis)
        val localDateTime = LocalDateTime.ofInstant(instant, systemZoneId)
        return localDateTime.format(dateFormatter)
    }

    fun toFormattedTimeString(timeInMillis: Long): String {
        val instant = Instant.ofEpochMilli(timeInMillis)
        val localDateTime = LocalDateTime.ofInstant(instant, systemZoneId)
        return localDateTime.format(timeFormatter)
    }

    fun toFormattedDateTimeString(timeInMillis: Long): String {
        val instant = Instant.ofEpochMilli(timeInMillis)
        val localDateTime = LocalDateTime.ofInstant(instant, systemZoneId)
        return localDateTime.format(dateTimeFormatter)
    }

    // Kept for compatibility if the specific "yyyy-MM-dd HH:mm" format is needed elsewhere directly
    fun toIsoFormattedDateTimeString(timeInMillis: Long): String {
        val instant = Instant.ofEpochMilli(timeInMillis)
        val localDateTime = LocalDateTime.ofInstant(instant, systemZoneId)
        return localDateTime.format(isoDateTimeFormatter)
    }
}

// Extension function updated to use java.time
fun Long.toFormattedString(): String {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    // Using the same format as the original extension for compatibility
    return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault()))
}