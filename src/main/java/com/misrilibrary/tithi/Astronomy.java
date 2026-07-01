package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.SunriseConvention;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Meeus astronomical algorithms for Sun/Moon ecliptic longitude and sunrise/sunset.
 *
 * <p>The Sun/Moon series are evaluated in Terrestrial Time (UT advanced by a
 * pure-Java Espenak &amp; Meeus delta-T), which fixes the dominant, time-growing
 * error of a UT-only evaluation. The Sun uses a truncated VSOP87 series (mean
 * ~1.5", max ~6.6" vs Swiss Ephemeris over 1900-2100) and the Moon carries the
 * same nutation term as the Sun so nutation cancels in the Moon-Sun elongation
 * (tithi) while the Moon's absolute longitude stays apparent.
 *
 * <p>This engine is intentionally identical, math-for-math, to the Dart
 * {@code tithi-engine-dart} astronomy engine, so the per-city correction tables
 * (generated as {@code Swiss - Meeus}) transfer exactly: {@code Meeus + corrections = Swiss}.
 */
class Astronomy {

    private static final double DEG2RAD = Math.PI / 180.0;
    private static final double RAD2DEG = 180.0 / Math.PI;

    public static double julianDay(LocalDateTime dt) {
        int y = dt.getYear();
        int m = dt.getMonthValue();
        double d = dt.getDayOfMonth() + dt.getHour() / 24.0 + dt.getMinute() / 1440.0 + dt.getSecond() / 86400.0;
        if (m <= 2) { y--; m += 12; }
        int a = y / 100;
        int b = 2 - a + a / 4;
        return (int) (365.25 * (y + 4716)) + (int) (30.6001 * (m + 1)) + d + b - 1524.5;
    }

    private static double T(double jd) { return (jd - 2451545.0) / 36525.0; }

    private static double norm360(double deg) { deg = deg % 360; return deg < 0 ? deg + 360 : deg; }

    /**
     * &Delta;T (TT &minus; UT) in <b>seconds</b> — Espenak &amp; Meeus (2006) polynomials.
     * The Meeus Sun/Moon series expect Terrestrial Time; UT must be advanced by &Delta;T.
     * MUST stay identical to the correction-table generator's delta-T.
     */
    static double deltaTSeconds(LocalDateTime dt) {
        double y = dt.getYear() + (dt.getMonthValue() - 0.5) / 12.0;
        double u;
        if (y < 1920) {
            double t = y - 1900;
            return -2.79 + 1.494119 * t - 0.0598939 * t * t + 0.0061966 * t * t * t - 0.000197 * t * t * t * t;
        } else if (y < 1941) {
            double t = y - 1920;
            return 21.20 + 0.84493 * t - 0.076100 * t * t + 0.0020936 * t * t * t;
        } else if (y < 1961) {
            double t = y - 1950;
            return 29.07 + 0.407 * t - t * t / 233 + t * t * t / 2547;
        } else if (y < 1986) {
            double t = y - 1975;
            return 45.45 + 1.067 * t - t * t / 260 - t * t * t / 718;
        } else if (y < 2005) {
            double t = y - 2000;
            return 63.86 + 0.3345 * t - 0.060374 * t * t + 0.0017275 * t * t * t
                    + 0.000651814 * t * t * t * t + 0.00002373599 * t * t * t * t * t;
        } else if (y < 2050) {
            double t = y - 2000;
            return 62.92 + 0.32217 * t + 0.005589 * t * t;
        } else if (y < 2150) {
            u = (y - 1820) / 100;
            return -20 + 32 * u * u - 0.5628 * (2150 - y);
        }
        u = (y - 1820) / 100;
        return -20 + 32 * u * u;
    }

    /** Julian Day in Terrestrial Time (UT advanced by &Delta;T) for the Meeus series. */
    private static double ttJulianDay(LocalDateTime dt) {
        return julianDay(dt) + deltaTSeconds(dt) / 86400.0;
    }

