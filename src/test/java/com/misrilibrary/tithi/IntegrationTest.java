package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    private final Panchang panchangP = new Panchang(MonthSystem.PURNIMANT);
    private final Panchang panchangA = new Panchang(MonthSystem.AMANT);

    @Test @DisplayName("Mar 19, 2026 Ujjain = Amavasya (T30)")
    void tithiMar19() {
        TithiInfo info = panchangP.forDate(LocalDate.of(2026, 3, 19), "Ujjain");
        assertEquals(30, info.getTithiNumber());
        assertEquals("Amavasya", info.getTithiName());
        assertEquals(Paksha.KRISHNA, info.getPaksha());
    }

    @Test @DisplayName("Jul 29, 2026 Ujjain = Purnima (T15)")
    void tithiJul29() {
        TithiInfo info = panchangP.forDate(LocalDate.of(2026, 7, 29), "Ujjain");
        assertEquals(15, info.getTithiNumber());
    }

    @Test @DisplayName("May 20, 2026 Ujjain = Adhika Jyeshtha")
    void adhikMonth() {
        TithiInfo info = panchangP.forDate(LocalDate.of(2026, 5, 20), "Ujjain");
        assertEquals(LunarMonth.JYESHTHA, info.getMonth());
        assertTrue(info.isAdhika());
    }

    @Test @DisplayName("Purnimant and Amant give same tithi number")
    void systemConsistency() {
        for (String city : new String[]{"Ujjain", "Srinagar", "Seattle"}) {
            for (LocalDate dt : new LocalDate[]{
                    LocalDate.of(2026, 3, 20), LocalDate.of(2026, 5, 20),
                    LocalDate.of(2026, 9, 4), LocalDate.of(2026, 11, 9)}) {
                TithiInfo p = panchangP.forDate(dt, city);
                TithiInfo a = panchangA.forDate(dt, city);
                assertEquals(p.getTithiNumber(), a.getTithiNumber(),
                        city + " " + dt + ": P=T" + p.getTithiNumber() + " A=T" + a.getTithiNumber());
            }
        }
    }

    @Test @DisplayName("K.1 after Purnima = next month (Purnimant, 2026)")
    void monthBoundariesPurnimant() {
        for (String city : new String[]{"Ujjain", "Srinagar", "Seattle"}) {
            for (LocalDate d = LocalDate.of(2026, 1, 1); d.getYear() == 2026; d = d.plusDays(1)) {
                TithiInfo info = panchangP.forDate(d, city);
                if (info.getTithiNumber() == 15 && !info.isAdhika()) {
                    LocalDate next = d.plusDays(1);
                    TithiInfo nextInfo = panchangP.forDate(next, city);
                    if (nextInfo.getPaksha() == Paksha.KRISHNA) {
                        LunarMonth expected = info.getMonth().next();
                        assertEquals(expected, nextInfo.getMonth(),
                                city + " " + d + ": " + info.getMonth() + " -> " + nextInfo.getMonth());
                    }
                }
            }
        }
    }

    @Test @DisplayName("Festival dates match Drik Panchang")
    void festivalDates() {
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
            Festival fest = Festival.all().stream().filter(f -> f.id.equals(exp.id)).findFirst().orElseThrow();
            LocalDate got = panchangP.dateFor(fest, exp.year, "Ujjain");
            assertNotNull(got, exp.id + " " + exp.year);
            assertEquals(exp.date, got.toString(), exp.id + " " + exp.year);
        }
    }

    @Test @DisplayName("Tokyo sunrise day-carry")
    void tokyoDayCarry() {
        CityLocation loc = City.getLocation("Tokyo");
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
        CityLocation loc = City.getLocation("Ujjain");
        LocalDateTime sr = Astronomy.computeSunrise(LocalDate.of(2026, 3, 20), loc);
        int localMin = sr.getHour() * 60 + sr.getMinute() + (int)(loc.getUtcOffset() * 60);
        assertTrue(localMin >= 385 && localMin <= 400, "Equinox sunrise: " + localMin);
    }

    @Test @DisplayName("forDate → getDates round-trip")
    void roundTrip() {
        LocalDate dt = LocalDate.of(2026, 8, 16);
        TithiInfo info = panchangP.forDate(dt, "Ujjain");
        List<LocalDate> found = panchangP.getDates(info.getMonth(), info.getPaksha(),
                info.getTithiInPaksha(), 2026, "Ujjain");
        assertFalse(found.isEmpty(), "No date found");
        TithiInfo verify = panchangP.forDate(found.get(0), "Ujjain");
        assertEquals(info.getTithiNumber(), verify.getTithiNumber());
        assertEquals(info.getMonth(), verify.getMonth());
    }
}
