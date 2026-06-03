package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.CityCorrections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates the structural correctness of all per-city JSON correction files.
 * Catches: malformed JSON, invalid tithi values, out-of-range day indices,
 * missing required sections, and inconsistencies.
 */
class CorrectionJsonTest {

    // 1900-01-01 to 2100-12-31 in days since epoch
    private static final int MIN_DAY_INDEX = 0;
    private static final int MAX_DAY_INDEX = 73413; // ~200 years

    @Test @DisplayName("Every JSON file in corrections/ folder parses cleanly")
    void allJsonFilesInFolderParseClean() throws Exception {
        // Scan the actual resources/corrections/ directory
        java.net.URL dir = getClass().getResource("/corrections");
        assertNotNull(dir, "corrections/ folder not found on classpath");
        java.io.File folder = new java.io.File(dir.toURI());
        java.io.File[] files = folder.listFiles((d, name) -> name.endsWith(".json"));
        assertNotNull(files);
        assertTrue(files.length >= 100, "Expected 100+ JSON files, found " + files.length);

        List<String> failures = new ArrayList<>();
        for (java.io.File file : files) {
            String cityKey = file.getName().replace(".json", "");
            try {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                // Validate it's parseable as our format
                assertTrue(content.startsWith("{"), cityKey + ": not valid JSON object");
                assertTrue(content.contains("\"tithi\""), cityKey + ": missing tithi section");
                // Parse through CityCorrections
                CityCorrections corr = CityCorrections.forCity(cityKey);
                assertNotNull(corr, cityKey + ": returned null");
            } catch (Exception e) {
                failures.add(cityKey + ": " + e.getClass().getSimpleName() + " " + e.getMessage());
            }
        }
        assertTrue(failures.isEmpty(),
                failures.size() + " JSON files failed to parse:\n" + String.join("\n", failures));
    }

    @Test @DisplayName("All tithi correction values are 1-30")
    void tithiValuesInRange() {
        List<String> violations = new ArrayList<>();
        for (String city : City.supported()) {
            CityCorrections corr = CityCorrections.forCity(city);
            for (Map.Entry<Integer, Integer> e : corr.getTithiCorrections().entrySet()) {
                if (e.getValue() < 1 || e.getValue() > 30) {
                    violations.add(city + " dayIndex=" + e.getKey() + " value=" + e.getValue());
                }
            }
        }
        assertTrue(violations.isEmpty(), "Tithi values out of [1,30]: " + violations);
    }

    @Test @DisplayName("All day indices are in valid epoch range (1900-2100)")
    void dayIndicesInRange() {
        List<String> violations = new ArrayList<>();
        for (String city : City.supported()) {
            CityCorrections corr = CityCorrections.forCity(city);
            for (int idx : corr.getTithiCorrections().keySet()) {
                if (idx < MIN_DAY_INDEX || idx > MAX_DAY_INDEX) {
                    violations.add(city + " tithi dayIndex=" + idx);
                }
            }
        }
        assertTrue(violations.isEmpty(), "Day indices out of range: " + violations);
    }

    @Test @DisplayName("Purnima corrections point to valid day indices")
    void purnimaCorrectionsValid() {
        List<String> violations = new ArrayList<>();
        for (String city : City.supported()) {
            CityCorrections corr = CityCorrections.forCity(city);
            for (Map.Entry<Integer, Integer> e : corr.getPurnimaCorrections().entrySet()) {
                int from = e.getKey(), to = e.getValue();
                if (from < MIN_DAY_INDEX || from > MAX_DAY_INDEX) {
                    violations.add(city + " purnima from=" + from);
                }
                if (to < MIN_DAY_INDEX || to > MAX_DAY_INDEX) {
                    violations.add(city + " purnima to=" + to);
                }
                // Correction should be within ±2 days of original
                if (Math.abs(to - from) > 2) {
                    violations.add(city + " purnima shift too large: " + from + "→" + to);
                }
            }
        }
        assertTrue(violations.isEmpty(), "Purnima violations: " + violations);
    }

    @Test @DisplayName("Amavasya corrections point to valid day indices")
    void amavasyaCorrectionsValid() {
        List<String> violations = new ArrayList<>();
        for (String city : City.supported()) {
            CityCorrections corr = CityCorrections.forCity(city);
            for (Map.Entry<Integer, Integer> e : corr.getAmavasyaCorrections().entrySet()) {
                int from = e.getKey(), to = e.getValue();
                if (from < MIN_DAY_INDEX || from > MAX_DAY_INDEX) {
                    violations.add(city + " amavasya from=" + from);
                }
                if (to < MIN_DAY_INDEX || to > MAX_DAY_INDEX) {
                    violations.add(city + " amavasya to=" + to);
                }
                if (Math.abs(to - from) > 2) {
                    violations.add(city + " amavasya shift too large: " + from + "→" + to);
                }
            }
        }
        assertTrue(violations.isEmpty(), "Amavasya violations: " + violations);
    }

    @Test @DisplayName("Transitions map has same keys as tithi corrections")
    void transitionsMatchTithi() {
        List<String> violations = new ArrayList<>();
        for (String city : City.supported()) {
            CityCorrections corr = CityCorrections.forCity(city);
            Set<Integer> tithiKeys = corr.getTithiCorrections().keySet();
            Set<Integer> transKeys = corr.getTransitionMinutes().keySet();
            if (!tithiKeys.equals(transKeys)) {
                int missingTrans = 0, extraTrans = 0;
                for (int k : tithiKeys) if (!transKeys.contains(k)) missingTrans++;
                for (int k : transKeys) if (!tithiKeys.contains(k)) extraTrans++;
                if (missingTrans > 0 || extraTrans > 0) {
                    violations.add(city + " missingTransitions=" + missingTrans + " extraTransitions=" + extraTrans);
                }
            }
        }
        assertTrue(violations.isEmpty(), "Transition/tithi key mismatch: " + violations);
    }

    @Test @DisplayName("JSON files exist on classpath for all supported cities")
    void jsonFilesExist() {
        List<String> missing = new ArrayList<>();
        for (String city : City.supported()) {
            String key = city.toLowerCase().replaceAll("\\s+", "");
            InputStream is = getClass().getResourceAsStream("/corrections/" + key + ".json");
            if (is == null) {
                missing.add(city + " (expected: /corrections/" + key + ".json)");
            } else {
                try { is.close(); } catch (IOException ignored) {}
            }
        }
        assertTrue(missing.isEmpty(), "Missing JSON files: " + missing);
    }
}
