package com.misrilibrary.tithi.model;

/**
 * Which point of the Sun's disk defines the sunrise/sunset instant.
 *
 * <p>The value is the geometric altitude of the Sun's <b>center</b> (in degrees)
 * at the rise/set moment. Switching convention only shifts this one constant; the
 * rest of the rise/set math (declination, equation of time, hour angle) is
 * unchanged.
 *
 * <p>Mirrors the Dart {@code tithi-engine-dart} {@code SunriseConvention}.
 */
public enum SunriseConvention {

    /**
     * Upper limb of the disk tangent to the horizon — the classic "first ray"
     * definition (USNO/NOAA, and what Drik Panchang uses for Sūryodaya):
     * 34′ mean refraction + 16′ solar semidiameter = 50′ → −0.833°.
     *
     * <p>This is the library <b>default</b>; omitting the convention reproduces
     * the engine's original behavior byte-for-byte.
     */
    UPPER_LIMB(-0.833),

    /**
     * Center of the disk on the horizon — "half the disk visible". Refraction
     * only, no semidiameter term: 34′ → −0.5667°. Sits ~16′ (one semidiameter)
     * higher than {@link #UPPER_LIMB}, so sunrise is ~1–4 min later and sunset
     * ~1–4 min earlier (the exact delta grows with latitude).
     */
    CENTER_DISC(-0.5667);

    private final double horizonAltitudeDeg;

    SunriseConvention(double horizonAltitudeDeg) {
        this.horizonAltitudeDeg = horizonAltitudeDeg;
    }

    /** Geometric altitude of the Sun's center (degrees) at the rise/set instant. */
    public double horizonAltitudeDeg() {
        return horizonAltitudeDeg;
    }
}
