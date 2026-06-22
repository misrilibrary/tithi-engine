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

    /** See {@link Panchang#tithiOnDate(LocalDate, String)}. */
    public TithiInfo tithiOnDate(LocalDate date) {
        return p.tithiOnDate(date, loc.key());
    }

    /** See {@link Panchang#tithiAtInstant(Instant, String, ZoneOffset)}. */
    public TithiInfo tithiAtInstant(Instant utcInstant, ZoneOffset offset) {
        return p.tithiAtInstant(utcInstant, loc.key(), offset);
    }

    /** See {@link Panchang#tithiSegments(Instant, Instant, String, ZoneOffset)}. */
    public List<TithiSegment> tithiSegments(Instant windowStartUtc, Instant windowEndUtc, ZoneOffset offset) {
        return p.tithiSegments(windowStartUtc, windowEndUtc, loc.key(), offset);
    }

    /** See {@link Panchang#getDate(LunarMonth, Paksha, int, int, String)}. */
    public LocalDate getDate(LunarMonth month, Paksha paksha, int tithiInPaksha, int year) {
        return p.getDate(month, paksha, tithiInPaksha, year, loc.key());
    }

    /** See {@link Panchang#getDates(LunarMonth, Paksha, int, int, String)}. */
    public List<LocalDate> getDates(LunarMonth month, Paksha paksha, int tithiInPaksha, int year) {
        return p.getDates(month, paksha, tithiInPaksha, year, loc.key());
    }

    /** See {@link Panchang#findNext(LunarMonth, Paksha, int, String, LocalDate)}. */
    public LocalDate findNext(LunarMonth month, Paksha paksha, int tithiInPaksha, LocalDate from) {
        return p.findNext(month, paksha, tithiInPaksha, loc.key(), from);
    }

    /** See {@link Panchang#dateFor(Festival, int, String)}. */
    public FestivalDate dateFor(Festival fest, int year) {
        return p.dateFor(fest, year, loc.key());
    }

    /** See {@link Panchang#recurringDates(Festival, int, String)}. */
    public List<FestivalDate> recurringDates(Festival fest, int year) {
        return p.recurringDates(fest, year, loc.key());
    }

    /** See {@link Panchang#sunrise(LocalDate, String)}. */
    public Instant sunrise(LocalDate date) {
        return p.sunrise(date, loc.key());
    }

    /** See {@link Panchang#sunset(LocalDate, String)}. */
    public Instant sunset(LocalDate date) {
        return p.sunset(date, loc.key());
    }
}
