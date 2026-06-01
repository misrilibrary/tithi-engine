package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.Cities;
import com.misrilibrary.tithi.data.CityCorrections;
import com.misrilibrary.tithi.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

        // TODO: Month resolution (LunarMonthResolver) — placeholder using simple approximation
        LunarMonth month = approximateMonth(date);
        boolean isAdhika = false;

        String pakshaStr = paksha == Paksha.SHUKLA ? "Shukla" : "Krishna";
        String display = month.getDisplayName() + " " + pakshaStr + " " + name;

        return new TithiInfo(tithiNum, name, paksha, inPaksha, month, isAdhika, display);
    }

    /** Placeholder month approximation until LunarMonthResolver is ported. */
    private LunarMonth approximateMonth(LocalDate date) {
        // Rough mapping: Chaitra ≈ Apr, Vaishakha ≈ May, etc.
        int m = (date.getMonthValue() + 8) % 12; // shift so Jan→Pausha(9)
        return LunarMonth.values()[m];
    }
}
