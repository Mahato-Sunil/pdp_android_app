package com.officialsunil.pdpapplication.utils

import com.google.firebase.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object CustomDateTimeFormatter {
    fun formatDateTime(timestamp: String): String {
        val regex = Regex("""seconds=(\d+), nanoseconds=(\d+)""")
        val matchResult = regex.find(timestamp)

        if (matchResult != null) {
            val (secondsStr, nanosStr) = matchResult.destructured
            val seconds = secondsStr.toLong()
            val nanoseconds = nanosStr.toLong()

            // Convert to Instant
            val instant = Instant.ofEpochSecond(seconds, nanoseconds)

            // Format the Instant
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy EEEE hh:mm a")
                .withZone(ZoneId.systemDefault())
            val formattedTime = formatter.format(instant)
            return formattedTime
        } else
            return "Invalid Format"
    }
}