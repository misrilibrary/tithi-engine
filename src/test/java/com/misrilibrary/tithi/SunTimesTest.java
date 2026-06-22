package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

/** Public sunrise/sunset exposure (Meeus, UTC instants). */
class SunTimesTest {

    private final Panchang p = new Panchang(MonthSystem.PURNIMANT);
    private final LocalDate d = LocalDate.of(2026, 6, 22);

    @Test
    void sunriseBeforeSunsetSensibleLocal() {
        var sr = p.sunrise(d, "New York");
        var ss = p.sunset(d, "New York");
        assertTrue(sr.isBefore(ss));
        int srH = LocalDateTime.ofInstant(sr, ZoneOffset.ofHours(-4)).getHour(); // EDT
        int ssH = LocalDateTime.ofInstant(ss, ZoneOffset.ofHours(-4)).getHour();
        assertTrue(srH >= 4 && srH <= 7, "sunrise hour " + srH);
        assertTrue(ssH >= 19 && ssH <= 21, "sunset hour " + ssH);
    }

    @Test
    void coordsOnCityCellMatchNamedCity() {
        CityLocation ny = City.getLocation("New York");
        var byCoord = p.at(Location.at(ny.getLatitude(), ny.getLongitude()));
        assertEquals(p.sunrise(d, "New York"), byCoord.sunrise(d));
        assertEquals(p.sunset(d, "New York"), byCoord.sunset(d));
    }

    @Test
    void unsupportedCityThrows() {
        assertThrows(IllegalArgumentException.class, () -> p.sunrise(d, "Nowhere"));
        assertThrows(IllegalArgumentException.class, () -> p.sunset(d, "Nowhere"));
    }
}
