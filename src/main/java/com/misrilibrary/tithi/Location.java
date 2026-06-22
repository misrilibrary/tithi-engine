package com.misrilibrary.tithi;

import java.time.Duration;

/**
 * A place to compute for: either a registered city, or raw coordinates.
 *
 * <p>Pass to {@link Panchang#at(Location)}:
 * <pre>
 * panchang.at(Location.city("Seattle")).tithiOnDate(date);
 * panchang.at(Location.at(47.61, -122.33, Duration.ofHours(-8))).tithiOnDate(date);
 * </pre>
 *
 * <p>Coordinate resolution uses the <b>0.1&deg; cell</b> (cities are stored at
 * ~11&nbsp;km precision): a point that rounds into a supported city's cell reuses
 * that city wholesale (its coords, stored offset, and corrections) and reports
 * {@link LocationSource#CITY_CORRECTED}; a point outside every cell is Meeus-only
 * ({@link LocationSource#MEEUS_RAW}) and requires an {@code offset}.
 */
public final class Location {

    private final String key;
    private final LocationSource source;

    private Location(String key, LocationSource source) {
        this.key = key;
        this.source = source;
    }

    /**
     * Opaque internal key the engine resolves to coordinates + corrections
     * (a registered city name, or an ad-hoc coordinate key). Treat as opaque.
     */
    public String key() { return key; }

    /** Whether this location is Swiss-corrected or Meeus-only. */
    public LocationSource source() { return source; }

    /**
     * A registered city by name (case/space-insensitive, or {@code "City, Region"}).
     * Throws {@link IllegalArgumentException} on use if the name is not supported.
     */
    public static Location city(String name) {
        return new Location(name, LocationSource.CITY_CORRECTED);
    }

    /** Coordinates that must fall in a supported city's 0.1&deg; cell (else throws on use). */
    public static Location at(double lat, double lng) {
        return at(lat, lng, null);
    }

    /**
     * Raw coordinates. If (lat,lng) rounds into a supported city's 0.1&deg; cell that
     * city is used wholesale (Swiss-corrected) and {@code offset} is ignored; otherwise
     * the point is Meeus-only and {@code offset} (the DST-aware UTC offset) is required.
     */
    public static Location at(double lat, double lng, Duration offset) {
        String hit = City.cityForCell(lat, lng);
        if (hit != null) return new Location(hit, LocationSource.CITY_CORRECTED);
        if (offset == null) {
            throw new IllegalArgumentException(
                "offset is required for coordinates outside every supported city "
                + "(Meeus-only): pass the DST-aware UTC offset in effect.");
        }
        String key = City.registerAdHocLocation(lat, lng, offset.toMinutes() / 60.0);
        return new Location(key, LocationSource.MEEUS_RAW);
    }
}
