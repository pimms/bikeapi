package no.jstien.bikeapi.tsdb.write;

public interface TSDBWriter {
    DatumBuilder createDatumBuilder(String metricName);
    void addDatum(Datum datum);
}

