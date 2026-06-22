package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for the 2.0.0 parity surface: city display names, festivals, time-aware API. */
class ParityApiTest {

    private final Panchang panchang = new Panchang(MonthSystem.PURNIMANT);

    // ─── B: city coverage + display-name disambiguation ───

    @Test @DisplayName("All 230 cities registered with coordinates")
    void allCitiesRegistered() {
        assertEquals(230, City.supported().size());
        assertNotNull(City.getLocation("São Paulo"));
        assertNotNull(City.getLocation("Medellín"));
        assertEquals("WA", City.getLocation("Seattle").getRegion());
    }

    @Test @DisplayName("qualifiedName appends region for all; null-region stays bare")
    void qualifiedName() {
        assertEquals("Seattle, WA", City.qualifiedName("Seattle"));
        assertEquals("Tokyo, Japan", City.qualifiedName("Tokyo"));
        assertEquals("Singapore", City.qualifiedName("Singapore"));       // self-qualifying
        assertEquals("Washington DC", City.qualifiedName("Washington DC")); // null region
    }

    @Test @DisplayName("displayName qualifies only commonly-confused names")
    void displayName() {
        assertEquals("Redmond, WA", City.displayName("Redmond"));   // ambiguous
        assertEquals("Naples, Italy", City.displayName("Naples"));  // ambiguous
        assertEquals("Seattle", City.displayName("Seattle"));       // has region but not ambiguous
        assertEquals("Delhi", City.displayName("Delhi"));           // not ambiguous
    }

    // ─── C: festivals ───

    @Test @DisplayName("Festival set includes Kashmiri + recurring masik festivals")
    void festivalSet() {
        var ids = Festival.all().stream().map(f -> f.id).toList();
        assertTrue(ids.contains("maha_shivaratri_kashmiri"));
        assertTrue(ids.contains("janmashtami_kashmiri"));
        assertTrue(ids.contains("masik_purnima"));
        assertEquals(35, Festival.all().size());
        assertEquals(FestivalTradition.KASHMIRI, Festival.MAHA_SHIVARATRI_KASHMIRI.tradition);
        assertTrue(Festival.MASIK_PURNIMA.recurring);
    }

    @Test @DisplayName("dateFor returns a FestivalDate with tithi span; Maha Shivaratri 2026 = Feb 15")
    void dateForReturnsFestivalDate() {
        FestivalDate fd = panchang.dateFor(Festival.MAHA_SHIVARATRI, 2026, "Ujjain");
        assertNotNull(fd);
        assertEquals(LocalDate.of(2026, 2, 15), fd.getDate());
        assertNotNull(fd.getTithiStart());
        assertNotNull(fd.getTithiEnd());
        assertTrue(fd.getTithiStart().isBefore(fd.getTithiEnd()));
        assertNotNull(fd.getMuhurtaStart()); // nishita rule → window present
    }

    @Test @DisplayName("recurringDates: ~12-13 Purnimas in a year, all tithi 15")
    void recurringDates() {
        List<FestivalDate> purnimas = panchang.recurringDates(Festival.MASIK_PURNIMA, 2026, "Ujjain");
        assertTrue(purnimas.size() >= 12 && purnimas.size() <= 13,
                "expected 12-13 purnimas, got " + purnimas.size());
        for (FestivalDate fd : purnimas) {
            assertEquals(15, panchang.tithiOnDate(fd.getDate(), "Ujjain").getTithiNumber());
        }
    }

    // ─── D: time-aware API ───

    @Test @DisplayName("tithiOnDate replaces forDate; Ujjain 2026-02-15 = Phalguna Krishna Trayodashi")
    void tithiOnDate() {
        TithiInfo info = panchang.tithiOnDate(LocalDate.of(2026, 2, 15), "Ujjain");
        assertEquals("Phalguna Krishna Trayodashi", info.getDisplayName());
    }

