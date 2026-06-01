package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.Cities;
import com.misrilibrary.tithi.data.CityCorrections;
import com.misrilibrary.tithi.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Hindu lunar calendar (tithi/panchang) calculator.
 * Pure Java, no external dependencies. 109 cities supported.
 *
 * <pre>
 * TithiCalculator calc = new TithiCalculator();
 * TithiInfo info = calc.getTithi(LocalDate.of(2026, 2, 15), "Ujjain");
 * // → Phalguna Krishna Trayodashi
 * </pre>
 */
public class TithiCalculator {

    private static final LocalDate EPOCH = LocalDate.of(1900, 1, 1);
    private final MonthSystem monthSystem;

    public TithiCalculator() {
        this(MonthSystem.PURNIMANT);
    }

    public TithiCalculator(MonthSystem monthSystem) {
        this.monthSystem = monthSystem;
    }

    public MonthSystem getMonthSystem() { return monthSystem; }

    /**
     * Convert a Gregorian date to its Hindu lunar tithi.
     * Uses precomputed corrections (1900-2100) when available, Meeus fallback otherwise.
     */
    public TithiInfo getTithi(LocalDate date, String city) {
        CityLocation loc = Cities.getLocation(city);
        CityCorrections corr = CityCorrections.forCity(city);
        int dayIndex = (int) EPOCH.until(date, java.time.temporal.ChronoUnit.DAYS);

        // Tithi number: correction table first, Meeus fallback
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

        // Month resolution using city-specific resolver
        LunarMonthResolver resolver = getResolver(city);
        LunarMonthResolver.MonthInfo monthInfo = resolver.getMonthInfo(date);

        String pakshaStr = paksha == Paksha.SHUKLA ? "Shukla" : "Krishna";
        String adhikaPrefix = monthInfo.adhika ? "Adhika " : "";
        String display = adhikaPrefix + monthInfo.month.getDisplayName() + " " + pakshaStr + " " + name;

        return new TithiInfo(tithiNum, name, paksha, inPaksha, monthInfo.month, monthInfo.adhika, display);
    }

    private final Map<String, LunarMonthResolver> resolverCache = new HashMap<>();

    private LunarMonthResolver getResolver(String city) {
        return resolverCache.computeIfAbsent(city, c -> new LunarMonthResolver(monthSystem, c));
    }
}
