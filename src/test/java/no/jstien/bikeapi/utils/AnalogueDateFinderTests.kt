package no.jstien.bikeapi.utils

import no.jstien.bikeapi.utils.holiday.HolidayRegistry
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.TimeZone

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class AnalogueDateFinderTests {
    private var dateFinder: AnalogueDateFinder? = null
    private var holidayRegistry: HolidayRegistry? = null

    @Before
    fun setup() {
        holidayRegistry = mock(HolidayRegistry::class.java)
        `when`(holidayRegistry!!.isHoliday(any())).thenReturn(false)

        dateFinder = AnalogueDateFinder(holidayRegistry!!)
    }

    @Test
    fun weekdaysAreAnalogue() {
        val weekday = GregorianCalendar(2018, 5, 18)
        weekday.timeZone = TimeZone.getTimeZone("UTC")

        val analogues = dateFinder!!.findAnaloguesForDay(weekday)
        Assert.assertTrue(analogues.size >= AnalogueDateFinder.DEFAULT_MAX_ANALOGUE_HITS)

        analogues.forEach { day ->
            val dayOfWeek = day.get(Calendar.DAY_OF_WEEK)
            Assert.assertFalse(dayOfWeek == Calendar.SUNDAY)
            Assert.assertFalse(dayOfWeek == Calendar.SATURDAY)
        }
    }

    @Test
    fun weekendsAreAnalogue() {
        val weekend = GregorianCalendar(2018, 5, 17)
        weekend.timeZone = TimeZone.getTimeZone("UTC")

        val analogues = dateFinder!!.findAnaloguesForDay(weekend)
        Assert.assertTrue(analogues.size >= 2)

        analogues.forEach { day ->
            val dayOfWeek = day.get(Calendar.DAY_OF_WEEK)
            Assert.assertTrue(dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY)
        }
    }
}
