package no.jstien.bikeapi.tsdb.read

interface StationTSDBReader {
    fun queryStations(requestFactory: RequestFactory): Map<Int, StationHistory>
}
