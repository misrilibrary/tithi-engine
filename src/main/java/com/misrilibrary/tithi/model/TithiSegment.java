package com.misrilibrary.tithi.model;

import java.time.Instant;

/**
 * A contiguous span of a single tithi within an enumeration window.
 *
 * <p>Returned by {@link com.misrilibrary.tithi.Panchang#tithiSegments}. The window
 * is partitioned by every tithi transition inside it, so N transitions yield N+1
 * segments. {@link #getStartUtc()}/{@link #getEndUtc()} are clipped to the window
 * edges; {@link #isStartTransition()}/{@link #isEndTransition()} indicate whether
 * that edge is a real tithi boundary (true) or just the window clip (false).
 */
public class TithiSegment {
    private final Instant startUtc; // inclusive
    private final Instant endUtc;   // exclusive
    private final TithiInfo tithi;
    private final boolean startIsTransition;
    private final boolean endIsTransition;

    public TithiSegment(Instant startUtc, Instant endUtc, TithiInfo tithi,
                        boolean startIsTransition, boolean endIsTransition) {
        this.startUtc = startUtc;
        this.endUtc = endUtc;
        this.tithi = tithi;
        this.startIsTransition = startIsTransition;
        this.endIsTransition = endIsTransition;
    }

    /** Start of the segment (UTC, inclusive). */
    public Instant getStartUtc() { return startUtc; }

    /** End of the segment (UTC, exclusive). */
    public Instant getEndUtc() { return endUtc; }

    /** The tithi active during this segment, fully resolved. */
    public TithiInfo getTithi() { return tithi; }

    /** True if the start edge is a real tithi transition (not just the window clip). */
    public boolean isStartTransition() { return startIsTransition; }

    /** True if the end edge is a real tithi transition (not just the window clip). */
    public boolean isEndTransition() { return endIsTransition; }

    @Override
    public String toString() { return tithi.getDisplayName() + " [" + startUtc + " → " + endUtc + "]"; }
}
