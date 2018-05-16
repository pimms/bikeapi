package no.jstien.bikeapi.station;

public class StationAvailability {
    public final int id;
    public final int availableBikes;
    public final int availableLocks;

    public StationAvailability(int id, int availableBikes, int availableLocks) {
        this.id = id;
        this.availableBikes = availableBikes;
        this.availableLocks = availableLocks;
    }

    public int getId() {
        return id;
    }

    public int getAvailableBikes() {
        return availableBikes;
    }

    public int getAvailableLocks() {
        return availableLocks;
    }
}
