package no.jstien.bikeapi.station

import java.io.Serializable

class Station(val id: Int, val numberOfLocks: Int, val title: String, val subtitle: String, val coordinate: Coordinate) : Serializable {

    var freeBikes: Int = 0
    var freeLocks: Int = 0
}
