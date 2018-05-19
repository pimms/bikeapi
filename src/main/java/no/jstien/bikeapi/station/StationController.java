package no.jstien.bikeapi.station;

import no.jstien.bikeapi.tsdb.DatumBuilder;
import no.jstien.bikeapi.tsdb.TSDB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StationController {
    private static Logger LOG = LogManager.getLogger();

    private StationRepository stationRepository;
    private DatumBuilder HTTP_CALL_METRIC;

    @Autowired
    public StationController(StationRepository stationRepository, TSDB tsdb) {
        this.stationRepository = stationRepository;
        HTTP_CALL_METRIC = tsdb.createDatumBuilder("http_calls").addTagKey("endpoint").addTagKey("method");
    }

    @RequestMapping("/stations")
    public List<Station> getAllStations() {
        HTTP_CALL_METRIC.addDatum("station_controller", "stations");
        return stationRepository.getAllStations();
    }

    @RequestMapping("/stations/closest")
    public Station getClosest(@RequestParam(value="lat") double lat,
                              @RequestParam(value="lon") double lon) {
        HTTP_CALL_METRIC.addDatum("station_controller", "stations/closest");
        Coordinate coord = new Coordinate(lat, lon);
        return stationRepository.getClosestStation(coord, false);
    }

    @RequestMapping("/stations/closestWithBikes")
    public Station getClosestWithBikes(@RequestParam(value="lat") double lat,
                              @RequestParam(value="lon") double lon) {
        HTTP_CALL_METRIC.addDatum("station_controller", "stations/closestWithBikes");
        Coordinate coord = new Coordinate(lat, lon);
        return stationRepository.getClosestStation(coord, true);
    }
}
