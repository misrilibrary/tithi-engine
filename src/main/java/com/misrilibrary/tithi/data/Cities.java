package com.misrilibrary.tithi.data;

import com.misrilibrary.tithi.model.CityLocation;
import java.util.*;

public class Cities {

    public static final String DEFAULT_CITY = "Ujjain";

    private static final Map<String, CityLocation> CITIES = new LinkedHashMap<>();

    static {
        // India
        CITIES.put("Ujjain", new CityLocation(23.2, 75.8, 5.5));
        CITIES.put("Delhi", new CityLocation(28.6, 77.2, 5.5));
        CITIES.put("Mumbai", new CityLocation(19.1, 72.9, 5.5));
        CITIES.put("Chennai", new CityLocation(13.1, 80.3, 5.5));
        CITIES.put("Kolkata", new CityLocation(22.6, 88.4, 5.5));
        CITIES.put("Bangalore", new CityLocation(12.97, 77.59, 5.5));
        CITIES.put("Hyderabad", new CityLocation(17.4, 78.5, 5.5));
        CITIES.put("Ahmedabad", new CityLocation(23.0, 72.6, 5.5));
        CITIES.put("Pune", new CityLocation(18.5, 73.9, 5.5));
        CITIES.put("Jaipur", new CityLocation(26.9, 75.8, 5.5));
        CITIES.put("Lucknow", new CityLocation(26.8, 81.0, 5.5));
        CITIES.put("Srinagar", new CityLocation(34.1, 74.8, 5.5));
        CITIES.put("Jammu", new CityLocation(32.7, 74.9, 5.5));
        CITIES.put("Varanasi", new CityLocation(25.3, 83.0, 5.5));
        CITIES.put("Chandigarh", new CityLocation(30.7, 76.8, 5.5));
        CITIES.put("Indore", new CityLocation(22.7, 75.9, 5.5));
        CITIES.put("Bhopal", new CityLocation(23.3, 77.4, 5.5));
        CITIES.put("Nagpur", new CityLocation(21.1, 79.1, 5.5));
        CITIES.put("Patna", new CityLocation(25.6, 85.1, 5.5));
        CITIES.put("Amritsar", new CityLocation(31.6, 74.9, 5.5));
        // US
        CITIES.put("Seattle", new CityLocation(47.6, -122.3, -8.0));
        CITIES.put("San Francisco", new CityLocation(37.8, -122.4, -8.0));
        CITIES.put("Fremont", new CityLocation(37.5, -122.0, -8.0));
        CITIES.put("New York", new CityLocation(40.7, -74.0, -5.0));
        CITIES.put("Chicago", new CityLocation(41.9, -87.6, -6.0));
        CITIES.put("Dallas", new CityLocation(32.8, -96.8, -6.0));
        CITIES.put("Boston", new CityLocation(42.4, -71.1, -5.0));
        // Europe
        CITIES.put("London", new CityLocation(51.5, -0.1, 0.0));
        CITIES.put("Berlin", new CityLocation(52.5, 13.4, 1.0));
        CITIES.put("Paris", new CityLocation(48.9, 2.3, 1.0));
        // Asia-Pacific
        CITIES.put("Tokyo", new CityLocation(35.7, 139.7, 9.0));
        CITIES.put("Sydney", new CityLocation(-33.9, 151.2, 10.0));
        CITIES.put("Singapore", new CityLocation(1.3, 103.8, 8.0));
        CITIES.put("Dubai", new CityLocation(25.2, 55.3, 4.0));
    }

    public static CityLocation getLocation(String city) {
        CityLocation loc = CITIES.get(city);
        return loc != null ? loc : CITIES.get(DEFAULT_CITY);
    }

    public static Set<String> getSupportedCities() {
        return Collections.unmodifiableSet(CITIES.keySet());
    }
}
