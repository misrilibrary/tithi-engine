package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import java.util.*;

/** A Hindu festival definition: tithi spec + muhurta rule. */
public class Festival {
    public final String id;
    public final String name;
    public final LunarMonth month;
    public final Paksha paksha;
    public final int tithiInPaksha;
    public final MuhurtaRule muhurta;

    public Festival(String id, String name, LunarMonth month, Paksha paksha, int tithiInPaksha, MuhurtaRule muhurta) {
        this.id = id; this.name = name; this.month = month; this.paksha = paksha;
        this.tithiInPaksha = tithiInPaksha; this.muhurta = muhurta;
    }

    public int getTithiNumber() { return paksha == Paksha.SHUKLA ? tithiInPaksha : tithiInPaksha + 15; }

    public static final Festival MAHA_SHIVARATRI = new Festival("maha_shivaratri", "Maha Shivaratri", LunarMonth.PHALGUNA, Paksha.KRISHNA, 14, MuhurtaRule.NISHITA);
    public static final Festival HOLIKA_DAHAN = new Festival("holika_dahan", "Holika Dahan", LunarMonth.PHALGUNA, Paksha.SHUKLA, 15, MuhurtaRule.PRADOSH);
    public static final Festival RAM_NAVAMI = new Festival("ram_navami", "Ram Navami", LunarMonth.CHAITRA, Paksha.SHUKLA, 9, MuhurtaRule.MADHYAHNA);
    public static final Festival AKSHAYA_TRITIYA = new Festival("akshaya_tritiya", "Akshaya Tritiya", LunarMonth.VAISHAKHA, Paksha.SHUKLA, 3, MuhurtaRule.MADHYAHNA);
    public static final Festival GURU_PURNIMA = new Festival("guru_purnima", "Guru Purnima", LunarMonth.ASHADHA, Paksha.SHUKLA, 15, MuhurtaRule.SUNRISE);
    public static final Festival RAKSHA_BANDHAN = new Festival("raksha_bandhan", "Raksha Bandhan", LunarMonth.SHRAVANA, Paksha.SHUKLA, 15, MuhurtaRule.SUNRISE);
    public static final Festival JANMASHTAMI_SMARTA = new Festival("janmashtami_smarta", "Janmashtami (Smarta)", LunarMonth.BHADRAPADA, Paksha.KRISHNA, 8, MuhurtaRule.NISHITA);
    public static final Festival JANMASHTAMI_ISKCON = new Festival("janmashtami_iskcon", "Janmashtami (ISKCON)", LunarMonth.BHADRAPADA, Paksha.KRISHNA, 8, MuhurtaRule.SUNRISE);
    public static final Festival GANESH_CHATURTHI = new Festival("ganesh_chaturthi", "Ganesh Chaturthi", LunarMonth.BHADRAPADA, Paksha.SHUKLA, 4, MuhurtaRule.SUNRISE);
    public static final Festival VIJAYADASHAMI = new Festival("vijayadashami", "Vijayadashami", LunarMonth.ASHVINA, Paksha.SHUKLA, 10, MuhurtaRule.SUNRISE);
    public static final Festival DIWALI = new Festival("diwali", "Diwali / Lakshmi Puja", LunarMonth.KARTIKA, Paksha.KRISHNA, 15, MuhurtaRule.PRADOSH);

    private static final List<Festival> ALL = List.of(
        MAHA_SHIVARATRI, HOLIKA_DAHAN, RAM_NAVAMI, AKSHAYA_TRITIYA,
        GURU_PURNIMA, RAKSHA_BANDHAN, JANMASHTAMI_SMARTA, JANMASHTAMI_ISKCON,
        GANESH_CHATURTHI, VIJAYADASHAMI, DIWALI
    );

    public static List<Festival> all() { return ALL; }
}
