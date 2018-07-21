package no.jstien.bikeapi.station

import java.io.Serializable

class Coordinate(val latitude: Double, val longitude: Double) : Serializable {

    fun distanceInMeters(other: Coordinate): Double {
        return Coordinate.distanceInMeters(this, other)
    }

    companion object {
        fun distanceInMeters(coordA: Coordinate, coordB: Coordinate): Double {
            val lat1 = coordA.latitude
            val lat2 = coordB.latitude
            val lon1 = coordA.longitude
            val lon2 = coordB.longitude

            val theta = lon1 - lon2
            var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
            dist = Math.acos(dist)
            dist = rad2deg(dist)
            dist = dist * 60.0 * 1.1515
            dist = dist * 1609.344
            return dist
        }

        private fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }

        private fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI
        }
    }
}
