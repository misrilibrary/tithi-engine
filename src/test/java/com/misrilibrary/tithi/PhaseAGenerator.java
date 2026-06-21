package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.CityLocation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.*;

/**
 * Phase A: regenerate Java per-city correction tables against the Dart Swiss-truth
 * dump, using the LIBRARY's (new) Astronomy engine.
 *
 * <p>A day gets a Java correction wherever {@code JavaMeeus(sunrise) != truth}; the
 * value is the Dart Swiss-truth. So the tables encode exactly where Java's Meeus
 * disagrees with Swiss — never a blind copy of Dart's correction SET (whose
 * membership is tied to Dart's Meeus, which may differ in the last ulp).
 *
 * <p>This is a tool, not part of the CI suite. Run explicitly:
 * <pre>
 *   ./gradlew test --tests com.misrilibrary.tithi.PhaseAGenerator -Dphasea.run=1
 *   ./gradlew test --tests com.misrilibrary.tithi.PhaseAGenerator -Dphasea.run=1 -Dphasea.city=seattle
 * </pre>
 * Reads:  build/phasea/{truth/<key>.bin, darttables/<key>.json, cities.json}
 * Writes: build/phasea/javatables/<key>.json  (library JSON format)
 */
class PhaseAGenerator {

    private static final LocalDate EPOCH = LocalDate.of(1900, 1, 1);
    private static final int TOTAL_DAYS = 73414;
    private static final Path BASE = Path.of("build/phasea");

    record CityRec(String key, String name, double lat, double lon, double utcOffset, String region) {}

