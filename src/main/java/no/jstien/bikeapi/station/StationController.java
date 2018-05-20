package no.jstien.bikeapi.station;

import no.jstien.bikeapi.tsdb.read.StationHistory;
import no.jstien.bikeapi.tsdb.read.StationTSDBReader;
import no.jstien.bikeapi.tsdb.write.DatumBuilder;
import no.jstien.bikeapi.tsdb.write.TSDBWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
public class StationController {
    private static Logger LOG = LogManager.getLogger();

    private StationRepository stationRepository;
    private DatumBuilder httpCallMetric;
    private StationTSDBReader tsdbReader;

    @Autowired
    public StationController(StationRepository stationRepository, TSDBWriter tsdbWriter, StationTSDBReader stationTSDBReader) {
        this.stationRepository = stationRepository;
        this.httpCallMetric = tsdbWriter.createDatumBuilder("http_calls").addTagKey("endpoint").addTagKey("method");
        this.tsdbReader = stationTSDBReader;
    }

    @RequestMapping("/stations")
    public List<Station> getAllStations() {
        httpCallMetric.addDatum("station_controller", "stations");
        return stationRepository.getAllStations();
    }

    @RequestMapping("/stations/closest")
    public Station getClosest(@RequestParam(value="lat") double lat,
                              @RequestParam(value="lon") double lon) {
        httpCallMetric.addDatum("station_controller", "stations/closest");
        Coordinate coord = new Coordinate(lat, lon);
        return stationRepository.getClosestStation(coord, false);
    }

    @RequestMapping("/stations/closestWithBikes")
    public Station getClosestWithBikes(@RequestParam(value="lat") double lat,
                              @RequestParam(value="lon") double lon) {
        httpCallMetric.addDatum("station_controller", "stations/closestWithBikes");
        Coordinate coord = new Coordinate(lat, lon);
        return stationRepository.getClosestStation(coord, true);
    }

    @RequestMapping("/tsdb")
    public Collection<StationHistory> what() {
        Map<Integer,StationHistory> historyMap;
        historyMap = tsdbReader.queryStations(ZonedDateTime.now().minusHours(2), ZonedDateTime.now(), 272, 188);
        return historyMap.values();
    }

    @RequestMapping("/")
    public String test() {
        return "<html><body><h1>BikeAPI</h1><p>See https://github.com/pimms/bikeapi</p></body></html>";
    }
}
