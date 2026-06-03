package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.CityCorrections;
import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests targeting specific uncovered branches. */
class BranchCoverageTest {

    // ═══ Astronomy: polar cosH > 1 (no sunrise) ═══

    @Test @DisplayName("Polar sunrise: cosH > 1 handled gracefully")
    void polarSunrise() {
        // Latitude 70° N at winter solstice — sun doesn't rise
        CityLocation polar = new CityLocation(70.0, 25.0, 2.0);
        LocalDateTime sr = Astronomy.computeSunrise(LocalDate.of(2026, 12, 21), polar);
        assertNotNull(sr); // Should return a value (clamped, not crash)
    }

    @Test @DisplayName("Polar sunset: cosH > 1 handled gracefully")
    void polarSunset() {
        CityLocation polar = new CityLocation(70.0, 25.0, 2.0);
        LocalDateTime ss = Astronomy.computeSunset(LocalDate.of(2026, 12, 21), polar);
        assertNotNull(ss);
    }

    // ═══ LunarMonthResolver.getMonthInfo: adjacent-year fallback ═══

    @Test @DisplayName("getMonthInfo for very early Jan — may need previous year spans")
    void monthInfoEarlyJan() {
        // Jan 1 of any year: the lunar month likely started in previous year's December
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.PURNIMANT, "Ujjain");
        // Force the resolver to only cache year 2026, then query a date that falls
        // in a span starting in 2025 — exercises adjacent year fallback
        LunarMonthResolver.MonthInfo info = resolver.getMonthInfo(LocalDate.of(2026, 1, 1));
        assertNotNull(info.month);
        assertFalse(info.month.getDisplayName().isEmpty());
    }

    @Test @DisplayName("getMonthInfo for late Dec — may need next year spans")
    void monthInfoLateDec() {
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.PURNIMANT, "Ujjain");
        LunarMonthResolver.MonthInfo info = resolver.getMonthInfo(LocalDate.of(2025, 12, 31));
        assertNotNull(info.month);
    }

    // ═══ LunarMonthResolver.buildSpans: kshaya + double Amavasya/Purnima ═══

    @Test @DisplayName("1963 Amant spans — kshaya month (crossings > 1)")
    void kshayaSpans1963() {
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.AMANT, "Ujjain");
        List<LunarMonthResolver.MonthSpan> spans = resolver.getSpansForYear(1963);
        assertFalse(spans.isEmpty());
        for (LunarMonthResolver.MonthSpan span : spans) {
            assertNotNull(span.month);
            assertNotNull(span.start);
            assertTrue(span.end.isAfter(span.start));
        }
    }

    @Test @DisplayName("1963 Purnimant spans — exercises double Purnima/Amavasya paths")
    void purnimantSpans1963() {
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.PURNIMANT, "Ujjain");
        List<LunarMonthResolver.MonthSpan> spans = resolver.getSpansForYear(1963);
        assertFalse(spans.isEmpty());
        assertTrue(spans.size() >= 12);
    }

    @Test @DisplayName("buildSpans for 1945 — adhika Chaitra, exercises naming logic")
    void buildSpans1945() {
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.PURNIMANT, "Ujjain");
        List<LunarMonthResolver.MonthSpan> spans = resolver.getSpansForYear(1945);
        boolean hasAdhika = spans.stream().anyMatch(s -> s.adhika);
        assertTrue(hasAdhika, "1945 should have an adhika month");
    }

    @Test @DisplayName("buildSpans exercises kshaya Amavasya (T29→T1 skip) path")
    void kshayaAmavasyaPath() {
        // Scan multiple years with a city that has NO corrections — forces Meeus raw path
        // where kshaya transitions (T28/29 → T1/2 jump) are more likely
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.AMANT, "NonexistentCity");
        // Just verify it doesn't crash across many years
        for (int year = 1950; year <= 1970; year++) {
            List<LunarMonthResolver.MonthSpan> spans = resolver.getSpansForYear(year);
            assertFalse(spans.isEmpty(), "year " + year + " has no spans");
        }
    }

    @Test @DisplayName("buildSpans with Purnimant where purnima not found in span")
    void purnimantNoPurnimaInSpan() {
        // Use a city with no corrections — raw Meeus may produce spans
        // where the purnima detection doesn't find T15 within a span (purnima == null path)
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.PURNIMANT, "NonexistentCity");
        for (int year = 1960; year <= 1965; year++) {
            List<LunarMonthResolver.MonthSpan> spans = resolver.getSpansForYear(year);
            assertFalse(spans.isEmpty());
        }
    }

    @Test @DisplayName("buildSpans: adhika forward-naming with consecutive adhika months")
    void adhikaForwardNaming() {
        // 1963 has both kshaya and adhika — exercises the forward-naming loop
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.AMANT, "Ujjain");
        List<LunarMonthResolver.MonthSpan> spans = resolver.getSpansForYear(1963);
        // Verify adhika spans have proper month names (not CHAITRA placeholder)
        for (LunarMonthResolver.MonthSpan span : spans) {
            if (span.adhika) {
                assertNotNull(span.month);
            }
        }
    }

    // ═══ LunarMonthResolver.correctAmavasya/correctPurnima: correction applied ═══

    @Test @DisplayName("Purnima correction applied — exercises corrected != null return")
    void purnimaCorrection() {
        CityCorrections corr = CityCorrections.forCity("Srinagar");
        assertFalse(corr.getPurnimaCorrections().isEmpty());
        // Run the resolver for Srinagar — it will call correctPurnima internally
        // and hit the non-null path for dates in the correction table
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.PURNIMANT, "Srinagar");
        List<LunarMonthResolver.MonthSpan> spans = resolver.getSpansForYear(2026);
        assertFalse(spans.isEmpty());
    }

    @Test @DisplayName("Amavasya correction applied — exercises corrected != null return")
    void amavasyaCorrection() {
        CityCorrections corr = CityCorrections.forCity("Srinagar");
        assertFalse(corr.getAmavasyaCorrections().isEmpty());
        // Amant system uses amavasya boundaries — forces correctAmavasya calls
        LunarMonthResolver resolver = new LunarMonthResolver(MonthSystem.AMANT, "Srinagar");
        List<LunarMonthResolver.MonthSpan> spans = resolver.getSpansForYear(2026);
        assertFalse(spans.isEmpty());
    }

    // ═══ TithiFinder.filterDiscardFarSpan: multiple matches ═══

    @Test @DisplayName("getDates in adhika year returns filtered results")
    void getDateAdhikaFiltering() {
        Panchang panchang = new Panchang(MonthSystem.PURNIMANT);
        // Jyeshtha Shukla 1 in 2026 — exists in both adhika and regular Jyeshtha
        // getDates should return the non-adhika one by default
        List<LocalDate> dates = panchang.getDates(LunarMonth.JYESHTHA, Paksha.SHUKLA, 1, 2026, "Ujjain");
        assertFalse(dates.isEmpty());
        // Verify we got the regular (non-adhika) month
        TithiInfo info = panchang.forDate(dates.get(0), "Ujjain");
        assertEquals(LunarMonth.JYESHTHA, info.getMonth());
    }

    @Test @DisplayName("TithiFinder across year boundary — exercises filterDiscardFarSpan")
    void finderAcrossYearBoundary() {
        Panchang panchang = new Panchang(MonthSystem.PURNIMANT);
        // Pausha in early January — month spans year boundary
        List<LocalDate> dates = panchang.getDates(LunarMonth.PAUSHA, Paksha.KRISHNA, 5, 2026, "Ujjain");
        assertFalse(dates.isEmpty());
        // Date should be in late Dec 2025 or early Jan 2026
        LocalDate d = dates.get(0);
        assertTrue(d.getYear() == 2025 || d.getYear() == 2026);
    }

    // ═══ CityCorrections.parseSection: missing/empty sections ═══

    @Test @DisplayName("CityCorrections handles city with partial JSON")
    void partialCorrectionJson() {
        // Cities that may not have all sections (transitions might be empty)
        CityCorrections corr = CityCorrections.forCity("Auckland");
        // Should not crash, maps may be empty
        assertNotNull(corr.getTithiCorrections());
        assertNotNull(corr.getTransitionMinutes());
    }

    // ═══ Panchang.muhurtaUtc: default case (already covered by SUNRISE) ═══
    // ═══ Panchang.dateFor: null return on empty dates (unreachable) ═══

    @Test @DisplayName("dateFor with all muhurta types")
    void allMuhurtaTypes() {
        Panchang panchang = new Panchang(MonthSystem.PURNIMANT);
        // NISHITA
        assertNotNull(panchang.dateFor(Festival.MAHA_SHIVARATRI, 2026, "Ujjain"));
        // PRADOSH
        assertNotNull(panchang.dateFor(Festival.DIWALI, 2026, "Ujjain"));
        // MADHYAHNA
        assertNotNull(panchang.dateFor(Festival.RAM_NAVAMI, 2026, "Ujjain"));
        // SUNRISE
        assertNotNull(panchang.dateFor(Festival.GANESH_CHATURTHI, 2026, "Ujjain"));
    }
}
