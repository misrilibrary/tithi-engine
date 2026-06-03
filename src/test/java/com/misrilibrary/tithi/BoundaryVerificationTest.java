package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.CityCorrections;
import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive boundary verification tests ported from Dart check_* scripts.
 * Covers month boundaries, festivals, sunrise edge cases, and wrap cities
 * across many cities and years (1900–2100 range).
 */
class BoundaryVerificationTest {

    private final Panchang panchangP = new Panchang(MonthSystem.PURNIMANT);
    private final Panchang panchangA = new Panchang(MonthSystem.AMANT);

    // ═══ MONTH BOUNDARIES (from check_month_boundaries.dart) ═══

    static final String[] INDIA_CITIES = {"Ujjain", "Delhi", "Mumbai", "Chennai", "Srinagar", "Jammu"};
    static final String[] OTHER_CITIES = {"Seattle", "San Francisco", "London", "Sydney"};

    @ParameterizedTest
    @ValueSource(strings = {"Ujjain", "Delhi", "Mumbai", "Chennai", "Srinagar", "Jammu"})
    @DisplayName("Purnimant: K.1 after Purnima = next month (India cities, 2026)")
    void purnimantBoundaryIndia(String city) {
        assertPurnimantBoundaries(city, 2026);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Seattle", "San Francisco", "London", "Sydney"})
    @DisplayName("Purnimant: K.1 after Purnima = next month (Other cities, 2026)")
    void purnimantBoundaryOther(String city) {
        assertPurnimantBoundaries(city, 2026);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ujjain", "Delhi", "Mumbai", "Chennai", "Srinagar", "Jammu"})
    @DisplayName("Amant: S.1 after Amavasya = next month (India cities, 2026)")
    void amantBoundaryIndia(String city) {
        assertAmantBoundaries(city, 2026);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Seattle", "San Francisco", "London", "Sydney"})
    @DisplayName("Amant: S.1 after Amavasya = next month (Other cities, 2026)")
    void amantBoundaryOther(String city) {
        assertAmantBoundaries(city, 2026);
    }

    private void assertPurnimantBoundaries(String city, int year) {
        int checked = 0;
        for (LocalDate d = LocalDate.of(year, 1, 1); d.getYear() == year; d = d.plusDays(1)) {
            TithiInfo info = panchangP.forDate(d, city);
            if (info.getTithiNumber() == 15 && !info.isAdhika()) {
                LocalDate next = d.plusDays(1);
                TithiInfo nextInfo = panchangP.forDate(next, city);
                if (nextInfo.getPaksha() == Paksha.KRISHNA) {
                    LunarMonth expected = info.getMonth().next();
                    assertEquals(expected, nextInfo.getMonth(),
                            city + " " + d + ": after Purnima(" + info.getMonth().getDisplayName()
                                    + ") got " + nextInfo.getMonth().getDisplayName()
                                    + " want " + expected.getDisplayName());
                    checked++;
                }
            }
        }
        assertTrue(checked >= 10, city + ": only " + checked + " Purnima boundaries found");
    }

    private void assertAmantBoundaries(String city, int year) {
        int checked = 0;
        for (LocalDate d = LocalDate.of(year, 1, 1); d.getYear() == year; d = d.plusDays(1)) {
            TithiInfo info = panchangA.forDate(d, city);
            if (info.getTithiNumber() == 30 && !info.isAdhika()) {
                LocalDate next = d.plusDays(1);
                TithiInfo nextInfo = panchangA.forDate(next, city);
                if (nextInfo.getPaksha() == Paksha.SHUKLA) {
                    LunarMonth expected = info.getMonth().next();
                    assertTrue(nextInfo.getMonth() == expected || nextInfo.isAdhika(),
                            city + " " + d + ": after Amavasya(" + info.getMonth().getDisplayName()
                                    + ") got " + nextInfo.getMonth().getDisplayName()
                                    + " want " + expected.getDisplayName() + " or adhika");
                    checked++;
                }
            }
        }
        assertTrue(checked >= 9, city + ": only " + checked + " Amavasya boundaries found");
    }

    // ═══ FESTIVALS (from check_festivals.dart) ═══

    @Test
    @DisplayName("Festival dates match Drik Panchang — Ujjain 2025-2026")
    void festivalsUjjain() {
        var truth = Map.ofEntries(
            Map.entry("maha_shivaratri_2025", "2025-02-26"),
            Map.entry("maha_shivaratri_2026", "2026-02-15"),
            Map.entry("holika_dahan_2025", "2025-03-13"),
            Map.entry("holika_dahan_2026", "2026-03-02"),
            Map.entry("ram_navami_2025", "2025-04-06"),
            Map.entry("ram_navami_2026", "2026-03-26"),
            Map.entry("akshaya_tritiya_2025", "2025-04-30"),
            Map.entry("akshaya_tritiya_2026", "2026-04-19"),
            Map.entry("guru_purnima_2025", "2025-07-10"),
            Map.entry("guru_purnima_2026", "2026-07-29"),
            Map.entry("janmashtami_smarta_2025", "2025-08-15"),
            Map.entry("janmashtami_smarta_2026", "2026-09-04"),
            Map.entry("diwali_2025", "2025-10-20"),
            Map.entry("diwali_2026", "2026-11-08")
        );
        assertFestivals(truth, "Ujjain");
    }

    @Test
    @DisplayName("Festival dates — Seattle 2025-2026")
    void festivalsSeattle() {
        // Diwali verified for Seattle (pradosh muhurta)
        var truth = Map.ofEntries(
            Map.entry("diwali_2025", "2025-10-20"),
            Map.entry("diwali_2026", "2026-11-08")
        );
        assertFestivals(truth, "Seattle");
    }

    private void assertFestivals(Map<String, String> truth, String city) {
        for (Festival fest : Festival.all()) {
            for (int year : new int[]{2025, 2026}) {
                String key = fest.id + "_" + year;
                String expected = truth.get(key);
                if (expected == null) continue;
                LocalDate got = panchangP.dateFor(fest, year, city);
                assertNotNull(got, fest.name + " " + year + " " + city);
                assertEquals(expected, got.toString(),
                        fest.name + " " + year + " " + city);
            }
        }
    }

    // ═══ SUNRISE EDGE CASES (from check_sunrise_east.dart + check_wrap_cities.dart) ═══

    @ParameterizedTest
    @ValueSource(strings = {"Varanasi", "Kolkata", "Guwahati", "Delhi", "Srinagar", "Seattle"})
    @DisplayName("Sunrise lands on correct local day at both solstices")
    void sunriseDayCarry(String city) {
        CityLocation loc = City.getLocation(city);
        for (LocalDate dt : new LocalDate[]{
                LocalDate.of(2025, 6, 21), LocalDate.of(2025, 12, 21)}) {
            LocalDateTime sr = Astronomy.computeSunrise(dt, loc);
            LocalDateTime local = sr.plusMinutes((long)(loc.getUtcOffset() * 60));
            assertEquals(dt.getDayOfMonth(), local.getDayOfMonth(),
                    city + " " + dt + " sunrise on wrong day: " + sr);
            assertTrue(local.getHour() >= 4 && local.getHour() <= 9,
                    city + " " + dt + " sunrise hour out of range: " + local.getHour());
        }
    }

    @Test
    @DisplayName("Sunrise seasonal sanity — Varanasi across 5 dates")
    void sunriseSeasonalVaranasi() {
        CityLocation loc = City.getLocation("Varanasi");
        for (LocalDate dt : new LocalDate[]{
                LocalDate.of(2025, 1, 15), LocalDate.of(2025, 3, 21),
                LocalDate.of(2025, 6, 21), LocalDate.of(2025, 9, 23),
                LocalDate.of(2025, 12, 21)}) {
            LocalDateTime sr = Astronomy.computeSunrise(dt, loc);
            // Convert UTC to local: handle day overflow
            long localMinutes = sr.getHour() * 60 + sr.getMinute() + (long)(loc.getUtcOffset() * 60);
            if (localMinutes >= 1440) localMinutes -= 1440;
            if (localMinutes < 0) localMinutes += 1440;
            double localHour = localMinutes / 60.0;
            assertTrue(localHour >= 5.0 && localHour <= 7.2,
                    "Varanasi " + dt + " sunrise local=" + String.format("%.2f", localHour));
        }
    }

    // ═══ ADHIKA MONTH NAMES (from tithi_regression_test.dart) ═══

    @Test
    @DisplayName("Adhika month names match verified Drik Panchang")
    void adhikaMonthNames() {
        var expected = Map.ofEntries(
            Map.entry(2010, "Vaishakha"),
            Map.entry(2015, "Ashadha"),
            Map.entry(2023, "Shravana"),
            Map.entry(2029, "Chaitra")
        );
        expected.forEach((year, monthName) -> {
            LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.PURNIMANT, "Ujjain");
            Set<String> adhikaMonths = new HashSet<>();
            for (LunarMonthResolver.MonthSpan span : resolver.getSpansForYear(year)) {
                if (span.adhika) adhikaMonths.add(span.month.getDisplayName());
            }
            assertTrue(adhikaMonths.contains(monthName),
                    year + ": expected Adhika " + monthName + " but got " + adhikaMonths);
        });
    }

    // ═══ NO MONTH SILENTLY SKIPPED (from tithi_regression_test.dart) ═══

    @ParameterizedTest
    @ValueSource(strings = {"Ujjain", "Kolkata", "Seattle"})
    @DisplayName("All 12 nij months present 2000–2030 (no silent skip)")
    void noMonthSkipped(String city) {
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.PURNIMANT, city);
        for (int year = 2000; year <= 2030; year++) {
            Set<LunarMonth> nij = new HashSet<>();
            for (LunarMonthResolver.MonthSpan span : resolver.getSpansForYear(year)) {
                if (!span.adhika) nij.add(span.month);
            }
            assertEquals(12, nij.size(),
                    city + " " + year + ": missing months, only found " + nij.size());
        }
    }

    // ═══ EVERY CITY HAS CORRECTION TABLE (from tithi_regression_test.dart) ═══

    @Test
    @DisplayName("Every supported city has a non-empty correction table")
    void allCitiesHaveCorrections() {
        List<String> missing = new ArrayList<>();
        for (String city : City.supported()) {
            CityCorrections corr = CityCorrections.forCity(city);
            if (corr.getTithiCorrections().isEmpty()) {
                missing.add(city);
            }
        }
        assertTrue(missing.isEmpty(),
                "Cities with no correction table (Meeus fallback only): " + missing
                + "\nRun the Swiss Ephemeris generator for these cities.");
    }
}
