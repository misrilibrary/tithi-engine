package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class MonthConverterTest {

    @Test @DisplayName("Shukla Paksha: no change in either direction")
    void shuklaNoChange() {
        assertEquals(LunarMonth.PAUSHA, MonthConverter.convert(LunarMonth.PAUSHA, Paksha.SHUKLA, MonthSystem.PURNIMANT, MonthSystem.AMANT));
        assertEquals(LunarMonth.MAGHA, MonthConverter.convert(LunarMonth.MAGHA, Paksha.SHUKLA, MonthSystem.AMANT, MonthSystem.PURNIMANT));
    }

    @Test @DisplayName("Krishna: Purnimant → Amant (previous month)")
    void krishnaPurnimantToAmant() {
        assertEquals(LunarMonth.MAGHA, MonthConverter.convert(LunarMonth.PHALGUNA, Paksha.KRISHNA, MonthSystem.PURNIMANT, MonthSystem.AMANT));
        assertEquals(LunarMonth.PAUSHA, MonthConverter.convert(LunarMonth.MAGHA, Paksha.KRISHNA, MonthSystem.PURNIMANT, MonthSystem.AMANT));
        assertEquals(LunarMonth.PHALGUNA, MonthConverter.convert(LunarMonth.CHAITRA, Paksha.KRISHNA, MonthSystem.PURNIMANT, MonthSystem.AMANT));
    }

    @Test @DisplayName("Krishna: Amant → Purnimant (next month)")
    void krishnaAmantToPurnimant() {
        assertEquals(LunarMonth.PHALGUNA, MonthConverter.convert(LunarMonth.MAGHA, Paksha.KRISHNA, MonthSystem.AMANT, MonthSystem.PURNIMANT));
        assertEquals(LunarMonth.MAGHA, MonthConverter.convert(LunarMonth.PAUSHA, Paksha.KRISHNA, MonthSystem.AMANT, MonthSystem.PURNIMANT));
        assertEquals(LunarMonth.CHAITRA, MonthConverter.convert(LunarMonth.PHALGUNA, Paksha.KRISHNA, MonthSystem.AMANT, MonthSystem.PURNIMANT));
    }

    @Test @DisplayName("Same system: no change")
    void sameSystem() {
        assertEquals(LunarMonth.PAUSHA, MonthConverter.convert(LunarMonth.PAUSHA, Paksha.KRISHNA, MonthSystem.PURNIMANT, MonthSystem.PURNIMANT));
        assertEquals(LunarMonth.MAGHA, MonthConverter.convert(LunarMonth.MAGHA, Paksha.KRISHNA, MonthSystem.AMANT, MonthSystem.AMANT));
    }

    @Test @DisplayName("Round-trip: Purnimant → Amant → Purnimant")
    void roundTrip() {
        LunarMonth si = MonthConverter.convert(LunarMonth.PHALGUNA, Paksha.KRISHNA, MonthSystem.PURNIMANT, MonthSystem.AMANT);
        LunarMonth ni = MonthConverter.convert(si, Paksha.KRISHNA, MonthSystem.AMANT, MonthSystem.PURNIMANT);
        assertEquals(LunarMonth.PHALGUNA, ni);
    }

    @Test @DisplayName("Verified: Feb 4 2015 NI=Phalguna Kr → SI=Magha Kr")
    void drikVerified() {
        assertEquals(LunarMonth.MAGHA, MonthConverter.convert(LunarMonth.PHALGUNA, Paksha.KRISHNA, MonthSystem.PURNIMANT, MonthSystem.AMANT));
    }
}
