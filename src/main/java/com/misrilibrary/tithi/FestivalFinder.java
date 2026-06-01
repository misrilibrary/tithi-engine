package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.Cities;
import com.misrilibrary.tithi.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Find festival dates applying muhurta rules (nishita/madhyahna/pradosh).
 * Always uses Purnimant internally since festival defs are in Purnimant convention.
 */
public class FestivalFinder {

    private final TithiFinder finder;

    public FestivalFinder(String city) {
        this.finder = new TithiFinder(MonthSystem.PURNIMANT, city);
    }

    /** Find the correct date for a festival in a given year. */
    public LocalDate findDate(FestivalDef fest, int year, String city) {
        List<LocalDate> dates = finder.findInYear(fest.month, fest.paksha, fest.tithiInPaksha, year, false);
        if (dates.isEmpty()) return null;

        LocalDate d = dates.get(0);

        // Apply muhurta rule: check if D-1 has the target tithi at muhurta time
        if (fest.muhurta != MuhurtaRule.SUNRISE) {
            CityLocation loc = Cities.getLocation(city);
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
}
