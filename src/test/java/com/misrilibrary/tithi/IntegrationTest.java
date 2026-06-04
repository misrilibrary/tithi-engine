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
        String[] cities = {"Ujjain", "Delhi", "Srinagar", "Seattle", "London", "Sydney", "Tokyo"};
        LocalDate[] dates = {
            LocalDate.of(2026, 3, 20), LocalDate.of(2026, 5, 20),
            LocalDate.of(2026, 7, 1), LocalDate.of(2026, 9, 4),
            LocalDate.of(2026, 11, 9), LocalDate.of(2026, 12, 25)};
        for (String city : cities) {
            for (LocalDate dt : dates) {
                TithiInfo p = panchangP.forDate(dt, city);
                TithiInfo a = panchangA.forDate(dt, city);
                assertEquals(p.getTithiNumber(), a.getTithiNumber(),
                        city + " " + dt + ": P=T" + p.getTithiNumber() + " A=T" + a.getTithiNumber());
                assertEquals(p.isAdhika(), a.isAdhika(),
                        city + " " + dt + ": adhika mismatch");
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

    @Test @DisplayName("Round-trip across 7 cities × 6 dates × 2 systems")
    void roundTripExpanded() {
        String[] cities = {"Ujjain", "Delhi", "Srinagar", "Seattle", "London", "Sydney", "Tokyo"};
        LocalDate[] dates = {LocalDate.of(2026, 1, 15), LocalDate.of(2026, 4, 20),
            LocalDate.of(2026, 8, 16), LocalDate.of(2026, 11, 8),
            LocalDate.of(2025, 10, 20), LocalDate.of(2025, 3, 15)};
        for (Panchang calc : new Panchang[]{panchangP, panchangA}) {
            for (String city : cities) {
                for (LocalDate dt : dates) {
                    TithiInfo info = calc.forDate(dt, city);
                    List<LocalDate> found = calc.getDates(info.getMonth(), info.getPaksha(),
                            info.getTithiInPaksha(), dt.getYear(), city);
                    boolean match = found.stream().anyMatch(fd -> {
                        TithiInfo fi = calc.forDate(fd, city);
                        return fi.getTithiNumber() == info.getTithiNumber()
                            && fi.getMonth() == info.getMonth();
                    });
                    assertTrue(match, city + " " + dt + " " + calc.getMonthSystem());
                }
            }
        }
    }

    @Test @DisplayName("Drik-verified month boundaries (Delhi, Purnimant)")
    void drikVerifiedBoundaries() {
        // Feb 3, 2015: Magha Purnima (last day of Magha)
        TithiInfo t1 = panchangP.forDate(LocalDate.of(2015, 2, 3), "Delhi");
        assertEquals(LunarMonth.MAGHA, t1.getMonth());
        assertEquals(15, t1.getTithiNumber());

        // Feb 4, 2015: Phalguna Krishna Pratipada (first day of Phalguna)
        TithiInfo t2 = panchangP.forDate(LocalDate.of(2015, 2, 4), "Delhi");
        assertEquals(LunarMonth.PHALGUNA, t2.getMonth());
        assertEquals(Paksha.KRISHNA, t2.getPaksha());

        // Jan 3, 2026: Pausha Purnima (last day of Pausha)
        TithiInfo t3 = panchangP.forDate(LocalDate.of(2026, 1, 3), "Delhi");
        assertEquals(LunarMonth.PAUSHA, t3.getMonth());
        assertEquals(15, t3.getTithiNumber());

        // Jan 4, 2026: Magha Krishna Pratipada (first day of Magha)
        TithiInfo t4 = panchangP.forDate(LocalDate.of(2026, 1, 4), "Delhi");
        assertEquals(LunarMonth.MAGHA, t4.getMonth());
        assertEquals(16, t4.getTithiNumber());
    }

    @Test @DisplayName("No month skipped 2000–2030 (Kolkata, Purnimant)")
    void noMonthSkipped31Years() {
        Panchang calc = new Panchang(MonthSystem.PURNIMANT);
        for (int year = 2000; year <= 2030; year++) {
            java.util.Set<LunarMonth> nij = new java.util.HashSet<>();
            for (LocalDate d = LocalDate.of(year, 1, 1); d.getYear() == year; d = d.plusDays(1)) {
                TithiInfo info = calc.forDate(d, "Kolkata");
                if (!info.isAdhika()) nij.add(info.getMonth());
            }
            assertEquals(12, nij.size(), "Year " + year + " missing months");
        }
    }

    @Test @DisplayName("Krishna before Shukla within same month (Purnimant)")
    void krishnaBeforeShukla() {
        // Magha 2026 starts Jan 4 (Krishna) and has Shukla later
        TithiInfo early = panchangP.forDate(LocalDate.of(2026, 1, 4), "Delhi");
        TithiInfo late = panchangP.forDate(LocalDate.of(2026, 1, 25), "Delhi");
        assertEquals(LunarMonth.MAGHA, early.getMonth());
        assertEquals(LunarMonth.MAGHA, late.getMonth());
        assertEquals(Paksha.KRISHNA, early.getPaksha());
        assertEquals(Paksha.SHUKLA, late.getPaksha());
    }

    @Test @DisplayName("Month transitions never exceed 14 in a year")
    void monthTransitionCount() {
        String[] cities = {"Ujjain", "Kolkata", "Seattle"};
        for (String city : cities) {
            LunarMonth last = null;
            int transitions = 0;
            for (LocalDate d = LocalDate.of(2026, 1, 1); d.getYear() == 2026; d = d.plusDays(1)) {
                TithiInfo t = panchangP.forDate(d, city);
                if (t.getMonth() != last) { transitions++; last = t.getMonth(); }
            }
            assertTrue(transitions >= 12 && transitions <= 14,
                    city + " transitions: " + transitions);
        }
    }
}