    @Test @DisplayName("tithiSegments partition the window contiguously; sunrise segment matches tithiOnDate")
    void tithiSegments() {
        // Ujjain civil day 2026-02-15 (IST +05:30) framed as UTC window.
        ZoneOffset ist = ZoneOffset.ofHoursMinutes(5, 30);
        Instant start = LocalDate.of(2026, 2, 15).atStartOfDay().toInstant(ist);
        Instant end = LocalDate.of(2026, 2, 16).atStartOfDay().toInstant(ist);
        List<TithiSegment> segs = panchang.tithiSegments(start, end, "Ujjain", ist);

        assertFalse(segs.isEmpty());
        assertEquals(start, segs.get(0).getStartUtc());
        assertEquals(end, segs.get(segs.size() - 1).getEndUtc());
        for (int i = 0; i < segs.size(); i++) {
            if (i > 0) assertEquals(segs.get(i - 1).getEndUtc(), segs.get(i).getStartUtc());
            assertEquals(i > 0, segs.get(i).isStartTransition());
            assertEquals(i < segs.size() - 1, segs.get(i).isEndTransition());
        }
        // The segment containing local sunrise must equal tithiOnDate's tithi.
        int sunriseTithi = panchang.tithiOnDate(LocalDate.of(2026, 2, 15), "Ujjain").getTithiNumber();
        assertTrue(segs.stream().anyMatch(s -> s.getTithi().getTithiNumber() == sunriseTithi));
    }

    @Test @DisplayName("tithiAtInstant agrees with the tithiSegments segment containing the instant")
    void tithiAtInstantConsistency() {
        ZoneOffset ist = ZoneOffset.ofHoursMinutes(5, 30);
        Instant start = LocalDate.of(2026, 2, 15).atStartOfDay().toInstant(ist);
        Instant end = LocalDate.of(2026, 2, 16).atStartOfDay().toInstant(ist);
        Instant noon = LocalDateTime.of(2026, 2, 15, 12, 0).toInstant(ist); // local noon

        int atInstant = panchang.tithiAtInstant(noon, "Ujjain", ist).getTithiNumber();
        List<TithiSegment> segs = panchang.tithiSegments(start, end, "Ujjain", ist);
        int segTithi = segs.stream()
                .filter(s -> !noon.isBefore(s.getStartUtc()) && noon.isBefore(s.getEndUtc()))
                .map(s -> s.getTithi().getTithiNumber())
                .findFirst().orElse(-1);
        assertEquals(segTithi, atInstant);
    }

    @Test @DisplayName("findNext returns the next occurrence of a tithi")
    void findNext() {
        // Maha Shivaratri tithi = Phalguna Krishna 14; next from 2026-01-01 ≈ mid-Feb.
        LocalDate next = panchang.findNext(LunarMonth.PHALGUNA, Paksha.KRISHNA, 14, "Ujjain",
                LocalDate.of(2026, 1, 1));
        assertNotNull(next);
        assertTrue(next.getYear() == 2026 && next.getMonthValue() <= 3);
    }

    // ─── TithiInfo.fromStored ───

    @Test @DisplayName("fromStored renders a saved tithi, with optional Purnimant↔Amant conversion")
    void fromStored() {
        TithiInfo p = TithiInfo.fromStored(30, LunarMonth.KARTIKA, MonthSystem.PURNIMANT);
        assertEquals("Kartika Krishna Amavasya", p.getDisplayName());
        assertEquals(Paksha.KRISHNA, p.getPaksha());
        assertEquals(15, p.getTithiInPaksha());

        // Krishna month converts Purnimant→Amant for display (Kartika → Ashvina).
        TithiInfo a = TithiInfo.fromStored(30, LunarMonth.KARTIKA, MonthSystem.PURNIMANT, false, MonthSystem.AMANT);
        assertEquals("Ashvina Krishna Amavasya", a.getDisplayName());
    }
}
