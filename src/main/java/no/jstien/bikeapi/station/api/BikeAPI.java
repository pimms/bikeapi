package no.jstien.bikeapi.station.api;

import no.jstien.bikeapi.station.Station;
import no.jstien.bikeapi.station.StationAvailability;

import java.util.List;

public interface BikeAPI {
    boolean areAllStationsClosed();
    List<Station> getAllStations();
    List<StationAvailability> getAvailabilities();
}
