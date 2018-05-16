package no.jstien.bikeapi.station;

import com.sun.xml.internal.ws.developer.Serialization;

import java.io.Serializable;

public class Station implements Serializable {
    private final int id;
    private final int numberOfLocks;
    private final String title;
    private final String subtitle;
    private final Coordinate coordinate;

    private int freeBikes;
    private int freeLocks;


    public Station(int id, int numberOfLocks, String title, String subtitle, Coordinate coordinate) {
        this.id = id;
        this.numberOfLocks = numberOfLocks;
        this.title = title;
        this.subtitle = subtitle;
        this.coordinate = coordinate;
    }

    public int getId() {
        return id;
    }

    public int getNumberOfLocks() {
        return numberOfLocks;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }


    public int getFreeBikes() {
        return freeBikes;
    }

    public void setFreeBikes(int freeBikes) {
        this.freeBikes = freeBikes;
    }

    public int getFreeLocks() {
        return freeLocks;
    }

    public void setFreeLocks(int freeLocks) {
        this.freeLocks = freeLocks;
    }
}
