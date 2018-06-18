package no.jstien.bikeapi.utils.holiday;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import no.jstien.bikeapi.utils.CalendarUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class HolidayRepositoryImpl implements HolidayRepository {
    private static final String WEBAPI_NO_URL = "https://webapi.no";
    private static final String HOLIDAY_PATH = "/api/v1/holydays/";

    private static final Logger LOG = LogManager.getLogger();

    private HttpClient httpClient;
    private List<Holiday> holidays;
    private int cachedYear;

    public HolidayRepositoryImpl(HttpClient httpClient) {
        this.cachedYear = 0;
        this.holidays = Collections.emptyList();
        this.httpClient = httpClient;
    }

    @Override
    public List<Holiday> getHolidaysForCurrentYear() {
        // literally future proof
        if (cachedYear != getCurrentYear()) {
            refreshRegistry();
        }

        return Collections.unmodifiableList(holidays);
    }

    private void refreshRegistry() {
        try {
            HttpUriRequest request = createRequest();
            HttpResponse response = httpClient.execute(request);
            holidays = parseResponse(response);
            for (Holiday holiday : holidays) {
                LOG.debug("HOLIDAY: {} ({})", holiday.getDate(), holiday.getDescription());
            }
        } catch (Exception e) {
            LOG.error("Failed to refresh holiday registry", e);
        }
    }

    private HttpUriRequest createRequest() {
        return new HttpGet(WEBAPI_NO_URL + HOLIDAY_PATH + getCurrentYear());
    }

    private int getCurrentYear() {
        return ZonedDateTime.now().getYear();
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