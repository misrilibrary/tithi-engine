package com.misrilibrary.tithi.model;

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

    public int getTithiNumber() { return tithiNumber; }
    public String getTithiName() { return tithiName; }
    public Paksha getPaksha() { return paksha; }
    public int getTithiInPaksha() { return tithiInPaksha; }
    public LunarMonth getMonth() { return month; }
    public boolean isAdhika() { return adhika; }
    public String getDisplayName() { return displayName; }

    @Override
    public String toString() { return displayName; }
}
