package no.jstien.bikeapi.station.api;

import no.jstien.bikeapi.station.Station;
import no.jstien.bikeapi.station.StationAvailability;
import no.jstien.bikeapi.station.Coordinate;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class BikeAPIImpl implements BikeAPI {
    private RestTemplate restTemplate;

    public BikeAPIImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean areAllStationsClosed() {
        String url = "https://oslobysykkel.no/api/v1/status";
        StatusCallDTO status = restTemplate.getForObject(url, StatusCallDTO.class);
        return status.getAll_stations_closed();
    }

    @Override
    public List<Station> getAllStations() {
        String url = "https://oslobysykkel.no/api/v1/stations";
        StationsCallDTO stations = restTemplate.getForObject(url, StationsCallDTO.class);

        return stations.getStations().stream()
                .map(this::unwrapStation)
                .collect(Collectors.toList());
    }

    @Override
    public List<StationAvailability> getAvailabilities() {
        String url = "https://oslobysykkel.no/api/v1/stations/availability";
        AvailabilityCallDTO stations = restTemplate.getForObject(url, AvailabilityCallDTO.class);

        return stations.getStations().stream()
                .map(this::unwrapAvailability)
                .collect(Collectors.toList());
    }

    private Station unwrapStation(StationDTO dto) {
        return new Station(
                dto.getId(),
                dto.getNumber_of_locks(),
                dto.getTitle(),
                dto.getSubtitle(),
                new Coordinate(dto.getCenter().getLatitude(), dto.getCenter().getLongitude())
        );
    }

    private StationAvailability unwrapAvailability(StationAvailabilityDTO dto) {
        return new StationAvailability(
                dto.getId(),
                dto.getAvailability().getBikes(),
                dto.getAvailability().getLocks()
        );
    }


}
