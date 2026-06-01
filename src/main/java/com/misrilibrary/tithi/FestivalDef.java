package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import java.util.*;

/** Static definition of a Hindu festival. */
public class FestivalDef {
    public final String id;
    public final String name;
    public final LunarMonth month;
    public final Paksha paksha;
    public final int tithiInPaksha;
    public final MuhurtaRule muhurta;

    public FestivalDef(String id, String name, LunarMonth month, Paksha paksha, int tithiInPaksha, MuhurtaRule muhurta) {
        this.id = id; this.name = name; this.month = month; this.paksha = paksha;
        this.tithiInPaksha = tithiInPaksha; this.muhurta = muhurta;
    }

    public int getTithiNumber() { return paksha == Paksha.SHUKLA ? tithiInPaksha : tithiInPaksha + 15; }

    public static final List<FestivalDef> ALL = List.of(
        new FestivalDef("maha_shivaratri", "Maha Shivaratri", LunarMonth.PHALGUNA, Paksha.KRISHNA, 14, MuhurtaRule.NISHITA),
        new FestivalDef("holika_dahan", "Holika Dahan", LunarMonth.PHALGUNA, Paksha.SHUKLA, 15, MuhurtaRule.PRADOSH),
        new FestivalDef("ram_navami", "Ram Navami", LunarMonth.CHAITRA, Paksha.SHUKLA, 9, MuhurtaRule.MADHYAHNA),
        new FestivalDef("akshaya_tritiya", "Akshaya Tritiya", LunarMonth.VAISHAKHA, Paksha.SHUKLA, 3, MuhurtaRule.MADHYAHNA),
        new FestivalDef("guru_purnima", "Guru Purnima", LunarMonth.ASHADHA, Paksha.SHUKLA, 15, MuhurtaRule.SUNRISE),
        new FestivalDef("raksha_bandhan", "Raksha Bandhan", LunarMonth.SHRAVANA, Paksha.SHUKLA, 15, MuhurtaRule.SUNRISE),
        new FestivalDef("janmashtami_smarta", "Janmashtami (Smarta)", LunarMonth.BHADRAPADA, Paksha.KRISHNA, 8, MuhurtaRule.NISHITA),
        new FestivalDef("janmashtami_iskcon", "Janmashtami (ISKCON)", LunarMonth.BHADRAPADA, Paksha.KRISHNA, 8, MuhurtaRule.SUNRISE),
        new FestivalDef("ganesh_chaturthi", "Ganesh Chaturthi", LunarMonth.BHADRAPADA, Paksha.SHUKLA, 4, MuhurtaRule.SUNRISE),
        new FestivalDef("vijayadashami", "Vijayadashami", LunarMonth.ASHVINA, Paksha.SHUKLA, 10, MuhurtaRule.SUNRISE),
        new FestivalDef("diwali", "Diwali / Lakshmi Puja", LunarMonth.KARTIKA, Paksha.KRISHNA, 15, MuhurtaRule.PRADOSH)
    );
}
