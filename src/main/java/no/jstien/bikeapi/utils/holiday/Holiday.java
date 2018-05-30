package no.jstien.bikeapi.utils.holiday;

import java.util.Calendar;

class Holiday {
    private Calendar date;
    private String description;

    public Holiday(Calendar date, String description) {
        this.date = date;
        this.description = description;
    }

    public Calendar getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
