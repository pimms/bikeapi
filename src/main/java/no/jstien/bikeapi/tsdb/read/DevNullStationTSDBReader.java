package no.jstien.bikeapi.tsdb.read;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DevNullStationTSDBReader implements StationTSDBReader {
    @Override
    public Map<Integer,StationHistory> queryStations(RequestFactory requestFactory) {
        Map<Integer,StationHistory> result = new HashMap<>();

        Arrays.stream(requestFactory.getStationIds()).forEach(id -> {
            result.put(id, new StationHistory(id));
        });

        return result;
    }
}
