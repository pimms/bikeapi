package no.jstien.bikeapi.station;

import no.jstien.bikeapi.station.api.BikeAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StationRepositoryImpl implements StationRepository {
    private static final Logger LOG = LogManager.getLogger();

    private BikeAPI bikeAPI;
    private List<Station> stations;

    public StationRepositoryImpl(BikeAPI bikeAPI) {
        this.bikeAPI = bikeAPI;
        this.stations = bikeAPI.getAllStations();
        this.stations.sort(Comparator.comparingInt(Station::getId));
        updateAvailability();
    }

    @Override
    public List<Station> getAllStations() {
        return Collections.unmodifiableList(stations);
    }

    @Override
    public Optional<Station> getStationById(int id) {
        return stations.stream().filter(s -> s.getId() == id).findFirst();
    }

    @Override
    public Station getClosestStation(Coordinate coord, boolean requireAvailableBikes) {
        Map<Station, Double> distMap = new HashMap<Station, Double>();

        for (Station station: stations) {
            distMap.put(station, coord.distanceInMeters(station.getCoordinate()));
        }

        double maxDist = Double.MAX_VALUE;
        Station result = null;

        for (Map.Entry<Station, Double> entry: distMap.entrySet()) {
            if (entry.getValue() < maxDist) {
                Station station = entry.getKey();
                if (!requireAvailableBikes || station.getFreeBikes() > 0) {
                    maxDist = entry.getValue();
                    result = entry.getKey();
                }
            }
        }

        return result;
    }


    @Scheduled(fixedRate = 30_000L, initialDelay = 30_000L)
    private void updateAvailability() {
        LOG.info("--> Updating station availability");
        List<StationAvailability> stationAvailabiliy = bikeAPI.getAvailabilities();
        Stream<Station> stationStream = stations.stream();
        updateWithExplicitLookup(stationAvailabiliy);
    }

    private void updateWithExplicitLookup(List<StationAvailability> stationAvailabiliy) {
        // TODO: This can be optimized heavily (given that the set of stations listed by the status API
        // is identical to the list retrieved by the get API), we can merely sort the two lists, and
        // solve this in O(1) time.
        stationAvailabiliy.forEach(avail -> {
            getStationById(avail.getId()).ifPresent(station -> {
                station.setFreeBikes(avail.getAvailableBikes());
                station.setFreeLocks(avail.getAvailableLocks());
            });
        });
    }

}
