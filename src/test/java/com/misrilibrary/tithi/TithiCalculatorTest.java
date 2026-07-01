package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TithiCalculatorTest {

    @Test
    void sunriseUjjainEquinox() {
        CityLocation loc = City.getLocation("Ujjain");
        LocalDateTime sr = Astronomy.computeSunrise(LocalDate.of(2026, 3, 20), loc);
        int localMin = sr.getHour() * 60 + sr.getMinute() + (int)(loc.getUtcOffset() * 60);
        assertTrue(localMin >= 385 && localMin <= 400, "Sunrise: " + localMin + " min");
    }

    @Test
    void tithiAtKnownDate() {
        Panchang panchang = new Panchang();
        TithiInfo info = panchang.tithiOnDate(LocalDate.of(2026, 3, 19), City.of("Ujjain"));
        assertEquals(30, info.getTithiNumber());
    }

    @Test
    void tithiAtPurnima() {
        Panchang panchang = new Panchang();
        TithiInfo info = panchang.tithiOnDate(LocalDate.of(2026, 7, 29), City.of("Ujjain"));
        assertEquals(15, info.getTithiNumber());
    }

    @Test
    void tokyoSunriseDayCarry() {
        CityLocation loc = City.getLocation("Tokyo");
        LocalDateTime sr = Astronomy.computeSunrise(LocalDate.of(2026, 6, 21), loc);
        LocalDateTime local = sr.plusMinutes((long)(loc.getUtcOffset() * 60));
        assertEquals(21, local.getDayOfMonth(), "Tokyo sunrise on wrong day");
        assertTrue(local.getHour() >= 4 && local.getHour() <= 7, "Tokyo sunrise hour: " + local.getHour());
    }

    @Test
    void forDateBasic() {
        Panchang panchang = new Panchang();
        TithiInfo info = panchang.tithiOnDate(LocalDate.of(2026, 3, 20), City.of("Ujjain"));
        assertEquals(2, info.getTithiNumber());
        assertEquals("Dwitiya", info.getTithiName());
        assertEquals(Paksha.SHUKLA, info.getPaksha());
    }
}
