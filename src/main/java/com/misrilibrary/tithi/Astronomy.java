package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.CityLocation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Meeus astronomical algorithms for Sun/Moon ecliptic longitude and sunrise/sunset.
 */
public class Astronomy {

    private static final double DEG2RAD = Math.PI / 180.0;
    private static final double RAD2DEG = 180.0 / Math.PI;

    public static double julianDay(LocalDateTime dt) {
        int y = dt.getYear();
        int m = dt.getMonthValue();
        double d = dt.getDayOfMonth() + dt.getHour() / 24.0 + dt.getMinute() / 1440.0 + dt.getSecond() / 86400.0;
        if (m <= 2) { y--; m += 12; }
        int a = y / 100;
        int b = 2 - a + a / 4;
        return (int)(365.25 * (y + 4716)) + (int)(30.6001 * (m + 1)) + d + b - 1524.5;
    }

    private static double T(double jd) { return (jd - 2451545.0) / 36525.0; }

    private static double norm360(double deg) { deg = deg % 360; return deg < 0 ? deg + 360 : deg; }

    public static double sunLongitude(LocalDateTime dt) {
        double t = T(julianDay(dt));
        double l0 = norm360(280.46646 + 36000.76983 * t + 0.0003032 * t * t);
        double m = norm360(357.52911 + 35999.05029 * t - 0.0001537 * t * t);
        double mRad = m * DEG2RAD;
        double c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * Math.sin(mRad)
                 + (0.019993 - 0.000101 * t) * Math.sin(2 * mRad)
                 + 0.000289 * Math.sin(3 * mRad);
        double sunLon = norm360(l0 + c);
        double omega = 125.04 - 1934.136 * t;
        return norm360(sunLon - 0.00569 - 0.00478 * Math.sin(omega * DEG2RAD));
    }

    public static double moonLongitude(LocalDateTime dt) {
        double t = T(julianDay(dt));
        double t2 = t * t, t3 = t2 * t, t4 = t3 * t;
        double lp = norm360(218.3164477 + 481267.88123421 * t - 0.0015786 * t2 + t3 / 538841 - t4 / 65194000);
        double d = norm360(297.8501921 + 445267.1114034 * t - 0.0018819 * t2 + t3 / 545868 - t4 / 113065000) * DEG2RAD;
        double m = norm360(357.5291092 + 35999.0502909 * t - 0.0001536 * t2 + t3 / 24490000) * DEG2RAD;
        double mp = norm360(134.9633964 + 477198.8675055 * t + 0.0087414 * t2 + t3 / 69699 - t4 / 14712000) * DEG2RAD;
        double f = norm360(93.2720950 + 483202.0175233 * t - 0.0036539 * t2 - t3 / 3526000 + t4 / 863310000) * DEG2RAD;

        double sumL = 6288774 * Math.sin(mp) + 1274027 * Math.sin(2*d - mp) + 658314 * Math.sin(2*d)
            + 213618 * Math.sin(2*mp) - 185116 * Math.sin(m) - 114332 * Math.sin(2*f)
            + 58793 * Math.sin(2*d - 2*mp) + 57066 * Math.sin(2*d - m - mp) + 53322 * Math.sin(2*d + mp)
            + 45758 * Math.sin(2*d - m) - 40923 * Math.sin(m - mp) - 34720 * Math.sin(d)
            - 30383 * Math.sin(m + mp) + 15327 * Math.sin(2*d - 2*f) - 12528 * Math.sin(mp + 2*f)
            + 10980 * Math.sin(mp - 2*f) + 10675 * Math.sin(4*d - mp) + 10034 * Math.sin(3*mp)
            + 8548 * Math.sin(4*d - 2*mp) - 7888 * Math.sin(2*d + m - mp) - 6766 * Math.sin(2*d + m)
            - 5163 * Math.sin(d - mp) + 4987 * Math.sin(d + m) + 4036 * Math.sin(2*d - m + mp)
            + 3994 * Math.sin(2*d + 2*mp) + 3861 * Math.sin(4*d) + 3665 * Math.sin(2*d - 3*mp)
            - 2689 * Math.sin(m - 2*mp) - 2602 * Math.sin(2*d - mp + 2*f)
            + 2390 * Math.sin(2*d - m - 2*mp) - 2348 * Math.sin(d + mp)
            + 2236 * Math.sin(2*d - 2*m) - 2120 * Math.sin(m + 2*mp) - 2069 * Math.sin(2*m);

        double a1 = norm360(119.75 + 131.849 * t) * DEG2RAD;
        double a2 = norm360(53.09 + 479264.290 * t) * DEG2RAD;
        sumL += 3958 * Math.sin(a1) + 1962 * Math.sin(lp * DEG2RAD - f) + 318 * Math.sin(a2);

        return norm360(lp + sumL / 1000000.0);
    }