    // VSOP87D Earth longitude terms [A, B, C]; L = Σ A·cos(B + C·τ), τ in millennia.
    // Truncated set (~1-2" over 1900-2100). MUST match the generator's tables.
    private static final double[][] VSOP_L0 = {
        {175347046, 0, 0}, {3341656, 4.6692568, 6283.07585}, {34894, 4.6261, 12566.1517},
        {3497, 2.7441, 5753.3849}, {3418, 2.8289, 3.5231}, {3136, 3.6277, 77713.7715},
        {2676, 4.4181, 7860.4194}, {2343, 6.1352, 3930.2097}, {1324, 0.7425, 11506.7698},
        {1273, 2.0371, 529.691}, {1199, 1.1096, 1577.3435}, {990, 5.233, 5884.927},
        {902, 2.045, 26.298}, {857, 3.508, 398.149}, {780, 1.179, 5223.694},
        {753, 2.533, 5507.553}, {505, 4.583, 18849.228}, {492, 4.205, 775.523},
        {357, 2.920, 0.067}, {317, 5.849, 11790.629}, {284, 1.899, 796.298},
        {271, 0.315, 10977.079}, {243, 0.345, 5486.778}, {206, 4.806, 2544.314},
        {205, 1.869, 5573.143}, {202, 2.458, 6069.777}, {156, 0.833, 213.299},
        {132, 3.411, 2942.463}, {126, 1.083, 20.775}, {115, 0.645, 0.980},
        {103, 0.636, 4694.003}, {102, 0.976, 15720.839}, {102, 4.267, 7.114}
    };
    private static final double[][] VSOP_L1 = {
        {628331966747.0, 0, 0}, {206059, 2.678235, 6283.07585}, {4303, 2.6351, 12566.1517},
        {425, 1.590, 3.523}, {119, 5.796, 26.298}, {109, 2.966, 1577.344},
        {93, 2.59, 18849.23}, {72, 1.14, 529.69}, {68, 1.87, 398.15},
        {67, 4.41, 5507.55}, {59, 2.89, 5223.69}, {56, 2.17, 155.42},
        {45, 0.40, 796.30}, {36, 0.47, 775.52}, {29, 2.65, 7.11},
        {21, 5.34, 0.98}, {19, 1.85, 5486.78}, {19, 4.97, 213.30},
        {17, 2.99, 6275.96}, {16, 0.03, 2544.31}
    };
    private static final double[][] VSOP_L2 = {
        {52919, 0, 0}, {8720, 1.0721, 6283.0758}, {309, 0.867, 12566.152},
        {27, 0.05, 3.52}, {16, 5.19, 26.30}, {16, 3.68, 155.42},
        {10, 0.76, 18849.23}, {9, 2.06, 77713.77}, {7, 0.83, 775.52}, {5, 4.66, 1577.34}
    };
    private static final double[][] VSOP_L3 = {
        {289, 5.844, 6283.076}, {35, 0, 0}, {17, 5.49, 12566.15}
    };
    private static final double[][] VSOP_L4 = {
        {114, 3.142, 0}
    };

    private static double vsopSeries(double[][] terms, double tau) {
        double s = 0;
        for (double[] x : terms) {
            s += x[0] * Math.cos(x[1] + x[2] * tau);
        }
        return s;
    }

    /**
     * Sun's ecliptic longitude in degrees (tropical), apparent of date.
     * Geometric longitude from truncated VSOP87; aberration (-0.00569) and the
     * nutation main term (-0.00478·sin Ω) kept so nutation cancels in the
     * Moon-Sun elongation and the Sun stays apparent.
     */
    public static double sunLongitude(LocalDateTime dt) {
        double jdTT = ttJulianDay(dt);
        double t = T(jdTT);
        double tau = (jdTT - 2451545.0) / 365250.0;
        double l = (vsopSeries(VSOP_L0, tau)
                + vsopSeries(VSOP_L1, tau) * tau
                + vsopSeries(VSOP_L2, tau) * tau * tau
                + vsopSeries(VSOP_L3, tau) * tau * tau * tau
                + vsopSeries(VSOP_L4, tau) * tau * tau * tau * tau) / 1e8;
        double geo = norm360(l * RAD2DEG + 180.0);
        double omega = 125.04 - 1934.136 * t;
        return norm360(geo - 0.00569 - 0.00478 * Math.sin(omega * DEG2RAD));
    }

