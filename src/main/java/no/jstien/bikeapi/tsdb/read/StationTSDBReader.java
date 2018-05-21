package no.jstien.bikeapi.tsdb.read;

import java.util.Map;

public interface StationTSDBReader {
    Map<Integer,StationHistory> queryStations(RequestFactory requestFactory);
}
