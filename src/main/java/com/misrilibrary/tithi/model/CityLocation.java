package com.misrilibrary.tithi.model;

public class CityLocation {
    private final double latitude;
    private final double longitude;
    private final double utcOffset;

    public CityLocation(double latitude, double longitude, double utcOffset) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.utcOffset = utcOffset;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getUtcOffset() { return utcOffset; }
}
