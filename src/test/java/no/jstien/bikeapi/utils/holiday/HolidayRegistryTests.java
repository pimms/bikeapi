package no.jstien.bikeapi.utils.holiday;

import no.jstien.bikeapi.utils.TestDateParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HolidayRegistryTests {
    private HolidayRepository holidayRepository;
    private HolidayRegistry holidayRegistry;

    @Before
    public void setup() {
        holidayRepository = Mockito.mock(HolidayRepository.class);
        List<Holiday> holidays = new ArrayList<>();
        holidays.add(new Holiday(TestDateParser.parseDate("2015-05-17"), "Grunnlovsdag"));
        holidays.add(new Holiday(TestDateParser.parseDate("2015-05-01"), "Arbeidernes dag"));
        Mockito.when(holidayRepository.getHolidaysForCurrentYear()).thenReturn(holidays);

        holidayRegistry = new HolidayRegistry(holidayRepository);
    }

    @Test
    public void maySeventeenthIsAHoliday() {
        Calendar maySeventeenth = TestDateParser.parseDate("2015-05-17");
        Assert.assertTrue(holidayRegistry.isHoliday(maySeventeenth));
    }

    @Test
    public void maySecondIsNotAHoliday() {
        Calendar maySeventeenth = TestDateParser.parseDate("2015-05-02");
        Assert.assertFalse(holidayRegistry.isHoliday(maySeventeenth));
    }

    @Test
    public void maySeventeenthPlusOneIsNotAHoliday() {
        Calendar date = TestDateParser.parseDate("2015-05-17");
        date.add(Calendar.DAY_OF_MONTH, 1);
        Assert.assertFalse(holidayRegistry.isHoliday(date));
    }

    @Test
    public void maySixteenthPlusOneIsAHoliday() {
        Calendar date = TestDateParser.parseDate("2015-05-16");
        date.add(Calendar.DAY_OF_MONTH, 1);
        Assert.assertTrue(holidayRegistry.isHoliday(date));
    }

    @Test
    public void mayEighteenthPlusOneIsAHoliday() {
        Calendar date = TestDateParser.parseDate("2015-05-18");
        date.add(Calendar.DAY_OF_MONTH, -1);
        Assert.assertTrue(holidayRegistry.isHoliday(date));
    }
}
