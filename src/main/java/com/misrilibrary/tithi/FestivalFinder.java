package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Finds festival dates and recurring-tithi occurrences, applying muhurta rules.
 * Festival definitions use Purnimant month names, so finding always uses a
 * Purnimant finder internally. Mirrors the Dart {@code festival_finder}.
 */
class FestivalFinder {

    private static Instant utc(LocalDateTime dt) { return dt.toInstant(ZoneOffset.UTC); }

    /** Find the correct festival date for a year and city, applying muhurta rules. */
    static FestivalDate findFestivalDate(Festival fest, int year, String city, TithiFinder purnimantFinder) {
        return findFestivalDate(fest, year, city, purnimantFinder, SunriseConvention.UPPER_LIMB);
    }

    /** Find the correct festival date for a year and city, applying muhurta rules. */
    static FestivalDate findFestivalDate(Festival fest, int year, String city, TithiFinder purnimantFinder,
                                         SunriseConvention convention) {
        List<LocalDate> dates = purnimantFinder.findInYear(fest.month, fest.paksha, fest.tithiInPaksha, year, false);
        if (dates.isEmpty()) return null;

        CityLocation loc = City.getLocation(city);
        LocalDate d = dates.get(0);

        // Apply muhurta rule: if D-1 has the target tithi at the muhurta moment, shift back.
        if (fest.muhurta != MuhurtaRule.SUNRISE) {
            LocalDate prev = d.minusDays(1);
            LocalDateTime muhurtaTime = muhurtaUtc(prev, loc, fest.muhurta, convention);
            if (Astronomy.tithiAt(muhurtaTime) == fest.getTithiNumber()) {
                d = prev;
            }
        }

        int target = fest.getTithiNumber();
        Instant tithiStart = utc(findTithiTransition(d, loc, target, true, convention));
        Instant tithiEnd = utc(findTithiTransition(d, loc, target, false, convention));

        Instant muhurtaStart = null, muhurtaEnd = null;
        if (fest.muhurta != MuhurtaRule.SUNRISE) {
            LocalDateTime[] mw = muhurtaWindow(d, loc, fest.muhurta, convention);
            muhurtaStart = utc(mw[0]);
            muhurtaEnd = utc(mw[1]);
        }
        return new FestivalDate(fest, d, tithiStart, tithiEnd, muhurtaStart, muhurtaEnd);
    }

    /** Find all occurrences of a recurring tithi in a year (sunrise "last day" rule). */
    static List<FestivalDate> findRecurringDates(Festival fest, int year, String city) {
        return findRecurringDates(fest, year, city, SunriseConvention.UPPER_LIMB,
                new LunarMonthResolver(MonthSystem.PURNIMANT, city, SunriseConvention.UPPER_LIMB));
    }

    /**
     * Find all occurrences of a recurring tithi in a year (sunrise "last day" rule).
     * The occurrence month/adhika are resolved from {@code resolver} (mirroring
     * Dart's {@code dayTithi.month/isAdhika}), not the {@link Festival} placeholder.
     */
    static List<FestivalDate> findRecurringDates(Festival fest, int year, String city,
                                                 SunriseConvention convention, LunarMonthResolver resolver) {
        CityLocation loc = City.getLocation(city);
        int target = fest.getTithiNumber();
        List<FestivalDate> results = new ArrayList<>();
        LocalDate lastSeen = null;

        LocalDate end = LocalDate.of(year + 1, 1, 2);
        for (LocalDate d = LocalDate.of(year, 1, 1); d.isBefore(end); d = d.plusDays(1)) {
            if (d.getYear() != year && !(d.getYear() == year + 1 && d.getMonthValue() == 1 && d.getDayOfMonth() == 1)) {
                break;
            }
            int t = Astronomy.tithiAt(Astronomy.computeSunrise(d, loc, convention));
            if (t == target) {
                lastSeen = d;
            } else if (lastSeen != null) {
                results.add(buildRecurring(fest, lastSeen, loc, target, convention, resolver));
                lastSeen = null;
            }
        }
        if (lastSeen != null) results.add(buildRecurring(fest, lastSeen, loc, target, convention, resolver));
        return results;
    }

    private static FestivalDate buildRecurring(Festival fest, LocalDate date, CityLocation loc, int target,
                                               SunriseConvention convention, LunarMonthResolver resolver) {
        Instant ts = utc(findTithiTransition(date, loc, target, true, convention));
        Instant te = utc(findTithiTransition(date, loc, target, false, convention));
        // Resolve the ACTUAL occurrence month/adhika from the date (Dart uses
        // dayTithi.month/isAdhika), not the FestivalDef placeholder month.
        LunarMonthResolver.MonthInfo mi = resolver.getMonthInfo(date);
        return new FestivalDate(fest, date, ts, te, null, null, mi.month, mi.adhika);
    }

