package com.misrilibrary.tithi.model;

import com.misrilibrary.tithi.Festival;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Result of finding a festival date, including the tithi span and (for non-sunrise
 * rules) the muhurta window. All instants are UTC.
 *
 * @see com.misrilibrary.tithi.Panchang#dateFor(Festival, int, String)
 * @see com.misrilibrary.tithi.Panchang#recurringDates(Festival, int, com.misrilibrary.tithi.City)
 */
public class FestivalDate {
    private final Festival festival;
    private final LocalDate date;
    private final Instant tithiStart;
    private final Instant tithiEnd;
    private final Instant muhurtaStart; // nullable (null for the sunrise rule)
    private final Instant muhurtaEnd;   // nullable
    private final LunarMonth month;     // actual occurrence month
    private final boolean isAdhika;     // true if the occurrence falls in an adhika (leap) month

    /**
     * Full constructor. For non-recurring festivals {@code month} equals
     * {@link Festival#month}; for recurring festivals it is the month resolved
     * from the occurrence date.
     */
    public FestivalDate(Festival festival, LocalDate date, Instant tithiStart, Instant tithiEnd,
                        Instant muhurtaStart, Instant muhurtaEnd, LunarMonth month, boolean isAdhika) {
        this.festival = festival;
        this.date = date;
        this.tithiStart = tithiStart;
        this.tithiEnd = tithiEnd;
        this.muhurtaStart = muhurtaStart;
        this.muhurtaEnd = muhurtaEnd;
        this.month = month;
        this.isAdhika = isAdhika;
    }

    /**
     * Convenience constructor for non-recurring festivals: {@code month} defaults
     * to the festival definition's month and {@code isAdhika} to {@code false}.
     */
    public FestivalDate(Festival festival, LocalDate date, Instant tithiStart, Instant tithiEnd,
                        Instant muhurtaStart, Instant muhurtaEnd) {
        this(festival, date, tithiStart, tithiEnd, muhurtaStart, muhurtaEnd, festival.month, false);
    }

    /** The festival definition this date is for. */
    public Festival getFestival() { return festival; }

    /** The Gregorian observance date. */
    public LocalDate getDate() { return date; }

    /** UTC instant the target tithi begins. */
    public Instant getTithiStart() { return tithiStart; }

    /** UTC instant the target tithi ends. */
    public Instant getTithiEnd() { return tithiEnd; }

    /** UTC start of the muhurta window, or {@code null} for the sunrise rule. */
    public Instant getMuhurtaStart() { return muhurtaStart; }

    /** UTC end of the muhurta window, or {@code null} for the sunrise rule. */
    public Instant getMuhurtaEnd() { return muhurtaEnd; }

    /**
     * The actual lunar month this occurrence falls in. For non-recurring
     * festivals this equals {@link Festival#month}; for recurring festivals it is
     * the month resolved from the occurrence date.
     */
    public LunarMonth getMonth() { return month; }

    /** Whether this occurrence falls in an adhika (leap) month. */
    public boolean isAdhika() { return isAdhika; }

    @Override
    public String toString() { return festival.name + " " + date; }
}
