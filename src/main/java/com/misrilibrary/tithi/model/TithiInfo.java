package com.misrilibrary.tithi.model;

/**
 * Immutable result of a tithi lookup — contains the tithi number, name, paksha,
 * lunar month, and a human-readable display string.
 *
 * <p>Returned by {@link com.misrilibrary.tithi.Panchang#tithiOnDate}.
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

    // ── Canonical tithi names (1-15 within a paksha) ──
    private static final String[] NAMES = {
        "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
        "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
        "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Purnima"
    };

    private static String nameFor(int tithiNumber) {
        if (tithiNumber == 15) return "Purnima";
        if (tithiNumber == 30) return "Amavasya";
        return NAMES[(tithiNumber - 1) % 15];
    }

    private static LunarMonth convertMonth(LunarMonth month, Paksha paksha, MonthSystem from, MonthSystem to) {
        if (from == to || paksha == Paksha.SHUKLA) return month;
        return from == MonthSystem.PURNIMANT ? month.prev() : month.next();
    }

    /**
     * Build a {@link TithiInfo} for a STORED tithi spec (number + month + the
     * system it was recorded in), deriving paksha/name/position and the display
     * string. This is pure naming/rendering — no astronomy.
     *
     * <p>If {@code displaySystem} differs from {@code storedSystem}, the month
     * name is converted between Purnimant/Amant for display (the tithi itself is
     * unchanged). The single source of truth for rendering a saved tithi.
     *
     * @param tithiNumber  absolute tithi number (1–30)
     * @param month        lunar month, in {@code storedSystem} convention
     * @param storedSystem the month-system the tithi was recorded in
     * @param isAdhika     whether it was an adhika (leap) month occurrence
     * @param displaySystem month-system to render in; {@code null} = same as stored
     */
    public static TithiInfo fromStored(int tithiNumber, LunarMonth month, MonthSystem storedSystem,
                                       boolean isAdhika, MonthSystem displaySystem) {
        Paksha paksha = tithiNumber <= 15 ? Paksha.SHUKLA : Paksha.KRISHNA;
        String name = nameFor(tithiNumber);
        int inPaksha = tithiNumber <= 15 ? tithiNumber : tithiNumber - 15;
        MonthSystem target = displaySystem != null ? displaySystem : storedSystem;
        LunarMonth displayMonth = convertMonth(month, paksha, storedSystem, target);
        String pakshaStr = paksha == Paksha.SHUKLA ? "Shukla" : "Krishna";
        String adhikaPrefix = isAdhika ? "Adhika " : "";
        String display = adhikaPrefix + displayMonth.getDisplayName() + " " + pakshaStr + " " + name;
        return new TithiInfo(tithiNumber, name, paksha, inPaksha, displayMonth, isAdhika, display);
    }

    /** Convenience overload: render in the same system it was stored in. */
    public static TithiInfo fromStored(int tithiNumber, LunarMonth month, MonthSystem storedSystem) {
        return fromStored(tithiNumber, month, storedSystem, false, null);
    }
}
