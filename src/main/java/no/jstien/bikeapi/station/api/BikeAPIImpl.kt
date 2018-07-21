package no.jstien.bikeapi.station.api

import no.jstien.bikeapi.station.Station
import no.jstien.bikeapi.station.StationAvailability
import no.jstien.bikeapi.station.Coordinate
import org.springframework.web.client.RestTemplate
import java.util.stream.Collectors

class BikeAPIImpl(private val restTemplate: RestTemplate) : BikeAPI {
    override fun areAllStationsClosed(): Boolean {
        val url = "https://oslobysykkel.no/api/v1/status"
        val status = restTemplate.getForObject(url, StatusCallDTO::class.java)
        return status!!.all_stations_closed
    }

    override fun getAllStations(): List<Station> {
        val url = "https://oslobysykkel.no/api/v1/stations"
        val stations = restTemplate.getForObject(url, StationsCallDTO::class.java)

        return stations!!.stations!!.stream()
                .map{ s -> unwrapStation(s) }
                .collect(Collectors.toList())
    }

    override fun getAvailabilities(): List<StationAvailability> {
        val url = "https://oslobysykkel.no/api/v1/stations/availability"
        val stations = restTemplate.getForObject(url, AvailabilityCallDTO::class.java)

        return stations!!.stations!!.stream()
                .map{ a -> this.unwrapAvailability(a) }
                .collect(Collectors.toList())
    }

    private fun unwrapStation(dto: StationDTO): Station {
        return Station(
                dto.id,
                dto.number_of_locks,
                dto.title!!,
                dto.subtitle!!,
                Coordinate(dto.center!!.latitude, dto.center!!.longitude)
        )
    }

    private fun unwrapAvailability(dto: StationAvailabilityDTO): StationAvailability {
        return StationAvailability(
                dto.id,
                dto.availability!!.bikes,
                dto.availability!!.locks
        )
    }


}
