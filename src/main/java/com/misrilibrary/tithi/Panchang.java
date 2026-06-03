package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.CityCorrections;
import com.misrilibrary.tithi.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Hindu lunar calendar (panchang) — the single public entry point.
 *
 * <pre>
 * Panchang panchang = new Panchang(MonthSystem.PURNIMANT);
 * TithiInfo info = panchang.forDate(LocalDate.of(2026, 2, 15), City.UJJAIN);
 * LocalDate diwali = panchang.dateFor(Festival.DIWALI, 2026, City.SEATTLE);
 * </pre>
 */
public class Panchang {

    private static final LocalDate EPOCH = LocalDate.of(1900, 1, 1);
    private final MonthSystem monthSystem;
    private final Map<String, LunarMonthResolver> resolverCache = new HashMap<>();
    private final Map<String, TithiFinder> finderCache = new HashMap<>();

    public Panchang() { this(MonthSystem.PURNIMANT); }

    public Panchang(MonthSystem monthSystem) { this.monthSystem = monthSystem; }

    public MonthSystem getMonthSystem() { return monthSystem; }

    /** Convert a Gregorian date to its Hindu lunar tithi. */
    public TithiInfo forDate(LocalDate date, String city) {
        CityLocation loc = City.getLocation(city);
        CityCorrections corr = CityCorrections.forCity(city);
        int dayIndex = (int) EPOCH.until(date, java.time.temporal.ChronoUnit.DAYS);

        Integer corrected = corr.getCorrectedTithi(dayIndex);
        int tithiNum;
        if (corrected != null) {
            tithiNum = corrected;
        } else {
            LocalDateTime sunrise = Astronomy.computeSunrise(date, loc);
            tithiNum = Astronomy.tithiAt(sunrise);
        }

        Paksha paksha = TithiUtils.getPaksha(tithiNum);
        String name = TithiUtils.getTithiName(tithiNum);
        int inPaksha = TithiUtils.tithiInPaksha(tithiNum);

        LunarMonthResolver resolver = getResolver(city);
        LunarMonthResolver.MonthInfo monthInfo = resolver.getMonthInfo(date);

        String pakshaStr = paksha == Paksha.SHUKLA ? "Shukla" : "Krishna";
        String adhikaPrefix = monthInfo.adhika ? "Adhika " : "";
        String display = adhikaPrefix + monthInfo.month.getDisplayName() + " " + pakshaStr + " " + name;

        return new TithiInfo(tithiNum, name, paksha, inPaksha, monthInfo.month, monthInfo.adhika, display);
    }

    /** Find the Gregorian date for a tithi in a year. Returns first match. */
    public LocalDate getDate(LunarMonth month, Paksha paksha, int tithiInPaksha, int year, String city) {
        List<LocalDate> dates = getDates(month, paksha, tithiInPaksha, year, city);
        if (dates.isEmpty()) throw new NoSuchElementException(
                "No date found for " + month + " " + paksha + " " + tithiInPaksha + " in " + year);
        return dates.get(0);
    }

    /** Find all Gregorian dates for a tithi in a year (handles adhika months). */
    public List<LocalDate> getDates(LunarMonth month, Paksha paksha, int tithiInPaksha, int year, String city) {
        return getFinder(city).findInYear(month, paksha, tithiInPaksha, year, false);
    }

    /** Find the date of a festival in a given year, applying muhurta rules. */
    public LocalDate dateFor(Festival fest, int year, String city) {
        List<LocalDate> dates = getFinder(city).findInYear(fest.month, fest.paksha, fest.tithiInPaksha, year, false);
        if (dates.isEmpty()) return null;

        LocalDate d = dates.get(0);

        if (fest.muhurta != MuhurtaRule.SUNRISE) {
            CityLocation loc = City.getLocation(city);
            LocalDate prev = d.minusDays(1);
            LocalDateTime muhurtaTime = muhurtaUtc(prev, loc, fest.muhurta);
            int tithiAtMuhurta = Astronomy.tithiAt(muhurtaTime);
            if (tithiAtMuhurta == fest.getTithiNumber()) {
                d = prev;
            }
        }
        return d;
    }

    private LocalDateTime muhurtaUtc(LocalDate date, CityLocation loc, MuhurtaRule rule) {
        LocalDateTime sunrise = Astronomy.computeSunrise(date, loc);
        LocalDateTime sunset = Astronomy.computeSunset(date, loc);
        switch (rule) {
            case NISHITA:
                LocalDateTime nextSunrise = Astronomy.computeSunrise(date.plusDays(1), loc);
                long nightMin = java.time.Duration.between(sunset, nextSunrise).toMinutes();
                return sunset.plusMinutes(nightMin / 2);
            case MADHYAHNA:
                long dayMin = java.time.Duration.between(sunrise, sunset).toMinutes();
                return sunrise.plusMinutes(dayMin / 2);
            case PRADOSH:
                return sunset.plusMinutes(60);
            default:
                return sunrise;
        }
    }

    private LunarMonthResolver getResolver(String city) {
        return resolverCache.computeIfAbsent(city, c -> new LunarMonthResolver(monthSystem, c));
    }

    private TithiFinder getFinder(String city) {
        return finderCache.computeIfAbsent(city, c -> new TithiFinder(monthSystem, c));
    }
}
