package com.example.nursecharting.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    private val systemZoneId: ZoneId = ZoneId.systemDefault()

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a", Locale.getDefault())
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

    fun toIsoFormattedDateTimeString(timeInMillis: Long): String {
        val instant = Instant.ofEpochMilli(timeInMillis)
        val localDateTime = LocalDateTime.ofInstant(instant, systemZoneId)
        return localDateTime.format(isoDateTimeFormatter)
    }
}

fun Long.toFormattedString(): String {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault()))
}