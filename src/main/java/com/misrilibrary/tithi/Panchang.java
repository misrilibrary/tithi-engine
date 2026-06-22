package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.CityCorrections;
import com.misrilibrary.tithi.model.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Hindu lunar calendar (panchang) — the single public entry point.
 *
 * <p>The time-aware API is <b>UTC-instant based</b>: callers pass true UTC
 * {@link Instant}s and, where a civil day matters, the DST-aware {@link ZoneOffset}
 * in effect at that instant. The library does no timezone resolution itself.
 *
 * <pre>
 * Panchang panchang = new Panchang(MonthSystem.PURNIMANT);
 * TithiInfo info = panchang.tithiOnDate(LocalDate.of(2026, 2, 15), City.UJJAIN);
 * FestivalDate diwali = panchang.dateFor(Festival.DIWALI, 2026, City.SEATTLE);
 * </pre>
 */
public class Panchang {

    private static final LocalDate EPOCH = LocalDate.of(1900, 1, 1);

    private final MonthSystem monthSystem;
    private final Map<String, LunarMonthResolver> resolverCache = new HashMap<>();
    private final Map<String, TithiFinder> finderCache = new HashMap<>();
    private final Map<String, TithiFinder> purnimantFinderCache = new HashMap<>();

    public Panchang() { this(MonthSystem.PURNIMANT); }

    public Panchang(MonthSystem monthSystem) { this.monthSystem = monthSystem; }

    public MonthSystem getMonthSystem() { return monthSystem; }

    // ── Tithi lookup (time-aware) ─────────────────────────────────────────────

    /**
     * Sunrise tithi for the panchang day of {@code date} at {@code city}
     * (observance/display). Only the calendar fields select the day; no offset is
     * needed since sunrise is astronomical.
     */
    public TithiInfo tithiOnDate(LocalDate date, String city) {
        CityLocation loc = City.getLocation(city);
        CityCorrections corr = CityCorrections.forCity(city);
        int dayIndex = (int) EPOCH.until(date, ChronoUnit.DAYS);
        Integer corrected = corr.getCorrectedTithi(dayIndex);
        int tithiNum = corrected != null ? corrected
                : Astronomy.tithiAt(Astronomy.computeSunrise(date, loc));
        return buildInfo(tithiNum, date, city);
    }

    /**
     * Tithi active at the exact UTC {@code utcInstant} at {@code city}
     * (birth-time precision). {@code offset} is the DST-aware UTC offset in effect
     * at that instant; it is used only to derive the civil date for
     * correction-table selection. The astronomy uses {@code utcInstant} directly.
     */
    public TithiInfo tithiAtInstant(Instant utcInstant, String city, ZoneOffset offset) {
        CityLocation loc = City.getLocation(city);
        CityCorrections corr = CityCorrections.forCity(city);
        int dayIndex = civilDayIndex(utcInstant, offset);
        LocalDate civilDate = LocalDateTime.ofInstant(utcInstant, offset).toLocalDate();

        Integer transMinute = corr.getTransitionMinutes().get(dayIndex);
        int tithiNum;
        if (transMinute != null) {
            int mins = standardLocalMinutes(utcInstant, loc);
            Integer sr = corr.getCorrectedTithi(dayIndex);
            int sunriseTithi = sr != null ? sr
                    : Astronomy.tithiAt(Astronomy.computeSunrise(civilDate, loc));
            tithiNum = mins >= transMinute ? sunriseTithi : (sunriseTithi - 1 == 0 ? 30 : sunriseTithi - 1);
        } else {
            tithiNum = meeusTithiAt(utcInstant);
        }
        return buildInfo(tithiNum, civilDate, city);
    }

