package com.misrilibrary.tithi;

/** Accuracy tier of a resolved {@link Location}. */
public enum LocationSource {
    /**
     * A registered city (or a coordinate that fell in a city's 0.1&deg; cell) —
     * Swiss-Ephemeris correction tables applied.
     */
    CITY_CORRECTED,

    /**
     * Raw coordinates outside every supported city's cell — pure Meeus astronomy
     * (no correction table). ~99.97% accurate on day-assignment.
     */
    MEEUS_RAW
}
