package no.jstien.bikeapi.station.api;

import java.util.List;

class AvailabilityCallDTO {
    private String updated_at;
    private double refreshRate;
    private List<StationAvailabilityDTO> stations;

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public double getRefresh_rate() {
        return refreshRate;
    }

    public void setRefresh_rate(double refreshRate) {
        this.refreshRate = refreshRate;
    }

    public List<StationAvailabilityDTO> getStations() {
        return stations;
    }

    public void setStations(List<StationAvailabilityDTO> stations) {
        this.stations = stations;
    }
}
