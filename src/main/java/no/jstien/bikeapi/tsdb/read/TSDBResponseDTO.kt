package no.jstien.bikeapi.tsdb.read

/**
 * This class is required because OpenTSDB has no concept of a 'station', and is not aware of the
 * fact that we consider the two metrics 'bikes.free' and 'locks.free' to be part of the same time
 * series. As such, we need this intermediate object (which captures either 'bikes.free' OR 'locks.free')
 * so we can join them into final StationHistory objects.
 */
internal class TSDBResponseDTO {
    var metric: String? = null
    var tags: Map<String, String>? = null
    var dps: TimeSerie? = null
}
