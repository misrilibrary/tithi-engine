package com.misrilibrary.tithi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Guards against extensibility mistakes — adding a city constant
 * without registering it properly.
 *
 * <p>If this test fails, follow the instructions in the failure message.
 * See also: README.md § Extensibility
 */
class ExtensibilityGuardTest {

    @Test
    @DisplayName("Every City constant must be in City.supported()")
    void allCityConstantsRegistered() {
        Set<String> supported = City.supported();

        List<String> missing = new ArrayList<>();
        for (Field f : City.class.getDeclaredFields()) {
            if (Modifier.isPublic(f.getModifiers())
                    && Modifier.isStatic(f.getModifiers())
                    && Modifier.isFinal(f.getModifiers())
                    && f.getType() == City.class) {
                try {
                    City value = (City) f.get(null);
                    if (!supported.contains(value.name())) {
                        missing.add(f.getName() + " = \"" + value.name() + "\"");
                    }
                } catch (Exception e) {
                    missing.add(f.getName() + " (could not read)");
                }
            }
        }

        assertTrue(missing.isEmpty(),
                "City constants declared but NOT registered: " + missing + "\n\n"
                + "HOW TO FIX: In City.java, use reg() to declare your constant:\n"
                + "    public static final City YOUR_CITY = reg(\"Your City\", lat, lon, utcOffset);\n");
    }
}
