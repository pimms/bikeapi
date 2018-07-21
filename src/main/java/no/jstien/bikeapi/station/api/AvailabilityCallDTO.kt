package no.jstien.bikeapi.station.api

internal class AvailabilityCallDTO {
    var updated_at: String? = null
    var refresh_rate: Double = 0.toDouble()
    var stations: List<StationAvailabilityDTO>? = null
}
