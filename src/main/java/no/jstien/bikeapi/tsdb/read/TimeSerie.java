package no.jstien.bikeapi.tsdb.read;

import java.util.ArrayList;
import java.util.List;

public class TimeSerie {
    private List<DataPoint> values;

    TimeSerie() {
        this.values = new ArrayList<>();
    }

    void addDataPoint(DataPoint dataPoint) {
        this.values.add(dataPoint);
    }

    public List<DataPoint> getDataPoints() {
        return values;
    }
}
