package no.jstien.bikeapi.station.api;

import java.util.List;

class StationsCallDTO {
    private List<StationDTO> stations;

    public List<StationDTO> getStations() {
        return stations;
    }

    public void setStations(List<StationDTO> stations) {
        this.stations = stations;
    }
}