    /**
     * Moon's ecliptic longitude in degrees (tropical), apparent of date.
     * Meeus Ch. 47 (full Table 47.A longitude terms), evaluated in TT, with the
     * same nutation main term the Sun uses added so nutation cancels in the
     * Moon-Sun elongation (tithi).
     */
    public static double moonLongitude(LocalDateTime dt) {
        double t = T(ttJulianDay(dt));
        double t2 = t * t, t3 = t2 * t, t4 = t3 * t;
        double lp = norm360(218.3164477 + 481267.88123421 * t - 0.0015786 * t2 + t3 / 538841 - t4 / 65194000);
        double d = norm360(297.8501921 + 445267.1114034 * t - 0.0018819 * t2 + t3 / 545868 - t4 / 113065000) * DEG2RAD;
        double m = norm360(357.5291092 + 35999.0502909 * t - 0.0001536 * t2 + t3 / 24490000) * DEG2RAD;
        double mp = norm360(134.9633964 + 477198.8675055 * t + 0.0087414 * t2 + t3 / 69699 - t4 / 14712000) * DEG2RAD;
        double f = norm360(93.2720950 + 483202.0175233 * t - 0.0036539 * t2 - t3 / 3526000 + t4 / 863310000) * DEG2RAD;

        double sumL = 0;
        sumL += 6288774 * Math.sin(mp);
        sumL += 1274027 * Math.sin(2*d - mp);
        sumL += 658314 * Math.sin(2*d);
        sumL += 213618 * Math.sin(2*mp);
        sumL += -185116 * Math.sin(m);
        sumL += -114332 * Math.sin(2*f);
        sumL += 58793 * Math.sin(2*d - 2*mp);
        sumL += 57066 * Math.sin(2*d - m - mp);
        sumL += 53322 * Math.sin(2*d + mp);
        sumL += 45758 * Math.sin(2*d - m);
        sumL += -40923 * Math.sin(m - mp);
        sumL += -34720 * Math.sin(d);
        sumL += -30383 * Math.sin(m + mp);
        sumL += 15327 * Math.sin(2*d - 2*f);
        sumL += -12528 * Math.sin(mp + 2*f);
        sumL += 10980 * Math.sin(mp - 2*f);
        sumL += 10675 * Math.sin(4*d - mp);
        sumL += 10034 * Math.sin(3*mp);
        sumL += 8548 * Math.sin(4*d - 2*mp);
        sumL += -7888 * Math.sin(2*d + m - mp);
        sumL += -6766 * Math.sin(2*d + m);
        sumL += -5163 * Math.sin(d - mp);
        sumL += 4987 * Math.sin(d + m);
        sumL += 4036 * Math.sin(2*d - m + mp);
        sumL += 3994 * Math.sin(2*d + 2*mp);
        sumL += 3861 * Math.sin(4*d);
        sumL += 3665 * Math.sin(2*d - 3*mp);
        sumL += -2689 * Math.sin(m - 2*mp);
        sumL += -2602 * Math.sin(2*d - mp + 2*f);
        sumL += 2390 * Math.sin(2*d - m - 2*mp);
        sumL += -2348 * Math.sin(d + mp);
        sumL += 2236 * Math.sin(2*d - 2*m);
        sumL += -2120 * Math.sin(m + 2*mp);
        sumL += -2069 * Math.sin(2*m);
        sumL += 2048 * Math.sin(2*d - 2*m - mp);
        sumL += -1773 * Math.sin(2*d + mp - 2*f);
        sumL += -1595 * Math.sin(2*d + 2*f);
        sumL += 1215 * Math.sin(4*d - m - mp);
        sumL += -1110 * Math.sin(2*mp + 2*f);
        sumL += -892 * Math.sin(3*d - mp);
        sumL += -810 * Math.sin(2*d + m + mp);
        sumL += 759 * Math.sin(4*d - m - 2*mp);
        sumL += -713 * Math.sin(2*m - mp);
        sumL += -700 * Math.sin(2*d + 2*m - mp);
        sumL += 691 * Math.sin(2*d + m - 2*mp);
        sumL += 596 * Math.sin(2*d - m - 2*f);
        sumL += 549 * Math.sin(4*d + mp);
        sumL += 537 * Math.sin(4*mp);
        sumL += 520 * Math.sin(4*d - m);
        sumL += -487 * Math.sin(d - 2*mp);
        sumL += -399 * Math.sin(2*d + m - 2*f);
        sumL += -381 * Math.sin(2*mp - 2*f);
        sumL += 351 * Math.sin(d + m + mp);
        sumL += -340 * Math.sin(3*d - 2*mp);
        sumL += 330 * Math.sin(4*d - 3*mp);
        sumL += 327 * Math.sin(2*d - m + 2*mp);
        sumL += -323 * Math.sin(2*m + mp);
        sumL += 299 * Math.sin(d + m - mp);
        sumL += 294 * Math.sin(2*d + 3*mp);

        double a1 = norm360(119.75 + 131.849 * t) * DEG2RAD;
        double a2 = norm360(53.09 + 479264.290 * t) * DEG2RAD;
        sumL += 3958 * Math.sin(a1);
        sumL += 1962 * Math.sin(lp * DEG2RAD - f);
        sumL += 318 * Math.sin(a2);

        // Apparent longitude: add the same nutation main term the Sun uses so
        // nutation cancels in the Moon-Sun elongation (tithi).
        double omega = 125.04 - 1934.136 * t;
        double nutation = -0.00478 * Math.sin(omega * DEG2RAD);
        return norm360(lp + sumL / 1000000.0 + nutation);
    }

