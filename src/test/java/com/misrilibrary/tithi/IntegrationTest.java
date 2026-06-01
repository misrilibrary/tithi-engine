package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.Cities;
import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test matrix — exercises all workflows across cities × systems.
 */
class IntegrationTest {

    private final TithiCalculator calcP = new TithiCalculator(MonthSystem.PURNIMANT);
    private final TithiCalculator calcA = new TithiCalculator(MonthSystem.AMANT);

    // ═══ TEST 1: getTithi known dates ═══

    @Test @DisplayName("Mar 19, 2026 Ujjain = Pratipada (T1)")
    void tithiMar19() {
        TithiInfo info = calcP.getTithi(LocalDate.of(2026, 3, 19), "Ujjain");
        assertEquals(1, info.getTithiNumber());
        assertEquals("Pratipada", info.getTithiName());
        assertEquals(Paksha.SHUKLA, info.getPaksha());
    }

    @Test @DisplayName("Jul 29, 2026 Ujjain = Purnima (T15)")
    void tithiJul29() {
        TithiInfo info = calcP.getTithi(LocalDate.of(2026, 7, 29), "Ujjain");
        assertEquals(15, info.getTithiNumber());
    }

    @Test @DisplayName("May 20, 2026 Ujjain = Adhika Jyeshtha")
    void adhikMonth() {
        TithiInfo info = calcP.getTithi(LocalDate.of(2026, 5, 20), "Ujjain");
        assertEquals(LunarMonth.JYESHTHA, info.getMonth());
        assertTrue(info.isAdhika());
    }

    // ═══ TEST 2: System consistency ═══

    @Test @DisplayName("Purnimant and Amant give same tithi number")
    void systemConsistency() {
        for (String city : new String[]{"Ujjain", "Srinagar", "Seattle"}) {
            for (LocalDate dt : new LocalDate[]{
                    LocalDate.of(2026, 3, 20), LocalDate.of(2026, 5, 20),
                    LocalDate.of(2026, 9, 4), LocalDate.of(2026, 11, 9)}) {
                TithiInfo p = calcP.getTithi(dt, city);
                TithiInfo a = calcA.getTithi(dt, city);
                assertEquals(p.getTithiNumber(), a.getTithiNumber(),
                        city + " " + dt + ": P=T" + p.getTithiNumber() + " A=T" + a.getTithiNumber());
            }
        }
    }

    // ═══ TEST 3: Month boundaries ═══

    @Test @DisplayName("K.1 after Purnima = next month (Purnimant, 2026)")
    void monthBoundariesPurnimant() {
        for (String city : new String[]{"Ujjain", "Srinagar", "Seattle"}) {
            for (LocalDate d = LocalDate.of(2026, 1, 1); d.getYear() == 2026; d = d.plusDays(1)) {
                TithiInfo info = calcP.getTithi(d, city);
                if (info.getTithiNumber() == 15 && !info.isAdhika()) {
                    LocalDate next = d.plusDays(1);
                    TithiInfo nextInfo = calcP.getTithi(next, city);
                    if (nextInfo.getPaksha() == Paksha.KRISHNA) {
                        LunarMonth expected = info.getMonth().next();
                        assertEquals(expected, nextInfo.getMonth(),
                                city + " " + d + ": " + info.getMonth() + " -> " + nextInfo.getMonth());
                    }
                }
            }
        }
    }

    // ═══ TEST 4: Festival dates ═══

    @Test @DisplayName("Festival dates match Drik Panchang")
    void festivalDates() {
        FestivalFinder ff = new FestivalFinder("Ujjain");
        record Expected(String id, int year, String date) {}
        var truth = List.of(
            new Expected("maha_shivaratri", 2025, "2025-02-26"),
            new Expected("maha_shivaratri", 2026, "2026-02-15"),
            new Expected("ram_navami", 2025, "2025-04-06"),
            new Expected("ram_navami", 2026, "2026-03-26"),
            new Expected("diwali", 2025, "2025-10-20"),
            new Expected("diwali", 2026, "2026-11-08")
        );
        for (var exp : truth) {
            FestivalDef fest = FestivalDef.ALL.stream().filter(f -> f.id.equals(exp.id)).findFirst().orElseThrow();
            LocalDate got = ff.findDate(fest, exp.year, "Ujjain");
            assertNotNull(got, exp.id + " " + exp.year);
            assertEquals(exp.date, got.toString(), exp.id + " " + exp.year);
        }
    }

    // ═══ TEST 5: Sunrise sanity ═══

    @Test @DisplayName("Tokyo sunrise day-carry")
    void tokyoDayCarry() {
        CityLocation loc = Cities.getLocation("Tokyo");
        for (LocalDate dt : new LocalDate[]{
                LocalDate.of(2026, 6, 21), LocalDate.of(2026, 12, 21)}) {
            LocalDateTime sr = Astronomy.computeSunrise(dt, loc);
            LocalDateTime local = sr.plusMinutes((long)(loc.getUtcOffset() * 60));
            assertEquals(dt.getDayOfMonth(), local.getDayOfMonth(), "Tokyo " + dt + " sunrise on wrong day");
            assertTrue(local.getHour() >= 4 && local.getHour() <= 7, "Tokyo " + dt + " hour: " + local.getHour());
        }
    }

    @Test @DisplayName("Ujjain sunrise in reasonable range")
    void ujjainSunrise() {
        CityLocation loc = Cities.getLocation("Ujjain");
        LocalDateTime sr = Astronomy.computeSunrise(LocalDate.of(2026, 3, 20), loc);
        int localMin = sr.getHour() * 60 + sr.getMinute() + (int)(loc.getUtcOffset() * 60);
        assertTrue(localMin >= 385 && localMin <= 400, "Equinox sunrise: " + localMin);
    }

    // ═══ TEST 6: Round-trip ═══

    @Test @DisplayName("getTithi → findInYear returns consistent date")
    void roundTrip() {
        TithiFinder finder = new TithiFinder(MonthSystem.PURNIMANT, "Ujjain");
        LocalDate dt = LocalDate.of(2026, 8, 16);
        TithiInfo info = calcP.getTithi(dt, "Ujjain");
        List<LocalDate> found = finder.findInYear(info.getMonth(), info.getPaksha(),
                info.getTithiInPaksha(), 2026, info.isAdhika());
        assertFalse(found.isEmpty(), "No date found");
        // Verify found date has same tithi
        TithiInfo verify = calcP.getTithi(found.get(0), "Ujjain");
        assertEquals(info.getTithiNumber(), verify.getTithiNumber());
        assertEquals(info.getMonth(), verify.getMonth());
    }
}
