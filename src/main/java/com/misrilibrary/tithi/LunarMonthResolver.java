package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.CityCorrections;
import com.misrilibrary.tithi.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Resolves the lunar month for any given date using moment-based sankranti assignment.
 * Handles adhika (intercalary), kshaya (dropped), and double Purnima/Amavasya.
 */
class LunarMonthResolver {

    private static final LocalDate EPOCH = LocalDate.of(1900, 1, 1);

    private final MonthSystem system;
    private final String city;
    private final SunriseConvention convention;
    private final Map<Integer, List<MonthSpan>> cache = new HashMap<>();

    public LunarMonthResolver(MonthSystem system, String city) {
        this(system, city, SunriseConvention.UPPER_LIMB);
    }

    public LunarMonthResolver(MonthSystem system, String city, SunriseConvention convention) {
        this.system = system;
        this.city = city;
        this.convention = convention;
    }

    public LunarMonthResolver(MonthSystem system) {
        this(system, City.DEFAULT_NAME);
    }

    /** Get the month info for a specific date. */
    public MonthInfo getMonthInfo(LocalDate date) {
        // Check current year, then adjacent years (spans overlap year boundaries)
        for (int y = date.getYear() - 1; y <= date.getYear() + 1; y++) {
            for (MonthSpan span : getSpansForYear(y)) {
                if (!date.isBefore(span.start) && date.isBefore(span.end)) {
                    return new MonthInfo(span.month, span.adhika);
                }
            }
        }
        return new MonthInfo(LunarMonth.CHAITRA, false); // should never reach
    }

    /** Get all month spans for a year (cached). */
    public List<MonthSpan> getSpansForYear(int year) {
        return cache.computeIfAbsent(year, this::buildSpans);
    }

