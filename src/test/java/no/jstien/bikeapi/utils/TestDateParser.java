package no.jstien.bikeapi.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TestDateParser {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Must be on format "yyyy-MM-dd"
    public static Calendar parseDate(String dateString) {
        try {
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(dateString);
            calendar.setTime(date);
            return calendar;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse I guess", e);
        }
    }
}
