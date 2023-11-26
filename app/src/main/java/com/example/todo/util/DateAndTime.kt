package com.example.todo.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateAndTime {

      fun getCurrentDateTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    fun isTimestampToday(timestamp: Long): Boolean {
        val dt = Date(timestamp) // Convert seconds to milliseconds
        val cal = Calendar.getInstance()
        cal.time = dt
        val today = Calendar.getInstance()

        return cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
    }
}