    private List<MonthSpan> buildSpans(int year) {
        CityLocation loc = City.getLocation(city);
        CityCorrections corr = CityCorrections.forCity(city, convention);

        LocalDate scanStart = LocalDate.of(year - 1, 10, 1);
        LocalDate scanEnd = LocalDate.of(year + 1, 3, 1);

        // Find all Amavasya (T30) and Purnima (T15) dates
        List<LocalDate> amavasyas = new ArrayList<>();
        List<LocalDate> purnimas = new ArrayList<>();

        int prevTithi = -1;
        for (LocalDate dt = scanStart; dt.isBefore(scanEnd); dt = dt.plusDays(1)) {
            int dayIndex = (int) EPOCH.until(dt, ChronoUnit.DAYS);
            LocalDateTime sunrise = Astronomy.computeSunrise(dt, loc, convention);
            int meeusT = Astronomy.tithiAt(sunrise);
            // Use corrected tithi for boundary detection
            Integer corrT = corr.getCorrectedTithi(dayIndex);
            int effTithi = corrT != null ? corrT : meeusT;

            if (prevTithi >= 0) {
                if (effTithi == 30 && prevTithi != 30) {
                    amavasyas.add(correctAmavasya(dt, corr));
                } else if (effTithi == 30 && prevTithi == 30) {
                    // Double Amavasya: use last day directly
                    if (!amavasyas.isEmpty()) amavasyas.set(amavasyas.size() - 1, dt);
                } else if (prevTithi >= 28 && prevTithi < 30 && effTithi <= 2) {
                    amavasyas.add(correctAmavasya(dt.minusDays(1), corr));
                }
                if (effTithi == 15 && prevTithi != 15) {
                    purnimas.add(correctPurnima(dt, corr));
                } else if (effTithi == 15 && prevTithi == 15) {
                    // Double Purnima: use last day directly
                    if (!purnimas.isEmpty()) purnimas.set(purnimas.size() - 1, dt);
                } else if (prevTithi >= 13 && prevTithi < 15 && effTithi > 15 && effTithi <= 17) {
                    purnimas.add(correctPurnima(dt.minusDays(1), corr));
                }
            }
            prevTithi = effTithi;
        }

        // Build Amant spans with moment-based sankranti naming
        List<MonthSpan> amantSpans = new ArrayList<>();
        for (int i = 0; i < amavasyas.size() - 1; i++) {
            LocalDate spanStart = amavasyas.get(i).plusDays(1);
            LocalDate spanEnd = amavasyas.get(i + 1).plusDays(1);

            // Sun sign at new-moon moments
            LocalDateTime nm1 = newMoonMoment(amavasyas.get(i));
            LocalDateTime nm2 = newMoonMoment(amavasyas.get(i + 1));
            int signStart = siderealSunSign(nm1);
            int signEnd = siderealSunSign(nm2);
            int crossings = (signEnd - signStart + 12) % 12;

            if (crossings == 0) {
                // Adhika (no sankranti)
                amantSpans.add(new MonthSpan(spanStart, spanEnd, LunarMonth.CHAITRA, true));
            } else {
                // Named by first sankranti after starting new moon
                amantSpans.add(new MonthSpan(spanStart, spanEnd, signToMonth((signStart + 1) % 12), false));
            }
        }

        // Forward naming for adhika months
        for (int i = 0; i < amantSpans.size(); i++) {
            if (!amantSpans.get(i).adhika) continue;
            for (int j = i + 1; j < amantSpans.size(); j++) {
                if (!amantSpans.get(j).adhika) {
                    amantSpans.set(i, new MonthSpan(amantSpans.get(i).start, amantSpans.get(i).end,
                            amantSpans.get(j).month, true));
                    break;
                }
            }
        }

        // Build final spans based on system
        List<MonthSpan> spans = new ArrayList<>();
        if (system == MonthSystem.PURNIMANT) {
            for (MonthSpan am : amantSpans) {
                LocalDate purnima = null;
                for (LocalDate p : purnimas) {
                    if (!p.isBefore(am.start) && p.isBefore(am.end)) { purnima = p; break; }
                }
                if (purnima == null) { spans.add(am); continue; }
                LocalDate krishnaStart = purnima.plusDays(1);
                spans.add(new MonthSpan(am.start, krishnaStart, am.month, am.adhika));
                LunarMonth krishnaMonth = am.adhika ? am.month : am.month.next();
                spans.add(new MonthSpan(krishnaStart, am.end, krishnaMonth, am.adhika));
            }
        } else {
            spans.addAll(amantSpans);
        }

        // Filter to spans overlapping target year
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year + 1, 1, 1);
        List<MonthSpan> filtered = new ArrayList<>();
        for (MonthSpan s : spans) {
            if (s.start.isBefore(yearEnd) && s.end.isAfter(yearStart)) filtered.add(s);
        }
        return filtered;
    }

    private LocalDate correctAmavasya(LocalDate dt, CityCorrections corr) {
        int dayIndex = (int) EPOCH.until(dt, ChronoUnit.DAYS);
        Integer corrected = corr.getCorrectedAmavasya(dayIndex);
        return corrected != null ? EPOCH.plusDays(corrected) : dt;
    }

    private LocalDate correctPurnima(LocalDate dt, CityCorrections corr) {
        int dayIndex = (int) EPOCH.until(dt, ChronoUnit.DAYS);
        Integer corrected = corr.getCorrectedPurnima(dayIndex);
        return corrected != null ? EPOCH.plusDays(corrected) : dt;
    }

    private static int siderealSunSign(LocalDateTime dt) {
        return (int)(Astronomy.toSidereal(Astronomy.sunLongitude(dt), dt) / 30) % 12;
    }

    private static LunarMonth signToMonth(int sign) {
        return LunarMonth.values()[sign]; // 0=Mesha→Chaitra, 1=Vrishabha→Vaishakha, etc.
    }

    /** Bisect for exact new-moon moment near an Amavasya day. */
    private LocalDateTime newMoonMoment(LocalDate amavasyaDay) {
        LocalDateTime lo = amavasyaDay.minusDays(1).atStartOfDay();
        LocalDateTime hi = lo.plusDays(3);
        for (int i = 0; i < 44; i++) {
            LocalDateTime mid = lo.plusSeconds(ChronoUnit.SECONDS.between(lo, hi) / 2);
            if (signedElongation(mid) < 0) lo = mid; else hi = mid;
        }
        return lo.plusSeconds(ChronoUnit.SECONDS.between(lo, hi) / 2);
    }

    /** Moon-Sun elongation, normalized to (-180, 180]. Zero at new moon. */
    private static double signedElongation(LocalDateTime dt) {
        double sun = Astronomy.toSidereal(Astronomy.sunLongitude(dt), dt);
        double moon = Astronomy.toSidereal(Astronomy.moonLongitude(dt), dt);
        double e = (moon - sun) % 360;
        if (e < 0) e += 360;
        return e > 180 ? e - 360 : e;
    }

    // ─── Inner classes ───

    public static class MonthSpan {
        public final LocalDate start;
        public final LocalDate end;
        public final LunarMonth month;
        public final boolean adhika;

        public MonthSpan(LocalDate start, LocalDate end, LunarMonth month, boolean adhika) {
            this.start = start; this.end = end; this.month = month; this.adhika = adhika;
        }
    }

    public static class MonthInfo {
        public final LunarMonth month;
        public final boolean adhika;

        public MonthInfo(LunarMonth month, boolean adhika) {
            this.month = month; this.adhika = adhika;
        }
    }
}
