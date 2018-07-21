package no.jstien.bikeapi.utils

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.TimeZone

object CalendarUtils {
    fun clearTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    fun startOfDay(date: Calendar): GregorianCalendar {
        val calendar = GregorianCalendar(date.timeZone)
        calendar.set(Calendar.YEAR, date.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, date.get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.toZonedDateTime()
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        return calendar
    }

    fun endOfDay(date: Calendar): GregorianCalendar {
        val calendar = GregorianCalendar(date.timeZone)
        calendar.set(Calendar.YEAR, date.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, date.get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        calendar.toZonedDateTime()
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        return calendar
    }

    fun copyCalendar(c: Calendar): GregorianCalendar {
        val calendar = GregorianCalendar(c.timeZone)
        calendar.set(Calendar.YEAR, c.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, c.get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, c.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, c.get(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, c.get(Calendar.MILLISECOND))
        calendar.toZonedDateTime()
        return calendar
    }

    fun calendarToString(calendar: Calendar): String {
        return calendar.toInstant().toString()
    }
}
