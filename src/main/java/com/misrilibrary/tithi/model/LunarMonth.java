package com.misrilibrary.tithi.model;

public enum LunarMonth {
    CHAITRA("Chaitra"),
    VAISHAKHA("Vaishakha"),
    JYESHTHA("Jyeshtha"),
    ASHADHA("Ashadha"),
    SHRAVANA("Shravana"),
    BHADRAPADA("Bhadrapada"),
    ASHVINA("Ashvina"),
    KARTIKA("Kartika"),
    MARGASHIRSHA("Margashirsha"),
    PAUSHA("Pausha"),
    MAGHA("Magha"),
    PHALGUNA("Phalguna");

    private final String displayName;

    LunarMonth(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    public LunarMonth next() {
        return values()[(ordinal() + 1) % 12];
    }

    public LunarMonth prev() {
        return values()[(ordinal() + 11) % 12];
    }
}
