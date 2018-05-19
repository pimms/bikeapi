package no.jstien.bikeapi.tsdb;

public interface TSDB {
    DatumBuilder createDatumBuilder(String metricName);
    void addDatum(Datum datum);
}

