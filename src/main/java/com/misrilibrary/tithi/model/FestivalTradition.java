package com.misrilibrary.tithi.model;

/**
 * Tradition / sampradaya for festivals that have variant observance dates
 * (e.g. Janmashtami differs between Smarta, Vaishnava, and Kashmiri reckonings).
 */
public enum FestivalTradition {
    /** General / pan-Hindu reckoning. */
    GENERAL,
    /** Smarta (orthodox) reckoning. */
    SMARTA,
    /** Vaishnava (e.g. ISKCON) reckoning. */
    VAISHNAVA,
    /** Kashmiri reckoning. */
    KASHMIRI
}
