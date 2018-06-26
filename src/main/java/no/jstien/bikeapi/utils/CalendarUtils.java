package no.jstien.bikeapi.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalendarUtils {
    public static void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static GregorianCalendar startOfDay(Calendar date) {
        GregorianCalendar calendar = new GregorianCalendar(date.getTimeZone());
        calendar.set(Calendar.YEAR, date.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, date.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.toZonedDateTime();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar;
    }

    public static GregorianCalendar endOfDay(Calendar date) {
        GregorianCalendar calendar = new GregorianCalendar(date.getTimeZone());
        calendar.set(Calendar.YEAR, date.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, date.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.toZonedDateTime();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar;
    }

    public static GregorianCalendar copyCalendar(Calendar c) {
        GregorianCalendar calendar = new GregorianCalendar(c.getTimeZone());
        calendar.set(Calendar.YEAR, c.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, c.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, c.get(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, c.get(Calendar.MILLISECOND));
        calendar.toZonedDateTime();
        return calendar;
    }

    public static String calendarToString(Calendar calendar) {
        return calendar.toInstant().toString();
    }
}
