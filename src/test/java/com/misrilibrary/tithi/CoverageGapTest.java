package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.CityCorrections;
import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Targeted tests to cover remaining branch/line gaps. */
class CoverageGapTest {

    private final Panchang panchang = new Panchang(MonthSystem.PURNIMANT);

    @Test @DisplayName("Adhika month getDates — exercises filterAdhikaMasa path")
    void getDateAdhikaMonth() {
        // Adhika Jyeshtha 2026: findDates for regular Jyeshtha should NOT return adhika dates
        List<LocalDate> dates = panchang.findDates(LunarMonth.JYESHTHA, Tithi.shukla(4), 2026, City.of("Ujjain"));
        assertFalse(dates.isEmpty());
        for (LocalDate d : dates) {
            TithiInfo info = panchang.tithiOnDate(d, City.of("Ujjain"));
            // Should find regular Jyeshtha, not adhika
            assertEquals(LunarMonth.JYESHTHA, info.getMonth());
        }
    }

    @Test @DisplayName("Year boundary date — exercises adjacent-year fallback in getMonthInfo")
    void yearBoundaryDate() {
        // Dec 31 and Jan 1 — may need adjacent year scan
        TithiInfo dec31 = panchang.tithiOnDate(LocalDate.of(2025, 12, 31), City.of("Ujjain"));
        TithiInfo jan1 = panchang.tithiOnDate(LocalDate.of(2026, 1, 1), City.of("Ujjain"));
        assertNotNull(dec31.getMonth());
        assertNotNull(jan1.getMonth());
    }

    @Test @DisplayName("TithiInfo.toString()")
    void tithiInfoToString() {
        TithiInfo info = panchang.tithiOnDate(LocalDate.of(2026, 3, 20), City.of("Ujjain"));
        assertEquals(info.getDisplayName(), info.toString());
    }

    @Test @DisplayName("Paksha.fromTithiNumber()")
    void pakshaFromTithiNumber() {
        assertEquals(Paksha.SHUKLA, Paksha.fromTithiNumber(1));
        assertEquals(Paksha.SHUKLA, Paksha.fromTithiNumber(15));
        assertEquals(Paksha.KRISHNA, Paksha.fromTithiNumber(16));
        assertEquals(Paksha.KRISHNA, Paksha.fromTithiNumber(30));
    }

    @Test @DisplayName("LunarMonthResolver single-arg constructor")
    void resolverDefaultCity() {
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.PURNIMANT);
        LunarMonthResolver.MonthInfo info = resolver.getMonthInfo(LocalDate.of(2026, 6, 15));
        assertNotNull(info.month);
    }

    @Test @DisplayName("CityCorrections getter methods")
    void cityCorrectionsGetters() {
        CityCorrections corr = CityCorrections.forCity("Ujjain");
        assertNotNull(corr.getTithiCorrections());
        assertNotNull(corr.getTransitionMinutes());
        assertNotNull(corr.getPurnimaCorrections());
        assertNotNull(corr.getAmavasyaCorrections());
    }

    @Test @DisplayName("CityCorrections for unknown city — empty maps")
    void cityCorrectionsUnknown() {
        CityCorrections corr = CityCorrections.forCity("NonexistentCity");
        assertTrue(corr.getTithiCorrections().isEmpty());
    }

    @Test @DisplayName("TithiUtils boundary: invalid tithi number")
    void tithiUtilsBoundary() {
        assertEquals("Unknown", TithiUtils.getTithiName(0));
        assertEquals("Unknown", TithiUtils.getTithiName(31));
    }

    @Test @DisplayName("City.getLocation throws for unknown city (no silent fallback)")
    void cityLocationUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> City.getLocation("NonexistentCity"));
    }

    @Test @DisplayName("Panchang.findDate returns a valid date")
    void getDateReturnsValid() {
        LocalDate date = panchang.findDate(LunarMonth.CHAITRA, Tithi.shukla(9), 2026, City.of("Ujjain"));
        assertNotNull(date);
        assertEquals(2026, date.getYear());
    }

    @Test @DisplayName("dateFor with SUNRISE muhurta — no shift")
    void dateForSunriseMuhurta() {
        // Ganesh Chaturthi uses SUNRISE — should not shift
        FestivalDate gcFd = panchang.dateFor(Festival.GANESH_CHATURTHI, 2026, City.of("Ujjain"));
        LocalDate date = gcFd == null ? null : gcFd.getDate();
        assertNotNull(date);
    }
}
