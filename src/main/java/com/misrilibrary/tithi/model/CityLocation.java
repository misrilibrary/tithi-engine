package com.misrilibrary.tithi.model;

/**
 * Geographic coordinates and UTC offset for a city, used for sunrise/sunset computation.
 */
public class CityLocation {
    private final double latitude;
    private final double longitude;
    private final double utcOffset;
    private final String region;

    public CityLocation(double latitude, double longitude, double utcOffset) {
        this(latitude, longitude, utcOffset, null);
    }

    public CityLocation(double latitude, double longitude, double utcOffset, String region) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.utcOffset = utcOffset;
        this.region = region;
    }

    /** Latitude in decimal degrees (positive = north). */
    public double getLatitude() { return latitude; }

    /** Longitude in decimal degrees (positive = east). */
    public double getLongitude() { return longitude; }

    /** Standard UTC offset in hours (e.g. 5.5 for IST, -8.0 for PST). */
    public double getUtcOffset() { return utcOffset; }

    /**
     * Optional region/country qualifier for cities whose name collides with
     * another well-known place (e.g. {@code "WA"} for Redmond, {@code "UK"} for
     * Birmingham). {@code null} for self-qualifying names (Singapore, Hong Kong,
     * Bahrain, Washington DC). Display-only — does not affect any calculation.
     */
    public String getRegion() { return region; }
}
