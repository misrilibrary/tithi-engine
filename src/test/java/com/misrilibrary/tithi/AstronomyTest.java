package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AstronomyTest {

    @Test @DisplayName("Julian Day: J2000.0 epoch")
    void julianDayEpoch() {
        double jd = Astronomy.julianDay(LocalDateTime.of(2000, 1, 1, 12, 0));
        assertEquals(2451545.0, jd, 0.01);
    }

    @Test @DisplayName("Julian Day: Sputnik Oct 4.81, 1957")
    void julianDaySputnik() {
        double jd = Astronomy.julianDay(LocalDateTime.of(1957, 10, 4, 19, 26));
        assertEquals(2436116.31, jd, 0.01);
    }

    @Test @DisplayName("Sun longitude: vernal equinox ~0°")
    void sunLongitudeEquinox() {
        double lon = Astronomy.sunLongitude(LocalDateTime.of(2024, 3, 20, 12, 0));
        assertTrue(lon < 2 || lon > 358, "Equinox sun lon=" + lon);
    }

    @Test @DisplayName("Sun longitude: summer solstice ~90°")
    void sunLongitudeSolstice() {
        double lon = Astronomy.sunLongitude(LocalDateTime.of(2024, 6, 21, 12, 0));
        assertEquals(90, lon, 2);
    }

    @Test @DisplayName("Moon longitude: 0-360 range")
    void moonLongitudeRange() {
        double lon = Astronomy.moonLongitude(LocalDateTime.of(2024, 1, 15, 12, 0));
        assertTrue(lon >= 0 && lon < 360, "Moon lon=" + lon);
    }

    @Test @DisplayName("Moon moves ~13°/day")
    void moonDailyMotion() {
        double lon1 = Astronomy.moonLongitude(LocalDateTime.of(2024, 1, 15, 12, 0));
        double lon2 = Astronomy.moonLongitude(LocalDateTime.of(2024, 1, 16, 12, 0));
        double diff = (lon2 - lon1 + 360) % 360;
        assertEquals(13, diff, 2);
    }

    @Test @DisplayName("Ayanamsha: ~24° in 2024")
    void ayanamsha2024() {
        double ay = Astronomy.ayanamsha(LocalDateTime.of(2024, 1, 1, 0, 0));
        assertEquals(24.2, ay, 0.3);
    }

    @Test @DisplayName("Ayanamsha: increases over time")
    void ayanamshaIncreases() {
        double ay2000 = Astronomy.ayanamsha(LocalDateTime.of(2000, 1, 1, 0, 0));
        double ay2024 = Astronomy.ayanamsha(LocalDateTime.of(2024, 1, 1, 0, 0));
        assertTrue(ay2024 > ay2000);
    }

    @Test @DisplayName("calculateTithi: 0° diff = tithi 1")
    void tithiNewMoon() {
        assertEquals(1, Astronomy.calculateTithi(100, 100));
    }

    @Test @DisplayName("calculateTithi: just under 180° = tithi 15 (Purnima)")
    void tithiFullMoon() {
        assertEquals(15, Astronomy.calculateTithi(279.9, 100));
    }

    @Test @DisplayName("tithiAt: returns 1-30")
    void tithiAtRange() {
        int t = Astronomy.tithiAt(LocalDateTime.of(2026, 3, 20, 6, 0));
        assertTrue(t >= 1 && t <= 30, "tithiAt=" + t);
    }

    @Test @DisplayName("Consecutive days: tithi advances 0-2")
    void consecutiveTithis() {
        int t1 = Astronomy.tithiAt(LocalDateTime.of(2024, 6, 15, 6, 0));
        int t2 = Astronomy.tithiAt(LocalDateTime.of(2024, 6, 16, 6, 0));
        int diff = ((t2 - t1) % 30 + 30) % 30;
        assertTrue(diff <= 2, "Tithi jump=" + diff);
    }
}
