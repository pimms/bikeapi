package no.jstien.bikeapi.station.api;

class AvailabilityDTO {
    private int bikes;
    private int locks;

    public int getBikes() {
        return bikes;
    }

    public void setBikes(int bikes) {
        this.bikes = bikes;
    }

    public int getLocks() {
        return locks;
    }

    public void setLocks(int locks) {
        this.locks = locks;
    }
}
