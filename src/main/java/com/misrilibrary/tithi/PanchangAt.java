package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

/**
 * A {@link Panchang} bound to a {@link Location}; forwards to the city-keyed API
 * dropping the {@code city} argument. Obtain via {@link Panchang#at(Location)}.
 * {@link #source()} reports whether the location is Swiss-corrected or Meeus-only.
 */
public final class PanchangAt {

    private final Panchang p;
    private final Location loc;

    PanchangAt(Panchang p, Location loc) {
        this.p = p;
        this.loc = loc;
    }

    /** Accuracy tier of the bound location. */
    public LocationSource source() { return loc.source(); }

    /** See {@link Panchang#tithiOnDate(LocalDate, City)}. */
    public TithiInfo tithiOnDate(LocalDate date) {
        return p.tithiOnDateImpl(date, loc.key());
    }

    /** See {@link Panchang#tithiAtInstant(Instant, City, ZoneOffset)}. */
    public TithiInfo tithiAtInstant(Instant utcInstant, ZoneOffset offset) {
        return p.tithiAtInstantImpl(utcInstant, loc.key(), offset);
    }

    /** See {@link Panchang#tithiSegments(Instant, Instant, City, ZoneOffset)}. */
    public List<TithiSegment> tithiSegments(Instant windowStartUtc, Instant windowEndUtc, ZoneOffset offset) {
        return p.tithiSegmentsImpl(windowStartUtc, windowEndUtc, loc.key(), offset);
    }

    /** See {@link Panchang#findDate(LunarMonth, Tithi, int, City)}. */
    public LocalDate findDate(LunarMonth month, Tithi tithi, int year) {
        List<LocalDate> dates = p.findDatesImpl(month, tithi.paksha(), tithi.dayInPaksha(), year, loc.key());
        return dates.isEmpty() ? null : dates.get(0);
    }

    /** See {@link Panchang#findDates(LunarMonth, Tithi, int, City)}. */
    public List<LocalDate> findDates(LunarMonth month, Tithi tithi, int year) {
        return p.findDatesImpl(month, tithi.paksha(), tithi.dayInPaksha(), year, loc.key());
    }

    /** See {@link Panchang#findNext(LunarMonth, Tithi, City, LocalDate)}. */
    public LocalDate findNext(LunarMonth month, Tithi tithi, LocalDate from) {
        return p.findNextImpl(month, tithi.paksha(), tithi.dayInPaksha(), loc.key(), from);
    }

    /** See {@link Panchang#dateFor(Festival, int, City)}. */
    public FestivalDate dateFor(Festival fest, int year) {
        return p.dateForImpl(fest, year, loc.key());
    }

    /** See {@link Panchang#recurringDates(Festival, int, City)}. */
    public List<FestivalDate> recurringDates(Festival fest, int year) {
        return p.recurringDatesImpl(fest, year, loc.key());
    }

    /** See {@link Panchang#sunrise(LocalDate, City)}. */
    public Instant sunrise(LocalDate date) {
        return p.sunriseImpl(date, loc.key());
    }

    /** See {@link Panchang#sunset(LocalDate, City)}. */
    public Instant sunset(LocalDate date) {
        return p.sunsetImpl(date, loc.key());
    }
}
