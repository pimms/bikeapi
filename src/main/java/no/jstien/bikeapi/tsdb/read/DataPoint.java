package no.jstien.bikeapi.tsdb.read;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPoint {
    private long timestamp;
    private long value;

    public DataPoint(long timestamp, long value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    @JsonProperty("ts")
    public long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("val")
    public long getValue() {
        return value;
    }
}
