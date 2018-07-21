package no.jstien.bikeapi.station

import no.jstien.bikeapi.station.api.BikeAPI
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.scheduling.annotation.Scheduled

import java.util.*
import java.util.stream.Stream

class StationRepositoryImpl(private val bikeAPI: BikeAPI) : StationRepository {
    private val stations: List<Station> = bikeAPI.getAllStations()

    init {
        this.stations.sortedBy { s -> s.id }
        updateAvailability()
    }

    override fun getAllStations(): List<Station> {
        return Collections.unmodifiableList(stations)
    }

    override fun getStationById(id: Int): Optional<Station> {
        return stations.stream().filter { s -> s.id == id }.findFirst()
    }

    override fun getClosestStation(coord: Coordinate, requireAvailableBikes: Boolean): Station {
        val distMap = HashMap<Station, Double>()

        for (station in stations) {
            distMap[station] = coord.distanceInMeters(station.coordinate)
        }

        var maxDist = java.lang.Double.MAX_VALUE
        var result: Station? = null

        for ((station, value) in distMap) {
            if (value < maxDist) {
                if (!requireAvailableBikes || station.freeBikes > 0) {
                    maxDist = value
                    result = station
                }
            }
        }

        return result!!
    }


    @Scheduled(fixedRateString = "\${station.refresh.poll-interval}", initialDelayString = "\${station.refresh.poll-interval}")
    private fun updateAvailability() {
        LOG.info("--> Updating station availability")
        val stationAvailabiliy = bikeAPI.getAvailabilities()
        updateWithExplicitLookup(stationAvailabiliy)
    }

    private fun updateWithExplicitLookup(stationAvailabiliy: List<StationAvailability>) {
        // TODO: This can be optimized heavily (given that the set of stations listed by the status API
        // is identical to the list retrieved by the get API), we can merely sort the two lists, and
        // solve this in O(1) time.
        stationAvailabiliy.forEach { avail ->
            getStationById(avail.id).ifPresent { station ->
                station.freeBikes = avail.availableBikes
                station.freeLocks = avail.availableLocks
            }
        }
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }

}
