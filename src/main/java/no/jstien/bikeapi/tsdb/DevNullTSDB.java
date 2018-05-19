package no.jstien.bikeapi.tsdb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DevNullTSDB implements TSDB {
    Logger LOG = LogManager.getLogger();

    @Override
    public DatumBuilder createDatumBuilder(String metricName) {
        return new DatumBuilder(metricName, this);
    }

    @Override
    public void addDatum(Datum datum) {
        LOG.trace("DevNullTSDB -> discarding datum");
    }
}
