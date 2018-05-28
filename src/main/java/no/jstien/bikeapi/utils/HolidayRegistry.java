package no.jstien.bikeapi.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

public class HolidayRegistry {
    private static final Logger LOG = LogManager.getLogger();

    private static final String WEBAPI_NO_URL = "https://webapi.no";
    private static final String HOLIDAY_PATH = "/api/v1/holydays/";

    private static class Holiday {
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

    private HttpClient httpClient;
    private List<Holiday> holidays = Collections.emptyList();

    public HolidayRegistry(HttpClient httpClient) {
        this.httpClient = httpClient;
        refreshRegistry();
    }

    public boolean isHoliday(Calendar date) {
        boolean result = holidays.stream()
                .filter(h -> h.getDate().equals(date))
                .count() > 0;
        return result;
    }


    @Scheduled(cron = "0 0 0 1 1 *")
    private void refreshRegistry() {
        try {
            HttpUriRequest request = createRequest();
            HttpResponse response = httpClient.execute(request);
            holidays = parseResponse(response);
            for (Holiday holiday : holidays) {
                LOG.info("HOLIDAY: {} ({})", holiday.getDate(), holiday.getDescription());
            }
        } catch (Exception e) {
            LOG.error("Failed to refresh holiday registry", e);
        }
    }

    private HttpUriRequest createRequest() {
        int year = ZonedDateTime.now().getYear();
        HttpUriRequest request = new HttpGet(WEBAPI_NO_URL + HOLIDAY_PATH + year);
        return request;
    }

    private List<Holiday> parseResponse(HttpResponse response) {
        throwIfBadStatus(response);

        try {
            String responseJson = EntityUtils.toString(response.getEntity(), "UTF-8");
            return parseJson(responseJson);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse response JSON", e);
        }
    }

    private void throwIfBadStatus(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("Server returned status " + statusCode);
        }
    }

    private List<Holiday> parseJson(String json) {
        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(json).getAsJsonObject();


        JsonArray data = root.get("data").getAsJsonArray();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        List<Holiday> holidays = new ArrayList<>();
        data.forEach(ele -> {
            JsonObject obj = ele.getAsJsonObject();

            try {
                String desc = obj.get("description").getAsString();
                String dateStr = obj.get("date").getAsString();

                GregorianCalendar date = new GregorianCalendar();
                date.setTime(dateFormat.parse(dateStr));
                CalendarUtils.clearTime(date);

                holidays.add(new Holiday(date, desc));
            } catch (Exception e) {
                LOG.error("Failed to parse date", e);
            }
        });

        return holidays;
    }

}
