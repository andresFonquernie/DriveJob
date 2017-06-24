package es.fonkyprojects.drivejob.model.local;

import java.io.Serializable;

public class MapLocation implements Serializable {
    private String address;
    private double longitude;
    private double latitude;

    public MapLocation() {
    }

    public MapLocation(String address, double longitude, double latitude) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
