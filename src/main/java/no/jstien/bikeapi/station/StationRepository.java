package no.jstien.bikeapi.station;

import java.util.List;
import java.util.Optional;

public interface StationRepository {
    List<Station> getAllStations();
    Optional<Station> getStationById(int id);
    Station getClosestStation(Coordinate coord, boolean requireAvailableBikes);
}