    /** Representative UTC moment for a muhurta rule on a date. */
    private static LocalDateTime muhurtaUtc(LocalDate date, CityLocation loc, MuhurtaRule rule,
                                            SunriseConvention convention) {
        LocalDateTime sunrise = Astronomy.computeSunrise(date, loc, convention);
        LocalDateTime sunset = Astronomy.computeSunset(date, loc, convention);
        switch (rule) {
            case NISHITA:
                LocalDateTime nextSunrise = Astronomy.computeSunrise(date.plusDays(1), loc, convention);
                long nightMin = Duration.between(sunset, nextSunrise).toMinutes();
                return sunset.plusMinutes(nightMin / 2);
            case MADHYAHNA:
                long dayMin = Duration.between(sunrise, sunset).toMinutes();
                return sunrise.plusMinutes(dayMin / 2);
            case PRADOSH:
                return sunset.plusMinutes(60);
            default:
                return sunrise;
        }
    }

    /** Muhurta window (start, end) as UTC LocalDateTimes. */
    private static LocalDateTime[] muhurtaWindow(LocalDate date, CityLocation loc, MuhurtaRule rule,
                                                 SunriseConvention convention) {
        LocalDateTime sunrise = Astronomy.computeSunrise(date, loc, convention);
        LocalDateTime sunset = Astronomy.computeSunset(date, loc, convention);
        switch (rule) {
            case NISHITA: {
                LocalDateTime nextSunrise = Astronomy.computeSunrise(date.plusDays(1), loc, convention);
                long nightMinutes = Duration.between(sunset, nextSunrise).toMinutes();
                // Nishita = the 8th of the night's 15 muhurtas (the central muhurta).
                long muhurta = nightMinutes / 15;
                return new LocalDateTime[]{ sunset.plusMinutes(muhurta * 7), sunset.plusMinutes(muhurta * 8) };
            }
            case MADHYAHNA: {
                long dayMinutes = Duration.between(sunrise, sunset).toMinutes();
                long part = dayMinutes / 5;
                return new LocalDateTime[]{ sunrise.plusMinutes(part * 2), sunrise.plusMinutes(part * 3) };
            }
            case PRADOSH:
                return new LocalDateTime[]{ sunset, sunset.plusMinutes(144) };
            default:
                return new LocalDateTime[]{ sunrise, sunrise };
        }
    }

    /**
     * Binary search for when the target tithi starts ({@code searchStart=true}) or
     * ends ({@code searchStart=false}). Searches a ±36h window around the date's
     * sunrise. Mirrors the Dart implementation.
     */
    private static LocalDateTime findTithiTransition(LocalDate date, CityLocation loc, int targetTithi,
                                                     boolean searchStart, SunriseConvention convention) {
        LocalDateTime sunrise = Astronomy.computeSunrise(date, loc, convention);
        LocalDateTime lo = sunrise.minusHours(36);
        LocalDateTime hi = sunrise.plusHours(36);

        if (searchStart) {
            while (Astronomy.tithiAt(lo) == targetTithi && lo.isAfter(sunrise.minusHours(48))) {
                lo = lo.minusHours(6);
            }
            if (Astronomy.tithiAt(lo) == targetTithi) return lo; // spans whole window
            hi = sunrise;
            if (Astronomy.tithiAt(hi) != targetTithi) hi = sunrise.plusHours(12);
            if (Astronomy.tithiAt(hi) != targetTithi) return sunrise; // fallback
            while (Duration.between(lo, hi).toMinutes() > 1) {
                LocalDateTime mid = lo.plusMinutes(Duration.between(lo, hi).toMinutes() / 2);
                if (Astronomy.tithiAt(mid) == targetTithi) hi = mid; else lo = mid;
            }
            return hi;
        } else {
            lo = sunrise;
            if (Astronomy.tithiAt(lo) != targetTithi) lo = sunrise.minusHours(12);
            hi = sunrise.plusHours(36);
            if (Astronomy.tithiAt(lo) != targetTithi) return sunrise.plusHours(24); // fallback
            while (Duration.between(lo, hi).toMinutes() > 1) {
                LocalDateTime mid = lo.plusMinutes(Duration.between(lo, hi).toMinutes() / 2);
                if (Astronomy.tithiAt(mid) == targetTithi) lo = mid; else hi = mid;
            }
            return hi;
        }
    }
}
