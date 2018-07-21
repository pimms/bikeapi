package no.jstien.bikeapi.utils

import no.jstien.bikeapi.utils.holiday.HolidayRegistry

import java.util.*

import no.jstien.bikeapi.utils.CalendarUtils.copyCalendar

class AnalogueDateFinder(private val holidayRegistry: HolidayRegistry) {

    private var maxAnalogueHits = DEFAULT_MAX_ANALOGUE_HITS

    fun setMaxReturnLimit(maxLimit: Int) {
        this.maxAnalogueHits = maxLimit
    }

    fun findAnaloguesForToday(): List<Calendar> {
        val anchor = GregorianCalendar(TimeZone.getTimeZone("UTC"))
        CalendarUtils.clearTime(anchor)
        return findAnaloguesForDay(anchor)
    }

    fun findAnaloguesForDay(date: Calendar): List<Calendar> {
        return getCandidateDates(date)
    }

    private fun getCandidateDates(anchor: Calendar): List<Calendar> {
        val iterator = copyCalendar(anchor)

        val candidates = ArrayList<Calendar>()

        // Find up to five analogue dates, matching as much as possible
        var i = 0
        while (i < 30 && candidates.size < maxAnalogueHits) {
            iterator.add(Calendar.DAY_OF_MONTH, -1)

            if (isDateInWeekend(iterator) == isDateInWeekend(anchor) && !holidayRegistry.isHoliday(iterator))
                candidates.add(copyCalendar(iterator))
            i++
        }

        return candidates
    }

    private fun isDateInWeekend(date: Calendar): Boolean {
        val day = date.get(Calendar.DAY_OF_WEEK)
        return day == Calendar.SUNDAY || day == Calendar.SATURDAY
    }

    companion object {
        val DEFAULT_MAX_ANALOGUE_HITS = 5
    }
}
