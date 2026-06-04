package com.misrilibrary.tithi.model;

/**
 * Geographic coordinates and UTC offset for a city, used for sunrise/sunset computation.
 */
public class CityLocation {
    private final double latitude;
    private final double longitude;
    private final double utcOffset;

    public CityLocation(double latitude, double longitude, double utcOffset) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.utcOffset = utcOffset;
    }

    /** Latitude in decimal degrees (positive = north). */
    public double getLatitude() { return latitude; }

    /** Longitude in decimal degrees (positive = east). */
    public double getLongitude() { return longitude; }

    /** Standard UTC offset in hours (e.g. 5.5 for IST, -8.0 for PST). */
    public double getUtcOffset() { return utcOffset; }
}
