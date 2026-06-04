package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.Paksha;

class TithiUtils {

    private static final String[] TITHI_NAMES = {
        "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
        "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
        "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Purnima",
        "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
        "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
        "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Amavasya"
    };

    public static String getTithiName(int tithiNumber) {
        if (tithiNumber < 1 || tithiNumber > 30) return "Unknown";
        return TITHI_NAMES[tithiNumber - 1];
    }

    public static Paksha getPaksha(int tithiNumber) {
        return tithiNumber <= 15 ? Paksha.SHUKLA : Paksha.KRISHNA;
    }

    public static int tithiInPaksha(int tithiNumber) {
        return tithiNumber <= 15 ? tithiNumber : tithiNumber - 15;
    }
}
