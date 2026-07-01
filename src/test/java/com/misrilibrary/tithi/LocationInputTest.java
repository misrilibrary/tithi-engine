package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Phase-1 lat/long Location input: 0.1° cell reuse + Meeus-only fallback. */
class LocationInputTest {

    private final Panchang p = new Panchang(MonthSystem.PURNIMANT);
    private final LocalDate date = LocalDate.of(2026, 1, 3);

    @Test @DisplayName("coordinates on a city cell == Location.city (Swiss-corrected)")
    void coordOnCityCell() {
        CityLocation s = City.getLocation("Seattle");
        TithiInfo byCity = p.at(Location.city("Seattle")).tithiOnDate(date);
        for (Location pt : new Location[]{
                Location.at(s.getLatitude(), s.getLongitude()),
                Location.at(s.getLatitude() + 0.01, s.getLongitude() - 0.01)}) {
            assertEquals(LocationSource.CITY_CORRECTED, pt.source());
            TithiInfo byCoord = p.at(pt).tithiOnDate(date);
            assertEquals(byCity.getTithiNumber(), byCoord.getTithiNumber());
            assertEquals(byCity.getDisplayName(), byCoord.getDisplayName());
        }
    }

    @Test @DisplayName("coordinate matching a stored city reproduces the name result")
    void coordMatchesCity() {
        CityLocation ny = City.getLocation("New York");
        TithiInfo byCoord = p.at(Location.at(ny.getLatitude(), ny.getLongitude())).tithiOnDate(date);
        assertEquals(p.tithiOnDate(date, City.of("New York")).getDisplayName(), byCoord.getDisplayName());
    }

    @Test @DisplayName("off-grid coordinate is Meeus-only and requires an offset")
    void offGridMeeus() {
        assertThrows(IllegalArgumentException.class, () -> Location.at(0.0, -140.0));
        Location raw = Location.at(0.0, -140.0, Duration.ofHours(-9));
        assertEquals(LocationSource.MEEUS_RAW, raw.source());
        TithiInfo info = p.at(raw).tithiOnDate(date);
        assertTrue(info.getTithiNumber() >= 1 && info.getTithiNumber() <= 30);
    }

    @Test @DisplayName("Allahabad/Prayagraj alias resolve to the same corrected cell")
    void aliasCell() {
        CityLocation a = City.getLocation("Allahabad");
        CityLocation pr = City.getLocation("Prayagraj");
        assertEquals(a.getLatitude(), pr.getLatitude());
        assertEquals(a.getLongitude(), pr.getLongitude());
        assertEquals(LocationSource.CITY_CORRECTED,
                Location.at(a.getLatitude(), a.getLongitude()).source());
    }
}
