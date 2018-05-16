package no.jstien.bikeapi.station.api;

class StatusCallDTO {
    private boolean all_stations_closed;
    /* TODO: Handle what happens when a single station is closed (doc does not say)  */

    public boolean getAll_stations_closed() {
        return all_stations_closed;
    }

    public void setAll_stations_closed(boolean all_stations_closed) {
        this.all_stations_closed = all_stations_closed;
    }
}
