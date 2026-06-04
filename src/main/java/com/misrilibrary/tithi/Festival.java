package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import java.util.*;

/**
 * A Hindu festival definition: lunar month, paksha, tithi, and the muhurta rule
 * that determines which Gregorian date the festival falls on.
 *
 * <p>Use the pre-defined constants (e.g. {@code Festival.DIWALI}) or create custom
 * festivals with the public constructor.
 *
 * @see com.misrilibrary.tithi.Panchang#dateFor(Festival, int, String)
 */
public class Festival {
    /** Short identifier (e.g. "maha_shivaratri"). */
    public final String id;
    /** Human-readable name (e.g. "Maha Shivaratri"). */
    public final String name;
    /** Lunar month in which the festival falls (Purnimant convention). */
    public final LunarMonth month;
    /** Fortnight (Shukla or Krishna). */
    public final Paksha paksha;
    /** Tithi number within the paksha (1–15). */
    public final int tithiInPaksha;
    /** Rule for picking the correct Gregorian date when the tithi spans two days. */
    public final MuhurtaRule muhurta;

    public Festival(String id, String name, LunarMonth month, Paksha paksha, int tithiInPaksha, MuhurtaRule muhurta) {
        this.id = id; this.name = name; this.month = month; this.paksha = paksha;
        this.tithiInPaksha = tithiInPaksha; this.muhurta = muhurta;
    }

    /** Absolute tithi number (1–30) derived from paksha + tithiInPaksha. */
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

    /** All built-in festival definitions. */
    public static List<Festival> all() { return ALL; }
}
