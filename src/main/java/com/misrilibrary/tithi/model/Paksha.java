package com.misrilibrary.tithi.model;

/**
 * Lunar fortnight — the two halves of a Hindu lunar month.
 */
public enum Paksha {
    /** Waxing fortnight (bright half, tithis 1–15). */
    SHUKLA,
    /** Waning fortnight (dark half, tithis 16–30). */
    KRISHNA;

    /** Determine paksha from an absolute tithi number (1–30). */
    public static Paksha fromTithiNumber(int tithiNumber) {
        return tithiNumber <= 15 ? SHUKLA : KRISHNA;
    }
}
