package com.misrilibrary.tithi.model;

/**
 * Time-of-day rule for determining which date a festival falls on.
 * When the tithi spans two days, the muhurta rule picks the day
 * during which the relevant moment occurs.
 */
public enum MuhurtaRule {
    /** Festival observed on the day whose sunrise starts the tithi. */
    SUNRISE,
    /** Midnight — midpoint of the night (e.g. Maha Shivaratri). */
    NISHITA,
    /** Midday — midpoint between sunrise and sunset (e.g. Ram Navami). */
    MADHYAHNA,
    /** Evening — roughly one hour after sunset (e.g. Diwali). */
    PRADOSH
}