    /**
     * Sun rise/set UTC hours for {@code loc}/{@code convention}, evaluating the
     * Sun's position at {@code sunInstant}. {@code sign} = -1 for rise
     * (noon &minus; HA), +1 for set (noon + HA). Mirrors Dart's
     * {@code _riseSetUtcHours}.
     */
    private static double riseSetUtcHours(CityLocation loc, SunriseConvention convention,
                                          LocalDateTime sunInstant, double sign) {
        double t = (julianDay(sunInstant) - 2451545.0) / 36525.0;
        double sunLon = sunLongitude(sunInstant);
        double obliquity = (23.4393 - 0.0130 * t) * DEG2RAD;
        double sunLonRad = sunLon * DEG2RAD;
        double declination = Math.asin(Math.sin(obliquity) * Math.sin(sunLonRad));
        double latRad = loc.getLatitude() * DEG2RAD;
        double cosH = (Math.sin(convention.horizonAltitudeDeg() * DEG2RAD) - Math.sin(latRad) * Math.sin(declination))
                    / (Math.cos(latRad) * Math.cos(declination));
        // Clamp for polar regions (midnight sun / polar night).
        double hourAngle = Math.abs(cosH) > 1.0 ? Math.PI : Math.acos(Math.max(-1, Math.min(1, cosH)));
        double rightAsc = Math.atan2(Math.cos(obliquity) * Math.sin(sunLonRad), Math.cos(sunLonRad));
        double meanLon = norm360(280.46646 + 36000.76983 * t + 0.0003032 * t * t) * DEG2RAD;
        double eot = Math.atan2(Math.sin(meanLon - rightAsc), Math.cos(meanLon - rightAsc));
        double eotHours = eot * RAD2DEG / 15.0;
        double solarNoonUTC = 12.0 - loc.getLongitude() / 15.0 - eotHours;
        return solarNoonUTC + sign * (hourAngle * RAD2DEG / 15.0);
    }

    public static LocalDateTime computeSunrise(LocalDate date, CityLocation loc) {
        return computeSunrise(date, loc, SunriseConvention.UPPER_LIMB);
    }

    /**
     * Exact sunrise (UTC) for {@code date}/{@code loc}. The Sun's declination and
     * equation-of-time are refined <b>iteratively at the rise instant</b> (3
     * iterations, seeded at local noon), removing a latitude-growing error; the
     * result keeps full (sub-minute) resolution. Built via a {@link Duration}
     * offset from the UTC day start so a rise before/after the UTC day carries to
     * the correct calendar day. Mirrors Dart's {@code computeSunrise}.
     */
    public static LocalDateTime computeSunrise(LocalDate date, CityLocation loc, SunriseConvention convention) {
        return riseSet(date, loc, convention, -1.0);
    }

    public static LocalDateTime computeSunset(LocalDate date, CityLocation loc) {
        return computeSunset(date, loc, SunriseConvention.UPPER_LIMB);
    }

    /**
     * Exact sunset (UTC) — mirror of {@link #computeSunrise} with noon + hourAngle
     * instead of noon &minus; hourAngle.
     */
    public static LocalDateTime computeSunset(LocalDate date, CityLocation loc, SunriseConvention convention) {
        return riseSet(date, loc, convention, 1.0);
    }

    private static LocalDateTime riseSet(LocalDate date, CityLocation loc, SunriseConvention convention, double sign) {
        LocalDateTime base = date.atStartOfDay();
        LocalDateTime sunInstant = base.plusHours(12); // noon seed
        double hours = 0.0;
        for (int i = 0; i < 3; i++) {
            hours = riseSetUtcHours(loc, convention, sunInstant, sign);
            sunInstant = base.plus(Duration.ofMillis(Math.round(hours * 3600000)));
        }
        return base.plus(Duration.ofMillis(Math.round(hours * 3600000)));
    }

    /** Lahiri ayanamsha (sidereal correction). Calibrated to Swiss Ephemeris. */
    public static double ayanamsha(LocalDateTime dt) {
        double year = dt.getYear() + (dt.getMonthValue() - 1) / 12.0 + (dt.getDayOfMonth() - 1) / 365.25;
        double t = (year - 2000) / 100.0;
        return 23.8571 + 1.3970 * t + 0.0003 * t * t;
    }

    /** Convert tropical longitude to sidereal. */
    public static double toSidereal(double tropicalLon, LocalDateTime dt) {
        return norm360(tropicalLon - ayanamsha(dt));
    }

    /** Calculate tithi number (1-30) from sidereal Moon and Sun longitudes. */
    public static int calculateTithi(double moonSidereal, double sunSidereal) {
        double diff = (moonSidereal - sunSidereal) % 360;
        if (diff < 0) diff += 360;
        return (int) (diff / 12) + 1;
    }

    /** Get tithi at a specific UTC moment. */
    public static int tithiAt(LocalDateTime utcTime) {
        double sunLon = toSidereal(sunLongitude(utcTime), utcTime);
        double moonLon = toSidereal(moonLongitude(utcTime), utcTime);
        return calculateTithi(moonLon, sunLon);
    }
}
