package no.jstien.bikeapi.tsdb.read

class StationHistory {
    var stationId: Int = 0
        private set
    var freeBikes: TimeSerie? = null
        internal set
    var freeLocks: TimeSerie? = null
        internal set

    constructor(stationId: Int) {
        this.stationId = stationId
        this.freeBikes = TimeSerie()
        this.freeBikes = TimeSerie()
    }

    constructor(stationId: Int, freeBikes: TimeSerie, freeLocks: TimeSerie) {
        this.stationId = stationId
        this.freeBikes = freeBikes
        this.freeLocks = freeLocks
    }
}
