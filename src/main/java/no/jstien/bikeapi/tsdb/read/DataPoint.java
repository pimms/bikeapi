package no.jstien.bikeapi.tsdb.read;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPoint {
    private long timestamp;
    private double value;

    public DataPoint(long timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    @JsonProperty("ts")
    public long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("val")
    public double getValue() {
        return value;
    }
}
