package com.misrilibrary.tithi.model;

/**
 * The twelve months of the Hindu lunar calendar, starting with Chaitra.
 */
public enum LunarMonth {
    /** Month 1 — March/April. */
    CHAITRA("Chaitra"),
    /** Month 2 — April/May. */
    VAISHAKHA("Vaishakha"),
    /** Month 3 — May/June. */
    JYESHTHA("Jyeshtha"),
    /** Month 4 — June/July. */
    ASHADHA("Ashadha"),
    /** Month 5 — July/August. */
    SHRAVANA("Shravana"),
    /** Month 6 — August/September. */
    BHADRAPADA("Bhadrapada"),
    /** Month 7 — September/October. */
    ASHVINA("Ashvina"),
    /** Month 8 — October/November. */
    KARTIKA("Kartika"),
    /** Month 9 — November/December. */
    MARGASHIRSHA("Margashirsha"),
    /** Month 10 — December/January. */
    PAUSHA("Pausha"),
    /** Month 11 — January/February. */
    MAGHA("Magha"),
    /** Month 12 — February/March. */
    PHALGUNA("Phalguna");

    private final String displayName;

    LunarMonth(String displayName) {
        this.displayName = displayName;
    }

    /** Human-readable month name (e.g. "Chaitra"). */
    public String getDisplayName() { return displayName; }

    /** Next month in the cycle (wraps from Phalguna → Chaitra). */
    public LunarMonth next() {
        return values()[(ordinal() + 1) % 12];
    }

    /** Previous month in the cycle (wraps from Chaitra → Phalguna). */
    public LunarMonth prev() {
        return values()[(ordinal() + 11) % 12];
    }
}
