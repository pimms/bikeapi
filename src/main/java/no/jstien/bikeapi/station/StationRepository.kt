package no.jstien.bikeapi.station

import java.util.Optional

interface StationRepository {
    fun getAllStations(): List<Station>
    fun getStationById(id: Int): Optional<Station>
    fun getClosestStation(coord: Coordinate, requireAvailableBikes: Boolean): Station
}
