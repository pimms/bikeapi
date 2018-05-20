package no.jstien.bikeapi.tsdb.read;

import java.time.ZonedDateTime;
import java.util.Map;

public interface StationTSDBReader {
    StationHistory queryStation(ZonedDateTime from, ZonedDateTime to, int stationId);
    Map<Integer,StationHistory> queryStations(ZonedDateTime from, ZonedDateTime to, int ...stationIds);
}
