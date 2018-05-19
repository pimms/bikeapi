package no.jstien.bikeapi.tsdb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpenTSDB implements TSDB {
    private static final Logger LOG = LogManager.getLogger();

    private List<Datum> datums;

    public OpenTSDB() {
        datums = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public DatumBuilder createDatumBuilder(String metricName) {
        return new DatumBuilder(metricName, this);
    }

    @Override
    public void addDatum(Datum datum) {
        // datums.add(datum);
        LOG.info("Got datum: {}", datum);
    }

}
