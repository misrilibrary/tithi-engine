package com.misrilibrary.tithi;

import com.misrilibrary.tithi.data.Cities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Guards against extensibility mistakes — adding a festival or city constant
 * without registering it properly.
 *
 * <p>If this test fails, follow the instructions in the failure message.
 * See also: README.md § Extensibility
 */
class ExtensibilityGuardTest {

    @Test
    @DisplayName("Every Festival constant must appear in Festival.all()")
    void allFestivalConstantsRegistered() {
        // Find all public static final Festival fields on the Festival class
        List<String> declaredConstants = new ArrayList<>();
        for (Field f : Festival.class.getDeclaredFields()) {
            if (Modifier.isPublic(f.getModifiers())
                    && Modifier.isStatic(f.getModifiers())
                    && Modifier.isFinal(f.getModifiers())
                    && f.getType() == Festival.class) {
                declaredConstants.add(f.getName());
            }
        }

        Set<String> registeredIds = Festival.all().stream()
                .map(fest -> fest.id)
                .collect(Collectors.toSet());

        List<String> missing = new ArrayList<>();
        for (String fieldName : declaredConstants) {
            try {
                Festival fest = (Festival) Festival.class.getField(fieldName).get(null);
                if (!registeredIds.contains(fest.id)) {
                    missing.add(fieldName + " (id=\"" + fest.id + "\")");
                }
            } catch (Exception e) {
                missing.add(fieldName + " (could not read)");
            }
        }

        assertTrue(missing.isEmpty(),
                "Festival constants declared but NOT in Festival.all(): " + missing + "\n\n"
                + "HOW TO FIX: Open Festival.java and add the missing constant(s) to the ALL list:\n"
                + "    private static final List<Festival> ALL = List.of(\n"
                + "        ...,\n"
                + "        YOUR_NEW_CONSTANT   // ← add here\n"
                + "    );\n");
    }

    @Test
    @DisplayName("Every City constant must exist in Cities registry")
    void allCityConstantsRegistered() {
        Set<String> supportedCities = Cities.getSupportedCities();

        List<String> missing = new ArrayList<>();
        for (Field f : City.class.getDeclaredFields()) {
            if (Modifier.isPublic(f.getModifiers())
                    && Modifier.isStatic(f.getModifiers())
                    && Modifier.isFinal(f.getModifiers())
                    && f.getType() == String.class) {
                try {
                    String value = (String) f.get(null);
                    if (!supportedCities.contains(value)) {
                        missing.add(f.getName() + " = \"" + value + "\"");
                    }
                } catch (Exception e) {
                    missing.add(f.getName() + " (could not read)");
                }
            }
        }

        assertTrue(missing.isEmpty(),
                "City constants declared but NOT in Cities registry: " + missing + "\n\n"
                + "HOW TO FIX: Open data/Cities.java and add a CITIES.put(...) entry:\n"
                + "    CITIES.put(\"YourCity\", new CityLocation(lat, lon, utcOffset));\n");
    }

    @Test
    @DisplayName("Every city in Cities registry should have a City constant")
    void allRegisteredCitiesHaveConstants() {
        Set<String> constantValues = new HashSet<>();
        for (Field f : City.class.getDeclaredFields()) {
            if (Modifier.isPublic(f.getModifiers())
                    && Modifier.isStatic(f.getModifiers())
                    && Modifier.isFinal(f.getModifiers())
                    && f.getType() == String.class) {
                try {
                    constantValues.add((String) f.get(null));
                } catch (Exception ignored) {}
            }
        }

        Set<String> supportedCities = Cities.getSupportedCities();
        List<String> missing = supportedCities.stream()
                .filter(city -> !constantValues.contains(city))
                .sorted()
                .collect(Collectors.toList());

        assertTrue(missing.isEmpty(),
                "Cities in registry but missing a City.java constant: " + missing + "\n\n"
                + "HOW TO FIX: Open City.java and add a constant:\n"
                + "    public static final String YOUR_CITY = \"Your City\";\n");
    }
}
