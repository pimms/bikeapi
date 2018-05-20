package no.jstien.bikeapi.tsdb.read;

import com.google.gson.*;

import java.lang.reflect.Type;

public class TimeSerieJsonDeserializer implements JsonDeserializer<TimeSerie> {
    @Override
    public TimeSerie deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        TimeSerie timeSerie = new TimeSerie();

        jsonObject.keySet().forEach(key -> {
            long timestamp = Long.valueOf(key);
            long value = jsonObject.get(key).getAsLong();
            DataPoint dp = new DataPoint(timestamp, value);
            timeSerie.addDataPoint(dp);
        });

        return timeSerie;
    }
}

