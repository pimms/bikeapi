package no.jstien.bikeapi.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.TimeZone

object TestDateParser {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    // Must be on format "yyyy-MM-dd"
    fun parseDate(dateString: String): Calendar {
        try {
            val calendar = GregorianCalendar(TimeZone.getTimeZone("UTC"))
            val date = dateFormat.parse(dateString)
            calendar.time = date
            return calendar
        } catch (e: Exception) {
            throw RuntimeException("Failed to parse I guess", e)
        }

    }
}
