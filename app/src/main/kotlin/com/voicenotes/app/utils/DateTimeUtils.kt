package com.voicenotes.app.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy · hh:mm a")
    private val zoneId = ZoneId.systemDefault()

    fun formatDateTime(instant: Instant): String {
        val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
        return localDateTime.format(dateTimeFormatter)
    }

    fun formatDateOnly(instant: Instant): String {
        val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
        return localDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }

    fun formatTimeOnly(instant: Instant): String {
        val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
        return localDateTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    }
}
