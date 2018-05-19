package no.jstien.bikeapi.tsdb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Datum implements Serializable {
    private String metric;
    private long value;
    private long timestamp;
    private Map<String,String> tags = new HashMap<>();

    public Datum(String metric, long value, long timestamp) {
        this.metric = metric;
        this.value = value;
        this.timestamp = timestamp;
    }

    public Datum(String metric, long value) {
        this.metric = metric;
        this.value = value;
        this.timestamp = System.currentTimeMillis() / 1000L;
    }

    void addTag(String key, String value) {
        tags.put(key, value);
    }

    public String getMetric() {
        return metric;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getValue() {
        return value;
    }


    @Override
    public String toString() {
        return String.format("{metric:'%s', value:'%d', timestamp:'%d' tags:'%s'", metric, value, timestamp, tags.toString());
    }
}

