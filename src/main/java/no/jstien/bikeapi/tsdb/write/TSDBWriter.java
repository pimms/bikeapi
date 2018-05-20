package no.jstien.bikeapi.tsdb.write;

import no.jstien.bikeapi.tsdb.Datum;

public interface TSDBWriter {
    DatumBuilder createDatumBuilder(String metricName);
    void addDatum(Datum datum);
}

