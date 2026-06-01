package com.misrilibrary.tithi.model;

public enum Paksha {
    SHUKLA, KRISHNA;

    public static Paksha fromTithiNumber(int tithiNumber) {
        return tithiNumber <= 15 ? SHUKLA : KRISHNA;
    }
}
