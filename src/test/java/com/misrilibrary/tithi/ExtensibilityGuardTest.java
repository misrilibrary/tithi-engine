package com.misrilibrary.tithi;

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
    @DisplayName("Every City String constant must be in City.supported()")
    void allCityConstantsRegistered() {
        Set<String> supported = City.supported();

        List<String> missing = new ArrayList<>();
        for (Field f : City.class.getDeclaredFields()) {
            if (Modifier.isPublic(f.getModifiers())
                    && Modifier.isStatic(f.getModifiers())
                    && Modifier.isFinal(f.getModifiers())
                    && f.getType() == String.class) {
                try {
                    String value = (String) f.get(null);
                    if (!supported.contains(value)) {
                        missing.add(f.getName() + " = \"" + value + "\"");
                    }
                } catch (Exception e) {
                    missing.add(f.getName() + " (could not read)");
                }
            }
        }

        assertTrue(missing.isEmpty(),
                "City constants declared but NOT registered: " + missing + "\n\n"
                + "HOW TO FIX: In City.java, use reg() to declare your constant:\n"
                + "    public static final String YOUR_CITY = reg(\"Your City\", lat, lon, utcOffset);\n");
    }
}
