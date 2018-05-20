package no.jstien.bikeapi.tsdb.read;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

public class DevNullStationTSDBReader implements StationTSDBReader {
    @Override
    public StationHistory queryStation(ZonedDateTime from, ZonedDateTime to, int stationId) {
        return null;
    }

    @Override
    public Map<Integer,StationHistory> queryStations(ZonedDateTime from, ZonedDateTime to, int... stationIds) {
        return Collections.emptyMap();
    }
}
