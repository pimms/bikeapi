package no.jstien.bikeapi.station;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class StationController {
    private static Logger LOG = LogManager.getLogger();

    private StationRepository stationRepository;

    @Autowired
    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @RequestMapping("/stations")
    public List<Station> getAllStations() {
        return stationRepository.getAllStations();
    }

    @RequestMapping("/closest")
    public Station getClosest(@RequestParam(value="lat") double lat,
                              @RequestParam(value="lon") double lon) {
        Coordinate coord = new Coordinate(lat, lon);
        return stationRepository.getClosestStation(coord, false);
    }

    @RequestMapping("/closestWithBikes")
    public Station getClosestWithBikes(@RequestParam(value="lat") double lat,
                              @RequestParam(value="lon") double lon) {
        Coordinate coord = new Coordinate(lat, lon);
        return stationRepository.getClosestStation(coord, true);
    }
}
