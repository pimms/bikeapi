package no.jstien.bikeapi.station.api

import no.jstien.bikeapi.station.Station
import no.jstien.bikeapi.station.StationAvailability

interface BikeAPI {
    fun getAllStations(): List<Station>
    fun getAvailabilities(): List<StationAvailability>
    fun areAllStationsClosed(): Boolean
}
