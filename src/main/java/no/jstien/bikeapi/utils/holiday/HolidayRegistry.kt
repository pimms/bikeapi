package no.jstien.bikeapi.utils.holiday

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.Calendar

class HolidayRegistry(private val holidayRepository: HolidayRepository) {

    fun isHoliday(date: Calendar): Boolean {
        val holidays = holidayRepository.holidaysForCurrentYear
        return holidays.stream()
                .filter { h -> h.date == date }
                .count() > 0
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }

}
