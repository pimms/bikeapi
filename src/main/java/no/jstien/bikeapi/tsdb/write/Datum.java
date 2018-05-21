package no.jstien.bikeapi.tsdb.write;

import java.util.HashMap;
import java.util.Map;

class Datum {
    private String metric;
    private long value;
    private long timestamp;
    private Map<String,String> tags = new HashMap<>();

    Datum(String metric, long value, long timestamp) {
        this.metric = metric;
        this.value = value;
        this.timestamp = timestamp;
    }

    Datum(String metric, long value) {
        this.metric = metric;
        this.value = value;
        this.timestamp = System.currentTimeMillis() / 1000L;
    }

    public void addTag(String key, String value) {
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

