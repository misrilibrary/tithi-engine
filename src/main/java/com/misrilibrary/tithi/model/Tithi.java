package com.misrilibrary.tithi.model;

import java.util.Objects;

/**
 * A tithi as a value: a lunar day identified by its {@link Paksha} and position.
 *
 * <p>Carries both encodings — the absolute {@link #number()} (1–30) and the
 * paksha-relative {@link #dayInPaksha()} (1–15) — and owns the paksha-dependent
 * {@link #name()} (the 15th is Purnima in shukla, Amavasya in krishna), so callers
 * never reimplement that rule.
 *
 * <p>Mirrors the Dart {@code tithi-engine-dart} {@code Tithi}.
 *
 * <pre>
 * Tithi.shukla(8);     // Shukla Ashtami
 * Tithi.krishna(11);   // Krishna Ekadashi
 * Tithi.ofNumber(23);  // == Tithi.krishna(8)
 * </pre>
 */
public final class Tithi {

    private static final String[] NAMES = {
        "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
        "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
        "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Purnima"
    };

    private final Paksha paksha;
    private final int dayInPaksha;

    private Tithi(Paksha paksha, int dayInPaksha) {
        this.paksha = paksha;
        this.dayInPaksha = dayInPaksha;
    }

    /** Shukla-paksha (waxing) tithi at {@code dayInPaksha} (1–15; 15 = Purnima). */
    public static Tithi shukla(int dayInPaksha) {
        check(dayInPaksha);
        return new Tithi(Paksha.SHUKLA, dayInPaksha);
    }

    /** Krishna-paksha (waning) tithi at {@code dayInPaksha} (1–15; 15 = Amavasya). */
    public static Tithi krishna(int dayInPaksha) {
        check(dayInPaksha);
        return new Tithi(Paksha.KRISHNA, dayInPaksha);
    }

    /** From an absolute tithi number (1–30). */
    public static Tithi ofNumber(int number) {
        if (number < 1 || number > 30) {
            throw new IllegalArgumentException("tithi number must be 1–30, was " + number);
        }
        return number <= 15
                ? new Tithi(Paksha.SHUKLA, number)
                : new Tithi(Paksha.KRISHNA, number - 15);
    }

    private static void check(int d) {
        if (d < 1 || d > 15) {
            throw new IllegalArgumentException("dayInPaksha must be 1–15, was " + d);
        }
    }

    /** Absolute tithi number, 1–30. */
    public int number() {
        return paksha == Paksha.SHUKLA ? dayInPaksha : dayInPaksha + 15;
    }

    /** Fortnight (waxing/waning). */
    public Paksha paksha() { return paksha; }

    /** Position within the paksha, 1–15. */
    public int dayInPaksha() { return dayInPaksha; }

    /** Paksha-aware name (15 → Purnima for shukla, Amavasya for krishna). */
    public String name() {
        int n = number();
        if (n == 30) return "Amavasya";
        return NAMES[(n - 1) % 15];
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Tithi)) return false;
        Tithi t = (Tithi) other;
        return paksha == t.paksha && dayInPaksha == t.dayInPaksha;
    }

    @Override
    public int hashCode() { return Objects.hash(paksha, dayInPaksha); }

    @Override
    public String toString() {
        return (paksha == Paksha.SHUKLA ? "Shukla" : "Krishna") + " " + name()
                + " (" + (paksha == Paksha.SHUKLA ? "S" : "K") + "." + dayInPaksha + ")";
    }
}
