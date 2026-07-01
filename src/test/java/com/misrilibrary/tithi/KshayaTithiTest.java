package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for kshaya tithi detection (tithi skipped at sunrise).
 * When a tithi is kshaya, getDates should return the previous day.
 */
class KshayaTithiTest {

    private final Panchang panchang = new Panchang(MonthSystem.PURNIMANT);

    @Test @DisplayName("Chaitra S1 2026 kshaya → returns Mar 19")
    void chaitraS1_2026() {
        List<LocalDate> dates = panchang.findDates(LunarMonth.CHAITRA, Tithi.shukla(1), 2026, City.of("Ujjain"));
        assertFalse(dates.isEmpty(), "Kshaya tithi should still return a date");
        assertEquals(LocalDate.of(2026, 3, 19), dates.get(0));
    }

    @Test @DisplayName("Jyeshtha S1 2025 kshaya → returns May 27")
    void jyeshthaS1_2025() {
        List<LocalDate> dates = panchang.findDates(LunarMonth.JYESHTHA, Tithi.shukla(1), 2025, City.of("Ujjain"));
        assertFalse(dates.isEmpty(), "Kshaya tithi should still return a date");
        assertEquals(LocalDate.of(2025, 5, 27), dates.get(0));
    }

    @Test @DisplayName("Kartika K1 2025 kshaya at span start → returns a date")
    void kartikaK1_2025() {
        List<LocalDate> dates = panchang.findDates(LunarMonth.KARTIKA, Tithi.krishna(1), 2025, City.of("Ujjain"));
        assertFalse(dates.isEmpty(), "Kshaya K1 at span start should return a date");
    }

    @Test @DisplayName("Normal tithi Chaitra S5 2026 still works")
    void normalTithi() {
        List<LocalDate> dates = panchang.findDates(LunarMonth.CHAITRA, Tithi.shukla(5), 2026, City.of("Ujjain"));
        assertFalse(dates.isEmpty());
    }

    @Test @DisplayName("Vriddhi Margashirsha S1 2026 returns second day Dec 10")
    void vriddhiTithi() {
        List<LocalDate> dates = panchang.findDates(LunarMonth.MARGASHIRSHA, Tithi.shukla(1), 2026, City.of("Ujjain"));
        assertTrue(dates.contains(LocalDate.of(2026, 12, 10)));
    }
}
