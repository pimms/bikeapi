package no.jstien.bikeapi.station;

import java.io.Serializable;

public class Coordinate implements Serializable {
    public static double distanceInMeters(Coordinate coordA, Coordinate coordB) {
        double lat1 = coordA.getLatitude();
        double lat2 = coordB.getLatitude();
        double lon1 = coordA.getLongitude();
        double lon2 = coordB.getLongitude();

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    private final double latitude;
    private final double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceInMeters(Coordinate other) {
        return Coordinate.distanceInMeters(this, other);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
