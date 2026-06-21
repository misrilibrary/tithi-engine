package com.misrilibrary.tithi.model;

import com.misrilibrary.tithi.Festival;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Result of finding a festival date, including the tithi span and (for non-sunrise
 * rules) the muhurta window. All instants are UTC.
 *
 * @see com.misrilibrary.tithi.Panchang#dateFor(Festival, int, String)
 * @see com.misrilibrary.tithi.Panchang#recurringDates(Festival, int, String)
 */
public class FestivalDate {
    private final Festival festival;
    private final LocalDate date;
    private final Instant tithiStart;
    private final Instant tithiEnd;
    private final Instant muhurtaStart; // nullable (null for the sunrise rule)
    private final Instant muhurtaEnd;   // nullable

    public FestivalDate(Festival festival, LocalDate date, Instant tithiStart, Instant tithiEnd,
                        Instant muhurtaStart, Instant muhurtaEnd) {
        this.festival = festival;
        this.date = date;
        this.tithiStart = tithiStart;
        this.tithiEnd = tithiEnd;
        this.muhurtaStart = muhurtaStart;
        this.muhurtaEnd = muhurtaEnd;
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

    @Override
    public String toString() { return festival.name + " " + date; }
}