    public static LocalDateTime computeSunrise(LocalDate date, CityLocation loc) {
        LocalDateTime noon = date.atTime(12, 0);
        double jd = julianDay(noon);
        double t = (jd - 2451545.0) / 36525.0;
        double sunLon = sunLongitude(noon);
        double obliquity = (23.4393 - 0.0130 * t) * DEG2RAD;
        double sunLonRad = sunLon * DEG2RAD;
        double declination = Math.asin(Math.sin(obliquity) * Math.sin(sunLonRad));

        double latRad = loc.getLatitude() * DEG2RAD;
        double cosH = (Math.sin(-0.833 * DEG2RAD) - Math.sin(latRad) * Math.sin(declination))
                    / (Math.cos(latRad) * Math.cos(declination));
        double hourAngle = Math.abs(cosH) > 1.0 ? Math.PI : Math.acos(Math.max(-1, Math.min(1, cosH)));

        double rightAsc = Math.atan2(Math.cos(obliquity) * Math.sin(sunLonRad), Math.cos(sunLonRad));
        double meanLon = norm360(280.46646 + 36000.76983 * t + 0.0003032 * t * t) * DEG2RAD;
        double eot = Math.atan2(Math.sin(meanLon - rightAsc), Math.cos(meanLon - rightAsc));
        double eotHours = eot * RAD2DEG / 15.0;

        double solarNoonUTC = 12.0 - loc.getLongitude() / 15.0 - eotHours;
        double sunriseUTC = solarNoonUTC - (hourAngle * RAD2DEG / 15.0);

        long minutes = Math.round(sunriseUTC * 60);
        return date.atStartOfDay().plusMinutes(minutes);
    }

    public static LocalDateTime computeSunset(LocalDate date, CityLocation loc) {
        LocalDateTime noon = date.atTime(12, 0);
        double jd = julianDay(noon);
        double t = (jd - 2451545.0) / 36525.0;
        double sunLon = sunLongitude(noon);
        double obliquity = (23.4393 - 0.0130 * t) * DEG2RAD;
        double sunLonRad = sunLon * DEG2RAD;
        double declination = Math.asin(Math.sin(obliquity) * Math.sin(sunLonRad));

        double latRad = loc.getLatitude() * DEG2RAD;
        double cosH = (Math.sin(-0.833 * DEG2RAD) - Math.sin(latRad) * Math.sin(declination))
                    / (Math.cos(latRad) * Math.cos(declination));
        double hourAngle = Math.abs(cosH) > 1.0 ? Math.PI : Math.acos(Math.max(-1, Math.min(1, cosH)));

        double rightAsc = Math.atan2(Math.cos(obliquity) * Math.sin(sunLonRad), Math.cos(sunLonRad));
        double meanLon = norm360(280.46646 + 36000.76983 * t + 0.0003032 * t * t) * DEG2RAD;
        double eot = Math.atan2(Math.sin(meanLon - rightAsc), Math.cos(meanLon - rightAsc));
        double eotHours = eot * RAD2DEG / 15.0;

        double solarNoonUTC = 12.0 - loc.getLongitude() / 15.0 - eotHours;
        double sunsetUTC = solarNoonUTC + (hourAngle * RAD2DEG / 15.0);

        long minutes = Math.round(sunsetUTC * 60);
        return date.atStartOfDay().plusMinutes(minutes);
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
        return (int)(diff / 12) + 1;
    }

    /** Get tithi at a specific UTC moment. */
    public static int tithiAt(LocalDateTime utcTime) {
        double sunLon = toSidereal(sunLongitude(utcTime), utcTime);
        double moonLon = toSidereal(moonLongitude(utcTime), utcTime);
        return calculateTithi(moonLon, sunLon);
    }
}