    @org.junit.jupiter.api.Test
    void regenerateAndVerify() throws IOException {
        org.junit.jupiter.api.Assumptions.assumeTrue(
                "1".equals(System.getenv("PHASEA_RUN")),
                "PhaseAGenerator is a manual tool; set PHASEA_RUN=1");

        List<CityRec> cities = parseCities(Files.readString(BASE.resolve("cities.json")));
        String only = System.getenv("PHASEA_CITY");
        if (only != null) cities.removeIf(c -> !c.key.equals(only));

        Path outDir = BASE.resolve("javatables");
        Files.createDirectories(outDir);

        int totalCities = 0, citiesWithJavaOnly = 0;
        long verifyFailures = 0;
        long sumJavaCorr = 0, sumDartCorr = 0;
        long bothAgree = 0, javaOnlyDays = 0, dartOnlyDays = 0, valueDiffs = 0;
        long javaOnlyMissingTrans = 0;

        for (CityRec c : cities) {
            byte[] truth = Files.readAllBytes(BASE.resolve("truth").resolve(c.key + ".bin"));
            if (truth.length != TOTAL_DAYS) throw new IllegalStateException("bad truth length " + c.key);
            Map<String, Map<Integer, Integer>> dart = parseTables(
                    Files.readString(BASE.resolve("darttables").resolve(c.key + ".json")));
            Map<Integer, Integer> dartTithi = dart.get("tithi");
            Map<Integer, Integer> dartTrans = dart.get("transitions");

            CityLocation loc = new CityLocation(c.lat, c.lon, c.utcOffset);
            int[] javaMeeus = new int[TOTAL_DAYS];
            int[] tru = new int[TOTAL_DAYS];
            for (int d = 0; d < TOTAL_DAYS; d++) {
                tru[d] = truth[d] & 0xFF;
                LocalDate date = EPOCH.plusDays(d);
                LocalDateTime sr = Astronomy.computeSunrise(date, loc);
                javaMeeus[d] = Astronomy.tithiAt(sr);
            }

            // Tithi corrections = days where JavaMeeus disagrees with truth.
            TreeMap<Integer, Integer> tithiCorr = new TreeMap<>();
            for (int d = 0; d < TOTAL_DAYS; d++) {
                if (javaMeeus[d] != tru[d]) tithiCorr.put(d, tru[d]);
            }

            // Verify: Java engine + corrections reproduces truth EVERY day.
            for (int d = 0; d < TOTAL_DAYS; d++) {
                int finalT = tithiCorr.containsKey(d) ? tithiCorr.get(d) : javaMeeus[d];
                if (finalT != tru[d]) verifyFailures++;
            }

            // Boundary (amavasya/purnima) corrections: me=javaMeeus, swe=truth.
            TreeMap<Integer, Integer> amavasya = new TreeMap<>();
            TreeMap<Integer, Integer> purnima = new TreeMap<>();
            buildBoundaries(javaMeeus, tru, amavasya, purnima);

            // Transitions: reuse Dart's (Swiss-bisected) minute for shared corrected
            // days; collect any Java-only correction days lacking a Dart transition.
            TreeMap<Integer, Integer> transitions = new TreeMap<>();
            List<Integer> javaOnlyNoTrans = new ArrayList<>();
            for (int d : tithiCorr.keySet()) {
                Integer tm = dartTrans.get(d);
                if (tm != null) transitions.put(d, tm);
                else javaOnlyNoTrans.add(d);
            }

            // Divergence stats vs Dart's correction set.
            Set<Integer> allDays = new TreeSet<>();
            allDays.addAll(tithiCorr.keySet());
            allDays.addAll(dartTithi.keySet());
            for (int d : allDays) {
                boolean inJava = tithiCorr.containsKey(d);
                boolean inDart = dartTithi.containsKey(d);
                if (inJava && inDart) { bothAgree++; if (!tithiCorr.get(d).equals(dartTithi.get(d))) valueDiffs++; }
                else if (inJava) javaOnlyDays++;
                else dartOnlyDays++;
            }
            sumJavaCorr += tithiCorr.size();
            sumDartCorr += dartTithi.size();
            if (!javaOnlyNoTrans.isEmpty()) { citiesWithJavaOnly++; javaOnlyMissingTrans += javaOnlyNoTrans.size(); }

            writeJson(outDir.resolve(c.key + ".json"), tithiCorr, transitions, purnima, amavasya);
            totalCities++;
            if (only != null) {
                System.out.printf("%s: javaCorr=%d dartCorr=%d | sharedDays=%d javaOnly=%d dartOnly=%d valueDiffs=%d | javaOnlyNoTrans=%s%n",
                        c.key, tithiCorr.size(), dartTithi.size(), bothAgree, javaOnlyDays, dartOnlyDays, valueDiffs, javaOnlyNoTrans);
            }
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Cities processed: ").append(totalCities).append('\n');
        summary.append("Verify failures (must be 0): ").append(verifyFailures).append('\n');
        summary.append("Sum Java tithi corrections: ").append(sumJavaCorr).append('\n');
        summary.append("Sum Dart tithi corrections: ").append(sumDartCorr).append('\n');
        summary.append("shared days: ").append(bothAgree).append('\n');
        summary.append("value diffs on shared days (must be 0): ").append(valueDiffs).append('\n');
        summary.append("JAVA-ONLY days (Java needs, Dart lacks): ").append(javaOnlyDays).append('\n');
        summary.append("dart-only days (Dart needs, Java lacks): ").append(dartOnlyDays).append('\n');
        summary.append("cities with Java-only days missing a Dart transition: ").append(citiesWithJavaOnly)
                .append(" (").append(javaOnlyMissingTrans).append(" days)\n");
        try { Files.writeString(BASE.resolve("summary.txt"), summary.toString()); } catch (IOException ignore) {}
        System.out.println("════════════ Phase A regeneration summary ════════════");
        System.out.print(summary);

        org.junit.jupiter.api.Assertions.assertEquals(0, verifyFailures,
                "Java engine + regenerated corrections must reproduce truth exactly");
        org.junit.jupiter.api.Assertions.assertEquals(0, valueDiffs,
                "Where Java and Dart both correct a day, the corrected value must match");
    }

    /** Replicates the correction generator's amavasya/purnima boundary pairing. */
    private static void buildBoundaries(int[] me, int[] swe, TreeMap<Integer, Integer> amavasya,
                                        TreeMap<Integer, Integer> purnima) {
        List<Integer> sweAm = new ArrayList<>(), meAm = new ArrayList<>();
        List<Integer> swePu = new ArrayList<>(), mePu = new ArrayList<>();
        int pst = -1, pmt = -1;
        for (int d = 0; d < TOTAL_DAYS; d++) {
            int st = swe[d], mt = me[d];
            if (pst >= 0) {
                if (st == 30 && pst != 30) sweAm.add(d);
                else if (pst >= 28 && pst < 30 && st <= 2) sweAm.add(d - 1);
                if (st == 15 && pst != 15) swePu.add(d);
                else if (pst >= 13 && pst < 15 && st > 15 && st <= 17) swePu.add(d - 1);
            }
            if (pmt >= 0) {
                if (mt == 30 && pmt != 30) meAm.add(d);
                else if (pmt >= 28 && pmt < 30 && mt <= 2) meAm.add(d - 1);
                if (mt == 15 && pmt != 15) mePu.add(d);
                else if (pmt >= 13 && pmt < 15 && mt > 15 && mt <= 17) mePu.add(d - 1);
            }
            pst = st; pmt = mt;
        }
        pairDiffs(meAm, sweAm, amavasya);
        pairDiffs(mePu, swePu, purnima);
    }

    private static void pairDiffs(List<Integer> me, List<Integer> swe, TreeMap<Integer, Integer> out) {
        int len = Math.min(me.size(), swe.size());
        for (int i = 0; i < len; i++) {
            if (!me.get(i).equals(swe.get(i))) out.put(me.get(i), swe.get(i));
        }
    }

    private static void writeJson(Path path, Map<Integer, Integer> tithi, Map<Integer, Integer> trans,
                                  Map<Integer, Integer> purnima, Map<Integer, Integer> amavasya) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"tithi\":");
        appendMap(sb, tithi);
        sb.append(",\"transitions\":");
        appendMap(sb, trans);
        sb.append(",\"purnima\":");
        appendMap(sb, purnima);
        sb.append(",\"amavasya\":");
        appendMap(sb, amavasya);
        sb.append("}");
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }

    private static void appendMap(StringBuilder sb, Map<Integer, Integer> m) {
        sb.append("{");
        boolean first = true;
        for (var e : new TreeMap<>(m).entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(e.getKey()).append("\":").append(e.getValue());
            first = false;
        }
        sb.append("}");
    }

    private static List<CityRec> parseCities(String json) {
        List<CityRec> out = new ArrayList<>();
        Matcher m = Pattern.compile("\\{[^}]*}").matcher(json);
        while (m.find()) {
            String o = m.group();
            String key = grabStr(o, "key");
            String name = grabStr(o, "name");
            double lat = grabNum(o, "lat");
            double lon = grabNum(o, "lon");
            double off = grabNum(o, "utcOffset");
            Matcher rm = Pattern.compile("\"region\":(null|\"([^\"]*)\")").matcher(o);
            String region = null;
            if (rm.find() && rm.group(2) != null) region = rm.group(2);
            out.add(new CityRec(key, name, lat, lon, off, region));
        }
        return out;
    }

    private static String grabStr(String o, String field) {
        Matcher m = Pattern.compile("\"" + field + "\":\"([^\"]*)\"").matcher(o);
        if (!m.find()) throw new IllegalStateException("missing " + field + " in " + o);
        return m.group(1);
    }

    private static double grabNum(String o, String field) {
        Matcher m = Pattern.compile("\"" + field + "\":(-?[0-9.]+)").matcher(o);
        if (!m.find()) throw new IllegalStateException("missing " + field + " in " + o);
        return Double.parseDouble(m.group(1));
    }

    /** Parse {"tithi":{...},"transitions":{...},"purnima":{...},"amavasya":{...}} */
    private static Map<String, Map<Integer, Integer>> parseTables(String json) {
        Map<String, Map<Integer, Integer>> out = new HashMap<>();
        for (String section : new String[]{"tithi", "transitions", "purnima", "amavasya"}) {
            out.put(section, parseSection(json, section));
        }
        return out;
    }

    private static Map<Integer, Integer> parseSection(String json, String section) {
        Map<Integer, Integer> map = new HashMap<>();
        String marker = "\"" + section + "\":{";
        int start = json.indexOf(marker);
        if (start < 0) return map;
        start += marker.length();
        int end = json.indexOf("}", start);
        if (end < 0) return map;
        String content = json.substring(start, end).trim();
        if (content.isEmpty()) return map;
        for (String pair : content.split(",")) {
            String[] kv = pair.split(":");
            if (kv.length == 2) {
                map.put(Integer.parseInt(kv[0].trim().replace("\"", "")),
                        Integer.parseInt(kv[1].trim()));
            }
        }
        return map;
    }
}
