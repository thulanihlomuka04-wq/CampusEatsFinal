package com.campuseats.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility for formatting order and activity timestamps.
 */
object DateUtils {
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    fun formatTime(timestamp: Long): String = timeFormat.format(Date(timestamp))
    fun formatDateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))
}
