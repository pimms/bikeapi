package no.jstien.bikeapi.utils.holiday;

import java.util.List;

public interface HolidayRepository {
    List<Holiday> getHolidaysForCurrentYear();
}
