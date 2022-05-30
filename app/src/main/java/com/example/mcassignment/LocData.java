package com.example.mcassignment;

public class LocData {

    private Double latitude;
    private Double longitude;

    public LocData() {
        latitude = 0.0;
        longitude = 0.0;
    }

    public LocData(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
