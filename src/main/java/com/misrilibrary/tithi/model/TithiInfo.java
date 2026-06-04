package com.misrilibrary.tithi.model;

/**
 * Immutable result of a tithi lookup — contains the tithi number, name, paksha,
 * lunar month, and a human-readable display string.
 *
 * <p>Returned by {@link com.misrilibrary.tithi.Panchang#forDate}.
 */
public class TithiInfo {
    private final int tithiNumber;       // 1-30
    private final String tithiName;
    private final Paksha paksha;
    private final int tithiInPaksha;     // 1-15
    private final LunarMonth month;
    private final boolean adhika;
    private final String displayName;

    public TithiInfo(int tithiNumber, String tithiName, Paksha paksha, int tithiInPaksha,
                     LunarMonth month, boolean adhika, String displayName) {
        this.tithiNumber = tithiNumber;
        this.tithiName = tithiName;
        this.paksha = paksha;
        this.tithiInPaksha = tithiInPaksha;
        this.month = month;
        this.adhika = adhika;
        this.displayName = displayName;
    }

    /** Absolute tithi number (1–30; 1–15 = Shukla, 16–30 = Krishna). */
    public int getTithiNumber() { return tithiNumber; }

    /** Sanskrit tithi name (e.g. "Pratipada", "Ashtami", "Purnima"). */
    public String getTithiName() { return tithiName; }

    /** Fortnight — Shukla (waxing) or Krishna (waning). */
    public Paksha getPaksha() { return paksha; }

    /** Tithi number within the paksha (1–15). */
    public int getTithiInPaksha() { return tithiInPaksha; }

    /** Lunar month this tithi falls in. */
    public LunarMonth getMonth() { return month; }

    /** Whether this is an adhika (intercalary) month occurrence. */
    public boolean isAdhika() { return adhika; }

    /** Human-readable string, e.g. "Phalguna Krishna Trayodashi". */
    public String getDisplayName() { return displayName; }

    @Override
    public String toString() { return displayName; }
}
