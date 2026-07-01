package com.misrilibrary.tithi;

/**
 * Geographic coordinates and UTC offset for a city, used for sunrise/sunset
 * computation.
 *
 * <p><b>Internal — NOT part of the public API.</b> This type is package-private
 * (it used to live in {@code model}); callers specify places via
 * {@link Location} / {@link City} and the engine resolves coordinates itself,
 * mirroring the Dart engine where {@code CityLocation} lives under {@code src/}.
 */
final class CityLocation {
    private final double latitude;
    private final double longitude;
    private final double utcOffset;
    private final String region;

    CityLocation(double latitude, double longitude, double utcOffset) {
        this(latitude, longitude, utcOffset, null);
    }

    CityLocation(double latitude, double longitude, double utcOffset, String region) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.utcOffset = utcOffset;
        this.region = region;
    }

    /** Latitude in decimal degrees (positive = north). */
    double getLatitude() { return latitude; }

    /** Longitude in decimal degrees (positive = east). */
    double getLongitude() { return longitude; }

    /** Standard UTC offset in hours (e.g. 5.5 for IST, -8.0 for PST). */
    double getUtcOffset() { return utcOffset; }

    /**
     * Optional region/country qualifier for cities whose name collides with
     * another well-known place. {@code null} for self-qualifying names.
     * Display-only — does not affect any calculation.
     */
    String getRegion() { return region; }
}
