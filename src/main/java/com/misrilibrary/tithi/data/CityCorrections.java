package com.misrilibrary.tithi.data;

import com.misrilibrary.tithi.model.CityLocation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Loads and caches per-city correction tables from classpath JSON resources.
 * Format: {"tithi":{dayIndex:correctedTithi,...},"purnima":{from:to,...},"amavasya":{from:to,...}}
 *
 * <p><b>Internal use only</b> — this class is not part of the public API and may change without notice.
 */
public class CityCorrections {

    private static final Map<String, CityCorrections> cache = new HashMap<>();

    private final Map<Integer, Integer> tithiCorrections;
    private final Map<Integer, Integer> transitionMinutes;
    private final Map<Integer, Integer> purnimaCorrections;
    private final Map<Integer, Integer> amavasyaCorrections;

    private CityCorrections(Map<Integer, Integer> tithi, Map<Integer, Integer> transitions,
                            Map<Integer, Integer> purnima, Map<Integer, Integer> amavasya) {
        this.tithiCorrections = tithi;
        this.transitionMinutes = transitions;
        this.purnimaCorrections = purnima;
        this.amavasyaCorrections = amavasya;
    }

    public static CityCorrections forCity(String city) {
        // Resolve to the canonical registered city when known, else use the raw
        // input (truly-unknown -> no file -> empty maps). Keeps corrections and
        // coordinates consistent for any spelling of a supported city.
        String name = com.misrilibrary.tithi.City.resolveName(city);
        String key = (name != null ? name : city).toLowerCase().replaceAll("\\s+", "");
        return cache.computeIfAbsent(key, CityCorrections::load);
    }

    public Map<Integer, Integer> getTithiCorrections() { return Collections.unmodifiableMap(tithiCorrections); }
    public Map<Integer, Integer> getTransitionMinutes() { return Collections.unmodifiableMap(transitionMinutes); }
    public Map<Integer, Integer> getPurnimaCorrections() { return Collections.unmodifiableMap(purnimaCorrections); }
    public Map<Integer, Integer> getAmavasyaCorrections() { return Collections.unmodifiableMap(amavasyaCorrections); }

    public Integer getCorrectedTithi(int dayIndex) { return tithiCorrections.get(dayIndex); }
    public Integer getCorrectedPurnima(int dayIndex) { return purnimaCorrections.get(dayIndex); }
    public Integer getCorrectedAmavasya(int dayIndex) { return amavasyaCorrections.get(dayIndex); }

    private static CityCorrections load(String cityKey) {
        Map<Integer, Integer> tithi = new HashMap<>();
        Map<Integer, Integer> transitions = new HashMap<>();
        Map<Integer, Integer> purnima = new HashMap<>();
        Map<Integer, Integer> amavasya = new HashMap<>();

        try (InputStream is = CityCorrections.class.getResourceAsStream("/corrections/" + cityKey + ".json")) {
            if (is != null) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                tithi = parseSection(json, "tithi");
                transitions = parseSection(json, "transitions");
                purnima = parseSection(json, "purnima");
                amavasya = parseSection(json, "amavasya");
            }
        } catch (IOException e) {
            // No corrections available — use empty maps (Meeus fallback)
        }

        return new CityCorrections(tithi, transitions, purnima, amavasya);
    }

    /** Simple JSON map parser for {"key":value,...} sections. */
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
