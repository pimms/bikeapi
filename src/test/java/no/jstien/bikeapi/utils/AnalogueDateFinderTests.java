package no.jstien.bikeapi.utils;

import no.jstien.bikeapi.utils.holiday.HolidayRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnalogueDateFinderTests {
    private AnalogueDateFinder dateFinder;
    private HolidayRegistry holidayRegistry;

    @Before
    public void setup() {
        holidayRegistry = mock(HolidayRegistry.class);
        when(holidayRegistry.isHoliday(any())).thenReturn(false);

        dateFinder = new AnalogueDateFinder(holidayRegistry);
    }

    @Test
    public void weekdaysAreAnalogue() {
        Calendar weekday = new GregorianCalendar(2018, 5, 18);
        weekday.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<Calendar> analogues = dateFinder.findAnaloguesForDay(weekday);
        Assert.assertTrue(analogues.size() > 10);

        analogues.forEach(day -> {
            int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
            Assert.assertFalse(dayOfWeek == Calendar.SUNDAY);
            Assert.assertFalse(dayOfWeek == Calendar.SATURDAY);
        });
    }

    @Test
    public void weekendsAreAnalogue() {
        Calendar weekend = new GregorianCalendar(2018, 5, 17);
        weekend.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<Calendar> analogues = dateFinder.findAnaloguesForDay(weekend);
        Assert.assertTrue(analogues.size() >= 2);

        analogues.forEach(day -> {
            int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
            Assert.assertTrue(dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY);
        });
    }
}
