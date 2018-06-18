package no.jstien.bikeapi.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalendarUtils {
    public static void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static GregorianCalendar currentDate() {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        clearTime(calendar);
        return calendar;
    }

    public static GregorianCalendar startOfDay(Calendar date) {
        GregorianCalendar copy = copyCalendar(date);
        clearTime(copy);
        return copy;
    }

    public static GregorianCalendar endOfDay(Calendar date) {
        GregorianCalendar copy = copyCalendar(date);
        copy.set(Calendar.HOUR, 23);
        copy.set(Calendar.MINUTE, 59);
        copy.set(Calendar.SECOND, 59);
        copy.set(Calendar.MILLISECOND, 999);
        return copy;
    }

    public static GregorianCalendar copyCalendar(Calendar c) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR, c.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, c.get(Calendar.MONTH));
        calendar.set(Calendar.DATE, c.get(Calendar.DATE));
        return calendar;
    }

    public static String calendarToString(Calendar calendar) {
        return calendar.toInstant().toString();
    }
}
