package no.jstien.bikeapi.tsdb.read;

public class StationHistory {
    private int stationId;
    private TimeSerie freeBikes;
    private TimeSerie freeLocks;

    StationHistory(int stationId) {
        this.stationId = stationId;
        this.freeBikes = new TimeSerie();
        this.freeBikes = new TimeSerie();
    }

    void setFreeBikes(TimeSerie freeBikes) {
        this.freeBikes = freeBikes;
    }

    void setFreeLocks(TimeSerie freeLocks) {
        this.freeLocks = freeLocks;
    }


    public int getStationId() {
        return stationId;
    }

    public TimeSerie getFreeBikes() {
        return freeBikes;
    }

    public TimeSerie getFreeLocks() {
        return freeLocks;
    }
}
