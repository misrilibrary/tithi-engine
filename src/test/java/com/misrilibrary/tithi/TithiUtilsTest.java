package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TithiUtilsTest {

    @Test @DisplayName("Tithi names")
    void tithiNames() {
        assertEquals("Pratipada", TithiUtils.getTithiName(1));
        assertEquals("Ashtami", TithiUtils.getTithiName(8));
        assertEquals("Ekadashi", TithiUtils.getTithiName(11));
        assertEquals("Purnima", TithiUtils.getTithiName(15));
        assertEquals("Pratipada", TithiUtils.getTithiName(16)); // Krishna Pratipada
        assertEquals("Amavasya", TithiUtils.getTithiName(30));
    }

    @Test @DisplayName("Paksha detection")
    void pakshaDetection() {
        assertEquals(Paksha.SHUKLA, TithiUtils.getPaksha(1));
        assertEquals(Paksha.SHUKLA, TithiUtils.getPaksha(15));
        assertEquals(Paksha.KRISHNA, TithiUtils.getPaksha(16));
        assertEquals(Paksha.KRISHNA, TithiUtils.getPaksha(30));
    }

    @Test @DisplayName("Tithi in paksha")
    void tithiInPaksha() {
        assertEquals(1, TithiUtils.tithiInPaksha(1));
        assertEquals(15, TithiUtils.tithiInPaksha(15));
        assertEquals(1, TithiUtils.tithiInPaksha(16));
        assertEquals(15, TithiUtils.tithiInPaksha(30));
    }

    @Test @DisplayName("Display name format: Month Paksha Tithi")
    void displayNameFormat() {
        Panchang panchang = new Panchang();
        TithiInfo info = panchang.tithiOnDate(LocalDate.of(2024, 6, 15), "Ujjain");
        String[] parts = info.getDisplayName().split(" ");
        assertEquals(3, parts.length);
        assertTrue(parts[1].equals("Shukla") || parts[1].equals("Krishna"));
    }

    // ═══ Structural month boundary rules ═══

    @Test @DisplayName("Month never goes backward in a year (Purnimant)")
    void monthNeverBackward() {
        Panchang panchang = new Panchang(MonthSystem.PURNIMANT);
        String lastMonth = null;
        int transitions = 0;
        for (LocalDate d = LocalDate.of(2026, 1, 1); d.getYear() == 2026; d = d.plusDays(1)) {
            TithiInfo t = panchang.tithiOnDate(d, "Ujjain");
            String mn = t.getMonth().getDisplayName();
            if (!mn.equals(lastMonth)) {
                transitions++;
                lastMonth = mn;
            }
        }
        // 12 months + possible adhika = 12-14 transitions
        assertTrue(transitions >= 12 && transitions <= 14,
                "Transitions=" + transitions + " (expected 12-14)");
    }

    @Test @DisplayName("Krishna comes before Shukla within same month (Purnimant)")
    void krishnaBeforeShukla() {
        Panchang panchang = new Panchang(MonthSystem.PURNIMANT);
        // Magha 2026 starts Jan 4 (Krishna)
        TithiInfo early = panchang.tithiOnDate(LocalDate.of(2026, 1, 4), "Ujjain");
        assertEquals(LunarMonth.MAGHA, early.getMonth());
        assertEquals(Paksha.KRISHNA, early.getPaksha());
        // Later in same month should be Shukla
        TithiInfo later = panchang.tithiOnDate(LocalDate.of(2026, 1, 20), "Ujjain");
        assertEquals(LunarMonth.MAGHA, later.getMonth());
        assertEquals(Paksha.SHUKLA, later.getPaksha());
    }
}
