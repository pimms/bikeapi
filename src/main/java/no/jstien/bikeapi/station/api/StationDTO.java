package no.jstien.bikeapi.station.api;

import org.springframework.web.client.RestTemplate;

class StationDTO {
    private int id;
    private String title;
    private String subtitle;
    private int number_of_locks;
    private CoordinateDTO center;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getNumber_of_locks() {
        return number_of_locks;
    }

    public void setNumber_of_locks(int number_of_locks) {
        this.number_of_locks = number_of_locks;
    }

    public CoordinateDTO getCenter() {
        return center;
    }

    public void setCenter(CoordinateDTO center) {
        this.center = center;
    }

}
