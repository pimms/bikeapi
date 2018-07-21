package no.jstien.bikeapi.utils.holiday

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.Calendar

open class HolidayRegistry(private val holidayRepository: HolidayRepository) {

    open fun isHoliday(date: Calendar): Boolean {
        val holidays = holidayRepository.holidaysForCurrentYear
        return holidays.stream()
                .filter { h -> h.date == date }
                .count() > 0
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }

}
