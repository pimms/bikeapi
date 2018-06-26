package no.jstien.bikeapi.utils;

import no.jstien.bikeapi.utils.holiday.HolidayRegistry;

import java.util.*;

import static no.jstien.bikeapi.utils.CalendarUtils.copyCalendar;

public class AnalogueDateFinder {
    public static final int DEFAULT_MAX_ANALOGUE_HITS = 5;

    private int maxAnalogueHits = DEFAULT_MAX_ANALOGUE_HITS;
    private HolidayRegistry holidayRegistry;

    public AnalogueDateFinder(HolidayRegistry holidayRegistry) {
        this.holidayRegistry = holidayRegistry;
    }

    public void setMaxReturnLimit(int maxLimit) {
        this.maxAnalogueHits= maxLimit;
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
        for (int i = 0; i<30 && candidates.size() < maxAnalogueHits; i++) {
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
}
