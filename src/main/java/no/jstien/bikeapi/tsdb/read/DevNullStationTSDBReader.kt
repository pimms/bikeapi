package no.jstien.bikeapi.tsdb.read

import java.util.Arrays
import java.util.HashMap

class DevNullStationTSDBReader : StationTSDBReader {
    override fun queryStations(requestFactory: RequestFactory): Map<Int, StationHistory> {
        val result = HashMap<Int, StationHistory>()

        Arrays.stream(requestFactory.stationIds).forEach { id -> result.put(id, StationHistory(id)) }

        return result
    }
}
