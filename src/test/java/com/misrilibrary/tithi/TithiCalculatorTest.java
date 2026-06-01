package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.Cities;
import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TithiCalculatorTest {

    @Test
    void sunriseUjjainEquinox() {
        CityLocation loc = Cities.getLocation("Ujjain");
        LocalDateTime sr = Astronomy.computeSunrise(LocalDate.of(2026, 3, 20), loc);
        // Ujjain equinox sunrise ~6:31 local = 1:01 UTC
        int localMin = sr.getHour() * 60 + sr.getMinute() + (int)(loc.getUtcOffset() * 60);
        assertTrue(localMin >= 385 && localMin <= 400, "Sunrise: " + localMin + " min");
    }

    @Test
    void tithiAtKnownDate() {
        // Mar 19, 2026 Ujjain = Chaitra Shukla Pratipada (T1)
        int tithi = Astronomy.tithiAt(Astronomy.computeSunrise(LocalDate.of(2026, 3, 19),
                Cities.getLocation("Ujjain")));
        assertEquals(1, tithi);
    }

    @Test
    void tithiAtPurnima() {
        // Jul 29, 2026 Ujjain = Purnima (T15)
        int tithi = Astronomy.tithiAt(Astronomy.computeSunrise(LocalDate.of(2026, 7, 29),
                Cities.getLocation("Ujjain")));
        assertEquals(15, tithi);
    }

    @Test
    void tokyoSunriseDayCarry() {
        // Tokyo sunrise should be on the correct local day
        CityLocation loc = Cities.getLocation("Tokyo");
        LocalDateTime sr = Astronomy.computeSunrise(LocalDate.of(2026, 6, 21), loc);
        LocalDateTime local = sr.plusMinutes((long)(loc.getUtcOffset() * 60));
        assertEquals(21, local.getDayOfMonth(), "Tokyo sunrise on wrong day");
        assertTrue(local.getHour() >= 4 && local.getHour() <= 7, "Tokyo sunrise hour: " + local.getHour());
    }

    @Test
    void getTithiBasic() {
        TithiCalculator calc = new TithiCalculator();
        TithiInfo info = calc.getTithi(LocalDate.of(2026, 3, 19), "Ujjain");
        assertEquals(1, info.getTithiNumber());
        assertEquals("Pratipada", info.getTithiName());
        assertEquals(Paksha.SHUKLA, info.getPaksha());
    }
}