    /**
     * Every tithi segment within {@code [windowStartUtc, windowEndUtc)} at
     * {@code city}; N transitions → N+1 segments, each with its own resolved
     * {@link TithiInfo} and bounding instants. {@code offset} (the offset in effect
     * during the window) is used only for correction-table selection.
     */
    public List<TithiSegment> tithiSegments(Instant windowStartUtc, Instant windowEndUtc,
                                            String city, ZoneOffset offset) {
        CityLocation loc = City.getLocation(city);
        CityCorrections corr = CityCorrections.forCity(city);
        int dayIndex = civilDayIndex(windowStartUtc, offset);
        LocalDate civilDate = LocalDateTime.ofInstant(windowStartUtc, offset).toLocalDate();

        // 1. All transition instants inside the window (astronomy).
        List<Instant> transitions = findAllTransitions(windowStartUtc, windowEndUtc);

        // 2. Snap the table-known boundary to its corrected instant (Swiss-exact).
        Integer transMinute = corr.getTransitionMinutes().get(dayIndex);
        if (transMinute != null && !transitions.isEmpty()) {
            Instant correctedUtc = stdLocalMinutesToUtc(civilDate, transMinute, loc);
            if (correctedUtc.isAfter(windowStartUtc) && correctedUtc.isBefore(windowEndUtc)) {
                int bestI = 0;
                long bestDelta = Math.abs(Duration.between(transitions.get(0), correctedUtc).toMillis());
                for (int i = 1; i < transitions.size(); i++) {
                    long d = Math.abs(Duration.between(transitions.get(i), correctedUtc).toMillis());
                    if (d < bestDelta) { bestDelta = d; bestI = i; }
                }
                if (bestDelta <= 45 * 60_000L) transitions.set(bestI, correctedUtc);
            }
        }

        // 3. Anchor labels to the corrected sunrise tithi, step ±1 across boundaries.
        Integer anchorObj = corr.getCorrectedTithi(dayIndex);
        int anchorTithi = anchorObj != null ? anchorObj
                : Astronomy.tithiAt(Astronomy.computeSunrise(civilDate, loc));
        Instant sunriseUtc = Astronomy.computeSunrise(civilDate, loc).toInstant(ZoneOffset.UTC);

        List<Instant> bounds = new ArrayList<>();
        bounds.add(windowStartUtc);
        bounds.addAll(transitions);
        bounds.add(windowEndUtc);

        int sunriseSeg = 0;
        for (int i = 0; i < bounds.size() - 1; i++) {
            if (!sunriseUtc.isBefore(bounds.get(i)) && sunriseUtc.isBefore(bounds.get(i + 1))) {
                sunriseSeg = i;
                break;
            }
        }

        List<TithiSegment> segments = new ArrayList<>();
        int last = bounds.size() - 2;
        for (int i = 0; i <= last; i++) {
            int tnum = wrap30(anchorTithi + (i - sunriseSeg));
            Instant lo = bounds.get(i), hi = bounds.get(i + 1);
            Instant mid = lo.plusMillis(Duration.between(lo, hi).toMillis() / 2);
            LocalDate monthDate = LocalDateTime.ofInstant(mid, ZoneOffset.UTC).toLocalDate();
            segments.add(new TithiSegment(lo, hi, buildInfo(tnum, monthDate, city), i > 0, i < last));
        }
        return segments;
    }

    // ── Tithi → date ──────────────────────────────────────────────────────────

    /** Tithi spec → first matching Gregorian date in the year, or {@code null}. */
    public LocalDate getDate(LunarMonth month, Paksha paksha, int tithiInPaksha, int year, String city) {
        List<LocalDate> dates = getDates(month, paksha, tithiInPaksha, year, city);
        return dates.isEmpty() ? null : dates.get(0);
    }

    /** Tithi spec → all matching Gregorian dates in the year (adhika-aware). */
    public List<LocalDate> getDates(LunarMonth month, Paksha paksha, int tithiInPaksha, int year, String city) {
        return getFinder(city).findInYear(month, paksha, tithiInPaksha, year, false);
    }

    /** Next occurrence of a tithi from today (or {@code from}); {@code null} if none within ~400 days. */
    public LocalDate findNext(LunarMonth month, Paksha paksha, int tithiInPaksha, String city, LocalDate from) {
        LocalDate start = from != null ? from : LocalDate.now();
        int target = paksha == Paksha.SHUKLA ? tithiInPaksha : tithiInPaksha + 15;
        CityLocation loc = City.getLocation(city);
        LunarMonthResolver resolver = getResolver(city);
        for (int i = 0; i < 400; i++) {
            LocalDate dt = start.plusDays(i);
            int t = Astronomy.tithiAt(Astronomy.computeSunrise(dt, loc));
            if (t == target && resolver.getMonthInfo(dt).month == month) return dt;
        }
        return null;
    }

    // ── Festivals ───────────────────────────────────────────────────────────

    /** Festival → date with muhurta rules applied; {@code null} if it doesn't occur. */
    public FestivalDate dateFor(Festival fest, int year, String city) {
        return FestivalFinder.findFestivalDate(fest, year, city, getPurnimantFinder(city));
    }

    /** Recurring festival (e.g. monthly Ekadashi/Purnima) → all occurrences in the year. */
    public List<FestivalDate> recurringDates(Festival fest, int year, String city) {
        return FestivalFinder.findRecurringDates(fest, year, city);
    }

