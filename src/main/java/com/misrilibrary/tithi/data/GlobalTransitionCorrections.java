package com.misrilibrary.tithi.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Global (city-independent) Swiss tithi-transition corrections.
 *
 * <p>Mirrors the Dart {@code transitions/global_transition_corrections.g.dart}
 * plus the {@code _absTransitions}/{@code _correctTransition} logic in
 * {@code tithi_calculator.dart}. The tithi is geocentric, so a single table of
 * Swiss transition instants applies to every city.
 *
 * <p>The delta list is transcoded verbatim from the Dart data and shipped as the
 * classpath resource {@code /corrections/global_transitions.csv} (a plain array
 * literal of this size exceeds the JVM 64KB method-bytecode limit).
 *
 * <p><b>Internal use only</b> — not part of the public API.
 */
public final class GlobalTransitionCorrections {

    private GlobalTransitionCorrections() {}

    private static final LocalDate EPOCH = LocalDate.of(1900, 1, 1);

    /** Delta-encoded Swiss transition instants (UTC minutes since 1900-01-01):
     *  first int = absolute instant, each subsequent int = gap to the next. */
    private static volatile int[] deltasCache;

    private static int[] deltas() {
        int[] cached = deltasCache;
        if (cached != null) return cached;
        int[] d = new int[0];
        try (InputStream is = GlobalTransitionCorrections.class
                .getResourceAsStream("/corrections/global_transitions.csv")) {
            if (is != null) {
                String s = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                if (!s.isEmpty()) {
                    String[] parts = s.split(",");
                    d = new int[parts.length];
                    for (int i = 0; i < parts.length; i++) d[i] = Integer.parseInt(parts[i].trim());
                }
            }
        } catch (IOException e) {
            // No data on classpath — empty (Meeus fallback).
        }
        return deltasCache = d;
    }

    /** Lazily prefix-summed absolute Swiss transition instants (UTC minutes since 1900). */
    private static volatile int[] absTransCache;

    private static int[] absTransitions() {
        int[] cached = absTransCache;
        if (cached != null) return cached;
        int[] src = deltas();
        int[] abs = new int[src.length];
        int acc = 0;
        for (int i = 0; i < src.length; i++) {
            acc += src[i];
            abs[i] = acc;
        }
        return absTransCache = abs;
    }

    /**
     * Override a Meeus transition instant with the Swiss-exact one from the
     * global correction list when one exists within &plusmn;60 min. Returns the
     * Swiss instant (minute resolution) or the Meeus instant unchanged.
     */
    public static Instant correctTransition(Instant meeus) {
        int[] list = absTransitions();
        if (list.length == 0) return meeus;
        long meeusMin = Duration.between(EPOCH.atStartOfDay().toInstant(ZoneOffset.UTC), meeus).toMinutes();
        int lo = 0, hi = list.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            if (list[mid] < meeusMin) lo = mid + 1; else hi = mid - 1;
        }
        int best = -1;
        long bestDelta = 61;
        for (int i : new int[]{lo - 1, lo}) {
            if (i < 0 || i >= list.length) continue;
            long d = Math.abs(list[i] - meeusMin);
            if (d <= 60 && d < bestDelta) { bestDelta = d; best = i; }
        }
        return best >= 0
                ? EPOCH.atStartOfDay().toInstant(ZoneOffset.UTC).plus(Duration.ofMinutes(list[best]))
                : meeus;
    }
}
