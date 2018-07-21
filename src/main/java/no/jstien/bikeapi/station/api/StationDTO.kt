package no.jstien.bikeapi.station.api

import org.springframework.web.client.RestTemplate

internal class StationDTO {
    var id: Int = 0
    var title: String? = null
    var subtitle: String? = null
    var number_of_locks: Int = 0
    var center: CoordinateDTO? = null

}
