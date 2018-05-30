package no.jstien.bikeapi.utils.holiday;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.List;

public class HolidayRegistry {
    private static final Logger LOG = LogManager.getLogger();

    private HolidayRepository holidayRepository;

    public HolidayRegistry(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public boolean isHoliday(Calendar date) {
        List<Holiday> holidays = holidayRepository.getHolidaysForCurrentYear();
        boolean result = holidays.stream()
                .filter(h -> h.getDate().equals(date))
                .count() > 0;
        return result;
    }

}
