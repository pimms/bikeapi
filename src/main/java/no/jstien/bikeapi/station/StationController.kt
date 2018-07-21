package no.jstien.bikeapi.station

import no.jstien.bikeapi.tsdb.write.DatumBuilder
import no.jstien.bikeapi.tsdb.write.TSDBWriter
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class StationController @Autowired
constructor(private val stationRepository: StationRepository, tsdbWriter: TSDBWriter) {
    private val httpCallMetric: DatumBuilder

    val allStations: List<Station>
        @RequestMapping("/stations")
        get() {
            httpCallMetric.addDatum("station_controller", "stations")
            return stationRepository.getAllStations()
        }

    init {
        this.httpCallMetric = tsdbWriter.createDatumBuilder("http_calls").addTagKey("endpoint").addTagKey("method")
    }

    @RequestMapping("/stations/closest")
    fun getClosest(@RequestParam(value = "lat") lat: Double,
                   @RequestParam(value = "lon") lon: Double): Station {
        httpCallMetric.addDatum("station_controller", "stations/closest")
        val coord = Coordinate(lat, lon)
        return stationRepository.getClosestStation(coord, false)
    }

    @RequestMapping("/stations/closestWithBikes")
    fun getClosestWithBikes(@RequestParam(value = "lat") lat: Double,
                            @RequestParam(value = "lon") lon: Double): Station {
        httpCallMetric.addDatum("station_controller", "stations/closestWithBikes")
        val coord = Coordinate(lat, lon)
        return stationRepository.getClosestStation(coord, true)
    }

    @RequestMapping("/")
    fun test(): String {
        return "<html><body><h1>BikeAPI</h1><p>See https://github.com/pimms/bikeapi</p></body></html>"
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }
}
