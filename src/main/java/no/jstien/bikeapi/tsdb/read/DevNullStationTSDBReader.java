package no.jstien.bikeapi.tsdb.read;

import no.jstien.bikeapi.tsdb.Datum;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

public class DevNullStationTSDBReader implements StationTSDBReader {
    @Override
    public List<Datum> queryStation(ZonedDateTime from, ZonedDateTime to, int... stationIds) {
        return Collections.emptyList();
    }
}
