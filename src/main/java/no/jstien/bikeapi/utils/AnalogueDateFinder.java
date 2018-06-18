package no.jstien.bikeapi.utils;

import no.jstien.bikeapi.utils.holiday.HolidayRegistry;

import java.util.*;

public class AnalogueDateFinder {
    private HolidayRegistry holidayRegistry;

    public AnalogueDateFinder(HolidayRegistry holidayRegistry) {
        this.holidayRegistry = holidayRegistry;
    }

    public List<Calendar> findAnaloguesForToday() {
        Calendar anchor = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        CalendarUtils.clearTime(anchor);
        return findAnaloguesForDay(anchor);
    }

    public List<Calendar> findAnaloguesForDay(Calendar date) {
        return getCandidateDates(date);
    }

    private List<Calendar> getCandidateDates(Calendar anchor) {
        Calendar iterator = copyCalendar(anchor);

        List<Calendar> candidates = new ArrayList<>();

        // Find up to five analogue dates, matching as much as possible
        for (int i=0; i<30 && candidates.size() < 15; i++) {
            iterator.add(Calendar.DAY_OF_MONTH, -1);

            if (isDateInWeekend(iterator) == isDateInWeekend(anchor) && !holidayRegistry.isHoliday(iterator))
                candidates.add(copyCalendar(iterator));
        }

        return candidates;
    }

    private boolean isDateInWeekend(Calendar date) {
        int day = date.get(Calendar.DAY_OF_WEEK);
        return day == Calendar.SUNDAY || day == Calendar.SATURDAY;
    }

    private Calendar copyCalendar(Calendar c) {
        Calendar calendar = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar;
    }
}