    /**
     * Bind this Panchang to a {@link Location} (a registered city or raw
     * coordinates), returning a view whose methods drop the {@code city} argument.
     *
     * <pre>
     * panchang.at(Location.at(47.61, -122.33, Duration.ofHours(-8))).tithiOnDate(date);
     * </pre>
     */
    public PanchangAt at(Location location) {
        return new PanchangAt(this, location);
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private TithiInfo buildInfo(int tithiNum, LocalDate monthDate, String city) {
        Paksha paksha = TithiUtils.getPaksha(tithiNum);
        String name = TithiUtils.getTithiName(tithiNum);
        int inPaksha = TithiUtils.tithiInPaksha(tithiNum);
        LunarMonthResolver.MonthInfo mi = getResolver(city).getMonthInfo(monthDate);
        String pakshaStr = paksha == Paksha.SHUKLA ? "Shukla" : "Krishna";
        String adhikaPrefix = mi.adhika ? "Adhika " : "";
        String display = adhikaPrefix + mi.month.getDisplayName() + " " + pakshaStr + " " + name;
        return new TithiInfo(tithiNum, name, paksha, inPaksha, mi.month, mi.adhika, display);
    }

    private static int civilDayIndex(Instant utcInstant, ZoneOffset offset) {
        LocalDate civilDate = LocalDateTime.ofInstant(utcInstant, offset).toLocalDate();
        return (int) EPOCH.until(civilDate, ChronoUnit.DAYS);
    }

    /** {@code utcInstant} as minute-of-day in the city's fixed standard local time. */
    private static int standardLocalMinutes(Instant utcInstant, CityLocation loc) {
        long stdMin = Math.round(loc.getUtcOffset() * 60);
        LocalDateTime local = LocalDateTime.ofInstant(utcInstant, ZoneOffset.UTC).plusMinutes(stdMin);
        return (local.getHour() * 60 + local.getMinute()) % 1440;
    }

    /** Inverse of {@link #standardLocalMinutes}: standard-local minute-of-day → UTC instant. */
    private static Instant stdLocalMinutesToUtc(LocalDate civilDate, int minutes, CityLocation loc) {
        long stdMin = Math.round(loc.getUtcOffset() * 60);
        return civilDate.atStartOfDay().toInstant(ZoneOffset.UTC)
                .minusSeconds(stdMin * 60).plusSeconds((long) minutes * 60);
    }

    private static int meeusTithiAt(Instant utcInstant) {
        return Astronomy.tithiAt(LocalDateTime.ofInstant(utcInstant, ZoneOffset.UTC));
    }

    private static int wrap30(int n) { return ((n - 1) % 30 + 30) % 30 + 1; }

    /** All tithi transition instants in {@code [start, end)} (1-hour scan + 30s bisection). */
    private static List<Instant> findAllTransitions(Instant start, Instant end) {
        List<Instant> out = new ArrayList<>();
        Duration step = Duration.ofHours(1);
        Instant segLo = start;
        int loTithi = meeusTithiAt(start);
        Instant probe = start.plus(step);
        while (true) {
            Instant cur = probe.isAfter(end) ? end : probe;
            int t = meeusTithiAt(cur);
            if (t != loTithi) {
                Instant b = bisect(segLo, cur, loTithi);
                if (b.isAfter(start) && b.isBefore(end)) out.add(b);
                loTithi = t;
            }
            segLo = cur;
            if (!cur.isBefore(end)) break;
            probe = probe.plus(step);
        }
        return out;
    }

    private static Instant bisect(Instant lo, Instant hi, int startTithi) {
        while (Duration.between(lo, hi).getSeconds() > 30) {
            Instant mid = lo.plusSeconds(Duration.between(lo, hi).getSeconds() / 2);
            if (meeusTithiAt(mid) == startTithi) lo = mid; else hi = mid;
        }
        return hi;
    }

    private LunarMonthResolver getResolver(String city) {
        return resolverCache.computeIfAbsent(city, c -> new LunarMonthResolver(monthSystem, c));
    }

    private TithiFinder getFinder(String city) {
        return finderCache.computeIfAbsent(city, c -> new TithiFinder(monthSystem, c));
    }

    private TithiFinder getPurnimantFinder(String city) {
        return purnimantFinderCache.computeIfAbsent(city, c -> new TithiFinder(MonthSystem.PURNIMANT, c));
    }
}
