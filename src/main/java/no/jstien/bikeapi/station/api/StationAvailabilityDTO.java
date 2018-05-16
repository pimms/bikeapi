package no.jstien.bikeapi.station.api;

class StationAvailabilityDTO {
    private int id;
    private AvailabilityDTO availability;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AvailabilityDTO getAvailability() {
        return availability;
    }

    public void setAvailability(AvailabilityDTO availability) {
        this.availability = availability;
    }
}

