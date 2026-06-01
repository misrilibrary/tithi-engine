package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;

/**
 * Convert lunar month between Purnimant and Amant systems.
 * Only Krishna Paksha differs: Purnimant assigns it to the NEXT month.
 */
public class MonthConverter {

    public static LunarMonth convert(LunarMonth month, Paksha paksha, MonthSystem from, MonthSystem to) {
        if (from == to) return month;
        if (paksha == Paksha.SHUKLA) return month;

        if (from == MonthSystem.PURNIMANT && to == MonthSystem.AMANT) {
            return month.prev();
        } else {
            return month.next();
        }
    }
}
