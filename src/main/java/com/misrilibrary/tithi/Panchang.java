package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.CityCorrections;
import com.misrilibrary.tithi.data.GlobalTransitionCorrections;
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
    private final SunriseConvention convention;
    private final Map<String, LunarMonthResolver> resolverCache = new HashMap<>();
    private final Map<String, TithiFinder> finderCache = new HashMap<>();
    private final Map<String, TithiFinder> purnimantFinderCache = new HashMap<>();

    public Panchang() { this(MonthSystem.PURNIMANT); }

    public Panchang(MonthSystem monthSystem) { this(monthSystem, SunriseConvention.UPPER_LIMB); }

    /**
     * @param monthSystem Purnimant or Amant month assignment
     * @param convention  which point of the Sun's disk marks sunrise/sunset;
     *                    {@link SunriseConvention#UPPER_LIMB} reproduces the
     *                    engine's original behavior exactly
     */
    public Panchang(MonthSystem monthSystem, SunriseConvention convention) {
        this.monthSystem = monthSystem;
        this.convention = convention;
    }

    public MonthSystem getMonthSystem() { return monthSystem; }

    /** The sunrise/sunset convention this Panchang uses. */
    public SunriseConvention getConvention() { return convention; }

    // ── Tithi lookup (time-aware) ─────────────────────────────────────────────

    /**
     * Sunrise tithi for the panchang day of {@code date} at {@code city}
     * (observance/display). Only the calendar fields select the day; no offset is
     * needed since sunrise is astronomical.
     */
    public TithiInfo tithiOnDate(LocalDate date, City city) {
        return tithiOnDateImpl(date, city.name());
    }

    TithiInfo tithiOnDateImpl(LocalDate date, String city) {
        CityLocation loc = City.getLocation(city);
        CityCorrections corr = CityCorrections.forCity(city, convention);
        int dayIndex = (int) EPOCH.until(date, ChronoUnit.DAYS);
        Integer corrected = corr.getCorrectedTithi(dayIndex);
        int tithiNum = corrected != null ? corrected
                : Astronomy.tithiAt(Astronomy.computeSunrise(date, loc, convention));
        return buildInfo(tithiNum, date, city);
    }

    /**
     * Tithi active at the exact UTC {@code utcInstant} at {@code city}
     * (birth-time precision). {@code offset} is the DST-aware UTC offset in effect
     * at that instant; it is used only to derive the civil date for
     * correction-table selection. The astronomy uses {@code utcInstant} directly.
     */
    public TithiInfo tithiAtInstant(Instant utcInstant, City city, ZoneOffset offset) {
        return tithiAtInstantImpl(utcInstant, city.name(), offset);
    }

    TithiInfo tithiAtInstantImpl(Instant utcInstant, String city, ZoneOffset offset) {
        LocalDate civilDate = LocalDateTime.ofInstant(utcInstant, offset).toLocalDate();
        // The tithi at an instant is the Swiss-corrected elongation tithi of the
        // segment (bounded by corrected transitions) that contains it. Labeling by
        // the segment's own elongation — not a sunrise anchor — keeps it correct
        // even when the engine's Meeus sunrise lands on the opposite side of a
        // near-sunrise transition from the true sunrise (the straddle case).
        Instant lo = utcInstant.minus(Duration.ofHours(30));
        Instant hi = utcInstant.plus(Duration.ofHours(30));
        Instant a = lo, b = hi;
        for (Instant tr : findAllTransitions(lo, hi)) {
            if (!tr.isAfter(utcInstant)) {
                a = tr;
            } else {
                b = tr;
                break;
            }
        }
        Instant mid = a.plusMillis(Duration.between(a, b).toMillis() / 2);
        return buildInfo(meeusTithiAt(mid), civilDate, city);
    }

    /**
     * Every tithi segment within {@code [windowStartUtc, windowEndUtc)} at
     * {@code city}; N transitions → N+1 segments, each with its own resolved
     * {@link TithiInfo} and bounding instants. {@code offset} (the offset in effect
     * during the window) is used only for correction-table selection.
     */
    public List<TithiSegment> tithiSegments(Instant windowStartUtc, Instant windowEndUtc,
                                            City city, ZoneOffset offset) {
        return tithiSegmentsImpl(windowStartUtc, windowEndUtc, city.name(), offset);
    }

    List<TithiSegment> tithiSegmentsImpl(Instant windowStartUtc, Instant windowEndUtc,
                                         String city, ZoneOffset offset) {
        // 1. All transition instants inside the window — already Swiss-corrected at
        //    the source via the global (city-independent) transition correction.
        List<Instant> transitions = findAllTransitions(windowStartUtc, windowEndUtc);

        // 2. Label each segment by its OWN Swiss-corrected elongation tithi (the
        //    tithi at the segment midpoint), independent of any sunrise anchor — so
        //    labels stay .se1-correct even on sunrise-straddle days.
        List<Instant> bounds = new ArrayList<>();
        bounds.add(windowStartUtc);
        bounds.addAll(transitions);
        bounds.add(windowEndUtc);

        List<TithiSegment> segments = new ArrayList<>();
        int last = bounds.size() - 2;
        for (int i = 0; i <= last; i++) {
            Instant lo = bounds.get(i), hi = bounds.get(i + 1);
            Instant mid = lo.plusMillis(Duration.between(lo, hi).toMillis() / 2);
            LocalDate monthDate = LocalDateTime.ofInstant(mid, ZoneOffset.UTC).toLocalDate();
            segments.add(new TithiSegment(lo, hi, buildInfo(meeusTithiAt(mid), monthDate, city), i > 0, i < last));
        }
        return segments;
    }

    // ── Tithi → date (typed) ────────────────────────────────────────────────

    /** Tithi spec → first matching Gregorian date in the year, or {@code null}. */
    public LocalDate findDate(LunarMonth month, Tithi tithi, int year, City city) {
        List<LocalDate> dates = findDates(month, tithi, year, city);
        return dates.isEmpty() ? null : dates.get(0);
    }

    /** Tithi spec → all matching Gregorian dates in the year (adhika-aware). */
    public List<LocalDate> findDates(LunarMonth month, Tithi tithi, int year, City city) {
        return findDatesImpl(month, tithi.paksha(), tithi.dayInPaksha(), year, city.name());
    }

    /** Next occurrence of {@code tithi} in {@code month} at {@code city} on/after {@code from}; {@code null} if none within ~400 days. */
    public LocalDate findNext(LunarMonth month, Tithi tithi, City city, LocalDate from) {
        return findNextImpl(month, tithi.paksha(), tithi.dayInPaksha(), city.name(), from);
    }

    // Shared implementations (city keyed by canonical name / location key).
    List<LocalDate> findDatesImpl(LunarMonth month, Paksha paksha, int tithiInPaksha, int year, String city) {
        return getFinder(city).findInYear(month, paksha, tithiInPaksha, year, false);
    }

    LocalDate findNextImpl(LunarMonth month, Paksha paksha, int tithiInPaksha, String city, LocalDate from) {
        LocalDate start = from != null ? from : LocalDate.now();
        int target = paksha == Paksha.SHUKLA ? tithiInPaksha : tithiInPaksha + 15;
        CityLocation loc = City.getLocation(city);
        LunarMonthResolver resolver = getResolver(city);
        for (int i = 0; i < 400; i++) {
            LocalDate dt = start.plusDays(i);
            int t = Astronomy.tithiAt(Astronomy.computeSunrise(dt, loc, convention));
            if (t == target && resolver.getMonthInfo(dt).month == month) return dt;
        }
        return null;
    }

    // ── Festivals ───────────────────────────────────────────────────────────

    /** Festival → date with muhurta rules applied; {@code null} if it doesn't occur. */
    public FestivalDate dateFor(Festival fest, int year, City city) {
        return dateForImpl(fest, year, city.name());
    }

    FestivalDate dateForImpl(Festival fest, int year, String city) {
        return FestivalFinder.findFestivalDate(fest, year, city, getPurnimantFinder(city), convention);
    }

    /** Recurring festival (e.g. monthly Ekadashi/Purnima) → all occurrences in the year. */
    public List<FestivalDate> recurringDates(Festival fest, int year, City city) {
        return recurringDatesImpl(fest, year, city.name());
    }

    List<FestivalDate> recurringDatesImpl(Festival fest, int year, String city) {
        return FestivalFinder.findRecurringDates(fest, year, city, convention, getResolver(city));
    }

    /**
     * Sunrise as a UTC {@link Instant} for {@code date} at {@code city}.
     *
     * <p>Meeus astronomy (~1-minute accuracy); there is <b>no</b> per-city correction
     * (the correction tables adjust tithi, not sun times). Convert to local time using
     * the city's offset. At extreme latitudes on a no-sunrise day the value is a
     * clamped approximation.
     */
    public Instant sunrise(LocalDate date, City city) {
        return sunriseImpl(date, city.name());
    }

    Instant sunriseImpl(LocalDate date, String city) {
        return Astronomy.computeSunrise(date, City.getLocation(city), convention).toInstant(ZoneOffset.UTC);
    }

    /** Sunset as a UTC {@link Instant} for {@code date} at {@code city}. See {@link #sunrise}. */
    public Instant sunset(LocalDate date, City city) {
        return sunsetImpl(date, city.name());
    }

    Instant sunsetImpl(LocalDate date, String city) {
        return Astronomy.computeSunset(date, City.getLocation(city), convention).toInstant(ZoneOffset.UTC);
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

    private static int meeusTithiAt(Instant utcInstant) {
        return Astronomy.tithiAt(LocalDateTime.ofInstant(utcInstant, ZoneOffset.UTC));
    }

    /** All tithi transition instants in {@code [start, end)} (1-hour scan + 1s bisection). */
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
                // Override with the Swiss-exact instant from the global (city-independent)
                // transition correction when one exists within ±60 min. No-op until the
                // DATA table is generated (see GlobalTransitionCorrections).
                if (b.isAfter(start) && b.isBefore(end)) out.add(GlobalTransitionCorrections.correctTransition(b));
                loTithi = t;
            }
            segLo = cur;
            if (!cur.isBefore(end)) break;
            probe = probe.plus(step);
        }
        return out;
    }

    private static Instant bisect(Instant lo, Instant hi, int startTithi) {
        while (Duration.between(lo, hi).toMillis() > 1000) {
            Instant mid = lo.plusMillis(Duration.between(lo, hi).toMillis() / 2);
            if (meeusTithiAt(mid) == startTithi) lo = mid; else hi = mid;
        }
        return hi;
    }

    private LunarMonthResolver getResolver(String city) {
        return resolverCache.computeIfAbsent(city, c -> new LunarMonthResolver(monthSystem, c, convention));
    }

    private TithiFinder getFinder(String city) {
        return finderCache.computeIfAbsent(city, c -> new TithiFinder(monthSystem, c, convention));
    }

    private TithiFinder getPurnimantFinder(String city) {
        return purnimantFinderCache.computeIfAbsent(city, c -> new TithiFinder(MonthSystem.PURNIMANT, c, convention));
    }
}
