package com.misrilibrary.tithi.data;

import com.misrilibrary.tithi.model.CityLocation;
import java.util.*;

public class Cities {

    public static final String DEFAULT_CITY = "Ujjain";

    private static final Map<String, CityLocation> CITIES = new LinkedHashMap<>();

    static {
        CITIES.put("Delhi", new CityLocation(28.6, 77.2, 5.5));
        CITIES.put("Mumbai", new CityLocation(19.1, 72.9, 5.5));
        CITIES.put("Kolkata", new CityLocation(22.6, 88.4, 5.5));
        CITIES.put("Chennai", new CityLocation(13.1, 80.3, 5.5));
        CITIES.put("Srinagar", new CityLocation(34.1, 74.8, 5.5));
        CITIES.put("Bangalore", new CityLocation(12.9, 77.6, 5.5));
        CITIES.put("Hyderabad", new CityLocation(17.4, 78.5, 5.5));
        CITIES.put("Pune", new CityLocation(18.5, 73.9, 5.5));
        CITIES.put("Ahmedabad", new CityLocation(23.0, 72.6, 5.5));
        CITIES.put("Jaipur", new CityLocation(26.9, 75.8, 5.5));
        CITIES.put("Lucknow", new CityLocation(26.8, 81.0, 5.5));
        CITIES.put("Chandigarh", new CityLocation(30.7, 76.8, 5.5));
        CITIES.put("Jammu", new CityLocation(32.7, 74.9, 5.5));
        CITIES.put("Indore", new CityLocation(22.7, 75.9, 5.5));
        CITIES.put("Ujjain", new CityLocation(23.2, 75.8, 5.5));
        CITIES.put("Bhopal", new CityLocation(23.3, 77.4, 5.5));
        CITIES.put("Nagpur", new CityLocation(21.1, 79.1, 5.5));
        CITIES.put("Patna", new CityLocation(25.6, 85.1, 5.5));
        CITIES.put("Kochi", new CityLocation(10.0, 76.3, 5.5));
        CITIES.put("Guwahati", new CityLocation(26.1, 91.7, 5.5));
        CITIES.put("Varanasi", new CityLocation(25.3, 83.0, 5.5));
        CITIES.put("Amritsar", new CityLocation(31.6, 74.9, 5.5));
        CITIES.put("Dehradun", new CityLocation(30.3, 78.0, 5.5));
        CITIES.put("Thiruvananthapuram", new CityLocation(8.5, 76.9, 5.5));
        CITIES.put("Coimbatore", new CityLocation(11.0, 76.9, 5.5));
        CITIES.put("Visakhapatnam", new CityLocation(17.7, 83.3, 5.5));
        CITIES.put("Mangalore", new CityLocation(12.9, 74.9, 5.5));
        CITIES.put("Mysore", new CityLocation(12.3, 76.7, 5.5));
        CITIES.put("Noida", new CityLocation(28.6, 77.3, 5.5));
        CITIES.put("Gurgaon", new CityLocation(28.5, 77.0, 5.5));
        CITIES.put("Seattle", new CityLocation(47.6, -122.3, -8.0));
        CITIES.put("Kirkland", new CityLocation(47.7, -122.2, -8.0));
        CITIES.put("San Francisco", new CityLocation(37.8, -122.4, -8.0));
        CITIES.put("Fremont", new CityLocation(37.5, -122.0, -8.0));
        CITIES.put("San Jose", new CityLocation(37.3, -121.9, -8.0));
        CITIES.put("Los Angeles", new CityLocation(34.1, -118.2, -8.0));
        CITIES.put("Dallas", new CityLocation(32.8, -96.8, -6.0));
        CITIES.put("Austin", new CityLocation(30.3, -97.7, -6.0));
        CITIES.put("Houston", new CityLocation(29.8, -95.4, -6.0));
        CITIES.put("Boston", new CityLocation(42.4, -71.1, -5.0));
        CITIES.put("New York", new CityLocation(40.7, -74.0, -5.0));
        CITIES.put("Chicago", new CityLocation(41.9, -87.6, -6.0));
        CITIES.put("Atlanta", new CityLocation(33.7, -84.4, -5.0));
        CITIES.put("Orlando", new CityLocation(28.5, -81.4, -5.0));
        CITIES.put("Denver", new CityLocation(39.7, -105.0, -7.0));
        CITIES.put("Phoenix", new CityLocation(33.4, -112.1, -7.0));
        CITIES.put("Washington DC", new CityLocation(38.9, -77.0, -5.0));
        CITIES.put("Miami", new CityLocation(25.8, -80.2, -5.0));
        CITIES.put("Portland", new CityLocation(45.5, -122.7, -8.0));
        CITIES.put("Minneapolis", new CityLocation(44.9, -93.3, -6.0));
        CITIES.put("Detroit", new CityLocation(42.3, -83.0, -5.0));
        CITIES.put("Philadelphia", new CityLocation(40.0, -75.2, -5.0));
        CITIES.put("San Diego", new CityLocation(32.7, -117.2, -8.0));
        CITIES.put("Raleigh", new CityLocation(35.8, -78.6, -5.0));
        CITIES.put("Toronto", new CityLocation(43.7, -79.4, -5.0));
        CITIES.put("Vancouver", new CityLocation(49.3, -123.1, -8.0));
        CITIES.put("Montreal", new CityLocation(45.5, -73.6, -5.0));
        CITIES.put("Calgary", new CityLocation(51.0, -114.1, -7.0));
        CITIES.put("Ottawa", new CityLocation(45.4, -75.7, -5.0));
        CITIES.put("London", new CityLocation(51.5, -0.1, 0.0));
        CITIES.put("Berlin", new CityLocation(52.5, 13.4, 1.0));
        CITIES.put("Amsterdam", new CityLocation(52.4, 4.9, 1.0));
        CITIES.put("Paris", new CityLocation(48.9, 2.3, 1.0));
        CITIES.put("Dublin", new CityLocation(53.3, -6.3, 0.0));
        CITIES.put("Munich", new CityLocation(48.1, 11.6, 1.0));
        CITIES.put("Zurich", new CityLocation(47.4, 8.5, 1.0));
        CITIES.put("Stockholm", new CityLocation(59.3, 18.1, 1.0));
        CITIES.put("Helsinki", new CityLocation(60.2, 24.9, 2.0));
        CITIES.put("Warsaw", new CityLocation(52.2, 21.0, 1.0));
        CITIES.put("Vienna", new CityLocation(48.2, 16.4, 1.0));
        CITIES.put("Prague", new CityLocation(50.1, 14.4, 1.0));
        CITIES.put("Milan", new CityLocation(45.5, 9.2, 1.0));
        CITIES.put("Barcelona", new CityLocation(41.4, 2.2, 1.0));
        CITIES.put("Lisbon", new CityLocation(38.7, -9.1, 0.0));
        CITIES.put("Dubai", new CityLocation(25.2, 55.3, 4.0));
        CITIES.put("Muscat", new CityLocation(23.6, 58.5, 4.0));
        CITIES.put("Doha", new CityLocation(25.3, 51.5, 3.0));
        CITIES.put("Riyadh", new CityLocation(24.7, 46.7, 3.0));
        CITIES.put("Kuwait City", new CityLocation(29.4, 47.9, 3.0));
        CITIES.put("Bahrain", new CityLocation(26.2, 50.6, 3.0));
        CITIES.put("Tel Aviv", new CityLocation(32.1, 34.8, 2.0));
        CITIES.put("Singapore", new CityLocation(1.4, 103.8, 8.0));
        CITIES.put("Tokyo", new CityLocation(35.7, 139.7, 9.0));
        CITIES.put("Hong Kong", new CityLocation(22.3, 114.2, 8.0));
        CITIES.put("Kuala Lumpur", new CityLocation(3.1, 101.7, 8.0));
        CITIES.put("Bangkok", new CityLocation(13.8, 100.5, 7.0));
        CITIES.put("Jakarta", new CityLocation(-6.2, 106.8, 7.0));
        CITIES.put("Seoul", new CityLocation(37.6, 127.0, 9.0));
        CITIES.put("Taipei", new CityLocation(25.0, 121.5, 8.0));
        CITIES.put("Manila", new CityLocation(14.6, 121.0, 8.0));
        CITIES.put("Ho Chi Minh City", new CityLocation(10.8, 106.6, 7.0));
        CITIES.put("Kathmandu", new CityLocation(27.7, 85.3, 5.75));
        CITIES.put("Colombo", new CityLocation(6.9, 79.9, 5.5));
        CITIES.put("Dhaka", new CityLocation(23.8, 90.4, 6.0));
        CITIES.put("Sydney", new CityLocation(-33.9, 151.2, 10.0));
        CITIES.put("Melbourne", new CityLocation(-37.8, 145.0, 10.0));
        CITIES.put("Brisbane", new CityLocation(-27.5, 153.0, 10.0));
        CITIES.put("Perth", new CityLocation(-31.9, 115.9, 8.0));
        CITIES.put("Auckland", new CityLocation(-36.8, 174.8, 12.0));
        CITIES.put("Nairobi", new CityLocation(-1.3, 36.8, 3.0));
        CITIES.put("Cape Town", new CityLocation(-33.9, 18.4, 2.0));
        CITIES.put("Lagos", new CityLocation(6.5, 3.4, 1.0));
        CITIES.put("Cairo", new CityLocation(30.0, 31.2, 2.0));
        CITIES.put("Johannesburg", new CityLocation(-26.2, 28.0, 2.0));
        CITIES.put("São Paulo", new CityLocation(-23.5, -46.6, -3.0));
        CITIES.put("Buenos Aires", new CityLocation(-34.6, -58.4, -3.0));
        CITIES.put("Bogotá", new CityLocation(4.7, -74.1, -5.0));
        CITIES.put("Lima", new CityLocation(-12.0, -77.0, -5.0));
        CITIES.put("Santiago", new CityLocation(-33.4, -70.6, -4.0));
    }

    public static CityLocation getLocation(String city) {
        CityLocation loc = CITIES.get(city);
        return loc != null ? loc : CITIES.get(DEFAULT_CITY);
    }

    public static Set<String> getSupportedCities() {
        return Collections.unmodifiableSet(CITIES.keySet());
    }
}
