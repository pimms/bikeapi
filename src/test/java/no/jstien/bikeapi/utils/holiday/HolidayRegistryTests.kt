package no.jstien.bikeapi.utils.holiday

import no.jstien.bikeapi.utils.TestDateParser
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

import java.util.ArrayList
import java.util.Calendar

class HolidayRegistryTests {
    private var holidayRepository: HolidayRepository? = null
    private var holidayRegistry: HolidayRegistry? = null

    @Before
    fun setup() {
        holidayRepository = Mockito.mock(HolidayRepository::class.java)
        val holidays = ArrayList<Holiday>()
        holidays.add(Holiday(TestDateParser.parseDate("2015-05-17"), "Grunnlovsdag"))
        holidays.add(Holiday(TestDateParser.parseDate("2015-05-01"), "Arbeidernes dag"))
        Mockito.`when`(holidayRepository!!.holidaysForCurrentYear).thenReturn(holidays)

        holidayRegistry = HolidayRegistry(holidayRepository!!)
    }

    @Test
    fun maySeventeenthIsAHoliday() {
        val maySeventeenth = TestDateParser.parseDate("2015-05-17")
        Assert.assertTrue(holidayRegistry!!.isHoliday(maySeventeenth))
    }

    @Test
    fun maySecondIsNotAHoliday() {
        val maySeventeenth = TestDateParser.parseDate("2015-05-02")
        Assert.assertFalse(holidayRegistry!!.isHoliday(maySeventeenth))
    }

    @Test
    fun maySeventeenthPlusOneIsNotAHoliday() {
        val date = TestDateParser.parseDate("2015-05-17")
        date.add(Calendar.DAY_OF_MONTH, 1)
        Assert.assertFalse(holidayRegistry!!.isHoliday(date))
    }

    @Test
    fun maySixteenthPlusOneIsAHoliday() {
        val date = TestDateParser.parseDate("2015-05-16")
        date.add(Calendar.DAY_OF_MONTH, 1)
        Assert.assertTrue(holidayRegistry!!.isHoliday(date))
    }

    @Test
    fun mayEighteenthPlusOneIsAHoliday() {
        val date = TestDateParser.parseDate("2015-05-18")
        date.add(Calendar.DAY_OF_MONTH, -1)
        Assert.assertTrue(holidayRegistry!!.isHoliday(date))
    }
}
