package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.Cities;
import com.misrilibrary.tithi.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Find when a tithi falls in a given year. Implements the pipeline:
 * findRaw → filterDiscardFarSpan → filterAdhikaMasa
 */
public class TithiFinder {

    private final LunarMonthResolver resolver;
    private final String city;

    public TithiFinder(MonthSystem system, String city) {
        this.resolver = new LunarMonthResolver(system, city);
        this.city = city;
    }

    /** Find all dates a tithi occurs in a year. Usually returns 1 date. */
    public List<LocalDate> findInYear(LunarMonth month, Paksha paksha, int tithiInPaksha,
                                       int year, boolean isAdhika) {
        int targetTithi = paksha == Paksha.SHUKLA ? tithiInPaksha : tithiInPaksha + 15;
        CityLocation loc = Cities.getLocation(city);

        List<LunarMonthResolver.MonthSpan> spans = resolver.getSpansForYear(year);
        List<LunarMonthResolver.MonthSpan> prevSpans = resolver.getSpansForYear(year - 1);

        // Find matching spans
        List<LunarMonthResolver.MonthSpan> matchingSpans = new ArrayList<>();
        for (LunarMonthResolver.MonthSpan s : spans) { if (s.month == month) matchingSpans.add(s); }
        for (LunarMonthResolver.MonthSpan s : prevSpans) { if (s.month == month) matchingSpans.add(s); }

        // Deduplicate by start date
        Set<LocalDate> seen = new HashSet<>();
        matchingSpans = matchingSpans.stream().filter(s -> seen.add(s.start)).collect(Collectors.toList());

        // Find tithi in each span
        List<TithiMatch> results = new ArrayList<>();
        for (LunarMonthResolver.MonthSpan span : matchingSpans) {
            LocalDate lastSeen = null;
            int prevT = -1;
            for (LocalDate dt = span.start; dt.isBefore(span.end); dt = dt.plusDays(1)) {
                LocalDateTime sr = Astronomy.computeSunrise(dt, loc);
                int currentTithi = Astronomy.tithiAt(sr);
                if (currentTithi == targetTithi) {
                    lastSeen = dt;
                } else if (lastSeen != null) {
                    break;
                } else if (prevT > 0 && prevT < targetTithi && currentTithi > targetTithi) {
                    lastSeen = dt.minusDays(1); // kshaya
                    break;
                }
                prevT = currentTithi;
            }
            if (lastSeen != null) results.add(new TithiMatch(lastSeen, span));
        }

        // Apply filters
        results = filterDiscardFarSpan(results, year);
        results = filterAdhikaMasa(results, isAdhika);

        // Year boundary filter
        LocalDate earliest = LocalDate.of(year - 1, 12, 1);
        LocalDate latest = LocalDate.of(year + 1, 1, 1);
        return results.stream()
                .map(m -> m.date)
                .filter(d -> !d.isBefore(earliest) && d.isBefore(latest))
                .collect(Collectors.toList());
    }

    private List<TithiMatch> filterDiscardFarSpan(List<TithiMatch> matches, int year) {
        if (matches.size() <= 1) return matches;
        LocalDate earliest = LocalDate.of(year - 1, 12, 1);
        LocalDate latest = LocalDate.of(year + 1, 1, 1);
        List<TithiMatch> inYear = matches.stream()
                .filter(m -> !m.date.isBefore(earliest) && m.date.isBefore(latest))
                .collect(Collectors.toList());
        if (!inYear.isEmpty() && inYear.size() < matches.size()) return inYear;
        return matches;
    }

    private List<TithiMatch> filterAdhikaMasa(List<TithiMatch> matches, boolean isAdhika) {
        if (matches.size() <= 1) return matches;
        List<TithiMatch> preferred = matches.stream()
                .filter(m -> m.span.adhika == isAdhika)
                .collect(Collectors.toList());
        return preferred.isEmpty() ? matches : preferred;
    }

    private static class TithiMatch {
        final LocalDate date;
        final LunarMonthResolver.MonthSpan span;
        TithiMatch(LocalDate date, LunarMonthResolver.MonthSpan span) {
            this.date = date; this.span = span;
        }
    }
}
