package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.*;
import java.util.*;

/**
 * A Hindu festival definition: lunar month, paksha, tithi, the muhurta rule that
 * determines which Gregorian date the festival falls on, and its tradition.
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
    /** Tradition / sampradaya for festivals with variant dates. */
    public final FestivalTradition tradition;
    /** Whether enabled by default in a typical festival list. */
    public final boolean enabledByDefault;
    /** {@code true} = recurs every lunar month (the {@link #month} field is ignored). */
    public final boolean recurring;

    /** Full constructor. */
    public Festival(String id, String name, LunarMonth month, Paksha paksha, int tithiInPaksha,
                    MuhurtaRule muhurta, FestivalTradition tradition,
                    boolean enabledByDefault, boolean recurring) {
        this.id = id; this.name = name; this.month = month; this.paksha = paksha;
        this.tithiInPaksha = tithiInPaksha; this.muhurta = muhurta; this.tradition = tradition;
        this.enabledByDefault = enabledByDefault; this.recurring = recurring;
    }

    /** Convenience constructor — general tradition, enabled, non-recurring. */
    public Festival(String id, String name, LunarMonth month, Paksha paksha, int tithiInPaksha, MuhurtaRule muhurta) {
        this(id, name, month, paksha, tithiInPaksha, muhurta, FestivalTradition.GENERAL, true, false);
    }

    /** Absolute tithi number (1–30) derived from paksha + tithiInPaksha. */
    public int getTithiNumber() { return paksha == Paksha.SHUKLA ? tithiInPaksha : tithiInPaksha + 15; }

    public static final Festival MAHA_SHIVARATRI_KASHMIRI = new Festival("maha_shivaratri_kashmiri", "Herath", LunarMonth.PHALGUNA, Paksha.KRISHNA, 13, MuhurtaRule.NISHITA, FestivalTradition.KASHMIRI, true, false);
    public static final Festival MAHA_SHIVARATRI = new Festival("maha_shivaratri", "Maha Shivaratri", LunarMonth.PHALGUNA, Paksha.KRISHNA, 14, MuhurtaRule.NISHITA);
    public static final Festival RAM_NAVAMI = new Festival("ram_navami", "Ram Navami", LunarMonth.CHAITRA, Paksha.SHUKLA, 9, MuhurtaRule.MADHYAHNA);
    public static final Festival AKSHAYA_TRITIYA = new Festival("akshaya_tritiya", "Akshaya Tritiya", LunarMonth.VAISHAKHA, Paksha.SHUKLA, 3, MuhurtaRule.MADHYAHNA);
    public static final Festival GURU_PURNIMA = new Festival("guru_purnima", "Guru Purnima", LunarMonth.ASHADHA, Paksha.SHUKLA, 15, MuhurtaRule.SUNRISE);
    public static final Festival RAKSHA_BANDHAN = new Festival("raksha_bandhan", "Raksha Bandhan", LunarMonth.SHRAVANA, Paksha.SHUKLA, 15, MuhurtaRule.SUNRISE);
    public static final Festival JANMASHTAMI_KASHMIRI = new Festival("janmashtami_kashmiri", "Zarmasatam (Kashmiri)", LunarMonth.BHADRAPADA, Paksha.KRISHNA, 7, MuhurtaRule.NISHITA, FestivalTradition.KASHMIRI, true, false);
    public static final Festival JANMASHTAMI_SMARTA = new Festival("janmashtami_smarta", "Janmashtami (Smarta)", LunarMonth.BHADRAPADA, Paksha.KRISHNA, 8, MuhurtaRule.NISHITA, FestivalTradition.SMARTA, true, false);
    public static final Festival JANMASHTAMI_ISKCON = new Festival("janmashtami_iskcon", "Janmashtami (ISKCON)", LunarMonth.BHADRAPADA, Paksha.KRISHNA, 8, MuhurtaRule.SUNRISE, FestivalTradition.VAISHNAVA, true, false);
    public static final Festival GANESH_CHATURTHI = new Festival("ganesh_chaturthi", "Ganesh Chaturthi", LunarMonth.BHADRAPADA, Paksha.SHUKLA, 4, MuhurtaRule.SUNRISE);
    public static final Festival VIJAYADASHAMI = new Festival("vijayadashami", "Vijayadashami", LunarMonth.ASHVINA, Paksha.SHUKLA, 10, MuhurtaRule.SUNRISE);
    public static final Festival DIWALI = new Festival("diwali", "Diwali / Lakshmi Puja", LunarMonth.KARTIKA, Paksha.KRISHNA, 15, MuhurtaRule.PRADOSH);

    // ── Curated Kashmiri jantri additions (Samvat 2082) ──
    public static final Festival NAVREH = new Festival("navreh", "Navreh", LunarMonth.CHAITRA, Paksha.SHUKLA, 1, MuhurtaRule.SUNRISE, FestivalTradition.KASHMIRI, true, false);
    public static final Festival ZANG_TRAYI = new Festival("zang_trayi", "Zang Trayi", LunarMonth.CHAITRA, Paksha.SHUKLA, 3, MuhurtaRule.SUNRISE, FestivalTradition.KASHMIRI, true, false);
    public static final Festival DURGA_ASHTAMI = new Festival("durga_ashtami", "Durga Ashtami", LunarMonth.CHAITRA, Paksha.SHUKLA, 8, MuhurtaRule.SUNRISE);
    public static final Festival NIRJALA_EKADASHI = new Festival("nirjala_ekadashi", "Nirjala Ekadashi", LunarMonth.JYESHTHA, Paksha.SHUKLA, 11, MuhurtaRule.SUNRISE);
    public static final Festival ZYETH_ASHTAMI = new Festival("zyeth_ashtami", "Zyeth Ashtami", LunarMonth.JYESHTHA, Paksha.SHUKLA, 8, MuhurtaRule.SUNRISE, FestivalTradition.KASHMIRI, true, false);
    public static final Festival HAAR_ASHTAMI = new Festival("haar_ashtami", "Haar Ashtami", LunarMonth.ASHADHA, Paksha.SHUKLA, 8, MuhurtaRule.SUNRISE, FestivalTradition.KASHMIRI, true, false);
    public static final Festival SHARAD_NAVRATRI = new Festival("sharad_navratri", "Navratri (Sharad) Begins", LunarMonth.ASHVINA, Paksha.SHUKLA, 1, MuhurtaRule.SUNRISE);
    public static final Festival MAHA_NAVAMI = new Festival("maha_navami", "Maha Navami", LunarMonth.ASHVINA, Paksha.SHUKLA, 9, MuhurtaRule.SUNRISE);
    public static final Festival KARVA_CHAUTH = new Festival("karva_chauth", "Karva Chauth", LunarMonth.KARTIKA, Paksha.KRISHNA, 4, MuhurtaRule.SUNRISE);
    public static final Festival BHAI_DOOJ = new Festival("bhai_dooj", "Bhai Dooj", LunarMonth.KARTIKA, Paksha.SHUKLA, 2, MuhurtaRule.SUNRISE);
    public static final Festival KHICHDI_AMAVASYA = new Festival("khichdi_amavasya", "Khichdi Amavasya", LunarMonth.PAUSHA, Paksha.KRISHNA, 15, MuhurtaRule.SUNRISE);
    public static final Festival GAURI_TRITIYA = new Festival("gauri_tritiya", "Gauri Tritiya", LunarMonth.MAGHA, Paksha.SHUKLA, 3, MuhurtaRule.SUNRISE);
    public static final Festival KAAV_PUNIM = new Festival("kaav_punim", "Kaav Punim (Magh Purnima)", LunarMonth.MAGHA, Paksha.SHUKLA, 15, MuhurtaRule.SUNRISE, FestivalTradition.KASHMIRI, true, false);
    public static final Festival HURI_AUKDOH = new Festival("huri_aukdoh", "Huri Aukdoh", LunarMonth.PHALGUNA, Paksha.KRISHNA, 1, MuhurtaRule.SUNRISE, FestivalTradition.KASHMIRI, true, false);
    public static final Festival HURI_ASHTAMI = new Festival("huri_ashtami", "Huri Ashtami", LunarMonth.PHALGUNA, Paksha.KRISHNA, 8, MuhurtaRule.SUNRISE, FestivalTradition.KASHMIRI, true, false);
    public static final Festival TEIL_ASHTAMI = new Festival("teil_ashtami", "Teil Ashtami", LunarMonth.PHALGUNA, Paksha.SHUKLA, 8, MuhurtaRule.SUNRISE, FestivalTradition.KASHMIRI, true, false);
    public static final Festival HOLI = new Festival("holi", "Holi", LunarMonth.PHALGUNA, Paksha.SHUKLA, 15, MuhurtaRule.SUNRISE);
    public static final Festival SONTH = new Festival("sonth", "Sonth", LunarMonth.CHAITRA, Paksha.KRISHNA, 10, MuhurtaRule.SUNRISE, FestivalTradition.KASHMIRI, true, false);

    // ─── Recurring monthly tithis (month field ignored) ───
    public static final Festival MASIK_KRISHNA_ASHTAMI = new Festival("masik_krishna_ashtami", "Krishna Ashtami", LunarMonth.CHAITRA, Paksha.KRISHNA, 8, MuhurtaRule.SUNRISE, FestivalTradition.GENERAL, true, true);
    public static final Festival MASIK_SHUKLA_ASHTAMI = new Festival("masik_shukla_ashtami", "Shukla Ashtami", LunarMonth.CHAITRA, Paksha.SHUKLA, 8, MuhurtaRule.SUNRISE, FestivalTradition.GENERAL, true, true);
    public static final Festival MASIK_KRISHNA_EKADASHI = new Festival("masik_krishna_ekadashi", "Krishna Ekadashi", LunarMonth.CHAITRA, Paksha.KRISHNA, 11, MuhurtaRule.SUNRISE, FestivalTradition.GENERAL, true, true);
    public static final Festival MASIK_SHUKLA_EKADASHI = new Festival("masik_shukla_ekadashi", "Shukla Ekadashi", LunarMonth.CHAITRA, Paksha.SHUKLA, 11, MuhurtaRule.SUNRISE, FestivalTradition.GENERAL, true, true);
    public static final Festival MASIK_PURNIMA = new Festival("masik_purnima", "Purnima", LunarMonth.CHAITRA, Paksha.SHUKLA, 15, MuhurtaRule.SUNRISE, FestivalTradition.GENERAL, true, true);
    public static final Festival MASIK_AMAVASYA = new Festival("masik_amavasya", "Amavasya", LunarMonth.CHAITRA, Paksha.KRISHNA, 15, MuhurtaRule.SUNRISE, FestivalTradition.GENERAL, true, true);

    private static final List<Festival> ALL = List.of(
        MAHA_SHIVARATRI_KASHMIRI, MAHA_SHIVARATRI, RAM_NAVAMI, AKSHAYA_TRITIYA,
        GURU_PURNIMA, RAKSHA_BANDHAN, JANMASHTAMI_KASHMIRI, JANMASHTAMI_SMARTA, JANMASHTAMI_ISKCON,
        GANESH_CHATURTHI, VIJAYADASHAMI, DIWALI,
        NAVREH, ZANG_TRAYI, DURGA_ASHTAMI, NIRJALA_EKADASHI, ZYETH_ASHTAMI,
        HAAR_ASHTAMI, SHARAD_NAVRATRI, MAHA_NAVAMI, KARVA_CHAUTH, BHAI_DOOJ,
        KHICHDI_AMAVASYA, GAURI_TRITIYA, KAAV_PUNIM, HURI_AUKDOH, HURI_ASHTAMI,
        TEIL_ASHTAMI, HOLI, SONTH,
        MASIK_KRISHNA_ASHTAMI, MASIK_SHUKLA_ASHTAMI, MASIK_KRISHNA_EKADASHI, MASIK_SHUKLA_EKADASHI, MASIK_PURNIMA, MASIK_AMAVASYA
    );

    /** All built-in festival definitions. */
    public static List<Festival> all() { return ALL; }
}
