package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.CityLocation;
import java.util.*;

/**
 * City registry — name constants + coordinate data in one place.
 * Use constants (e.g. City.SEATTLE) for type-safe autocomplete,
 * or pass any registered string directly.
 */
public final class City {
    private City() {}

    private static final Map<String, CityLocation> LOCATIONS = new LinkedHashMap<>();

    // ─── India ───
    public static final String DELHI = reg("Delhi", 28.6, 77.2, 5.5);
    public static final String MUMBAI = reg("Mumbai", 19.1, 72.9, 5.5);
    public static final String KOLKATA = reg("Kolkata", 22.6, 88.4, 5.5);
    public static final String CHENNAI = reg("Chennai", 13.1, 80.3, 5.5);
    public static final String SRINAGAR = reg("Srinagar", 34.1, 74.8, 5.5);
    public static final String BANGALORE = reg("Bangalore", 12.9, 77.6, 5.5);
    public static final String HYDERABAD = reg("Hyderabad", 17.4, 78.5, 5.5);
    public static final String PUNE = reg("Pune", 18.5, 73.9, 5.5);
    public static final String AHMEDABAD = reg("Ahmedabad", 23.0, 72.6, 5.5);
    public static final String JAIPUR = reg("Jaipur", 26.9, 75.8, 5.5);
    public static final String LUCKNOW = reg("Lucknow", 26.8, 81.0, 5.5);
    public static final String CHANDIGARH = reg("Chandigarh", 30.7, 76.8, 5.5);
    public static final String JAMMU = reg("Jammu", 32.7, 74.9, 5.5);
    public static final String INDORE = reg("Indore", 22.7, 75.9, 5.5);
    public static final String UJJAIN = reg("Ujjain", 23.2, 75.8, 5.5);
    public static final String BHOPAL = reg("Bhopal", 23.3, 77.4, 5.5);
    public static final String NAGPUR = reg("Nagpur", 21.1, 79.1, 5.5);
    public static final String PATNA = reg("Patna", 25.6, 85.1, 5.5);
    public static final String KOCHI = reg("Kochi", 10.0, 76.3, 5.5);
    public static final String GUWAHATI = reg("Guwahati", 26.1, 91.7, 5.5);
    public static final String VARANASI = reg("Varanasi", 25.3, 83.0, 5.5);
    public static final String AMRITSAR = reg("Amritsar", 31.6, 74.9, 5.5);
    public static final String DEHRADUN = reg("Dehradun", 30.3, 78.0, 5.5);
    public static final String THIRUVANANTHAPURAM = reg("Thiruvananthapuram", 8.5, 76.9, 5.5);
    public static final String COIMBATORE = reg("Coimbatore", 11.0, 76.9, 5.5);
    public static final String VISAKHAPATNAM = reg("Visakhapatnam", 17.7, 83.3, 5.5);
    public static final String MANGALORE = reg("Mangalore", 12.9, 74.9, 5.5);
    public static final String MYSORE = reg("Mysore", 12.3, 76.7, 5.5);
    public static final String NOIDA = reg("Noida", 28.6, 77.3, 5.5);
    public static final String GURGAON = reg("Gurgaon", 28.5, 77.0, 5.5);
    public static final String AGRA = reg("Agra", 27.2, 78.0, 5.5);
    public static final String ALLAHABAD = reg("Allahabad", 25.4, 81.8, 5.5);
    public static final String AURANGABAD = reg("Aurangabad", 19.9, 75.3, 5.5);
    public static final String BHUBANESWAR = reg("Bhubaneswar", 20.3, 85.8, 5.5);
    public static final String FARIDABAD = reg("Faridabad", 28.4, 77.3, 5.5);
    public static final String GHAZIABAD = reg("Ghaziabad", 28.7, 77.4, 5.5);
    public static final String GORAKHPUR = reg("Gorakhpur", 26.8, 83.4, 5.5);
    public static final String GWALIOR = reg("Gwalior", 26.2, 78.2, 5.5);
    public static final String HUBLI = reg("Hubli", 15.4, 75.1, 5.5);
    public static final String JABALPUR = reg("Jabalpur", 23.2, 79.9, 5.5);
    public static final String JALANDHAR = reg("Jalandhar", 31.3, 75.6, 5.5);
    public static final String JODHPUR = reg("Jodhpur", 26.3, 73.0, 5.5);
    public static final String KANPUR = reg("Kanpur", 26.4, 80.3, 5.5);
    public static final String KOTA = reg("Kota", 25.2, 75.9, 5.5);
    public static final String LUDHIANA = reg("Ludhiana", 30.9, 75.9, 5.5);
    public static final String MADURAI = reg("Madurai", 9.9, 78.1, 5.5);
    public static final String MEERUT = reg("Meerut", 29.0, 77.7, 5.5);
    public static final String NASHIK = reg("Nashik", 20.0, 73.8, 5.5);
    public static final String RAIPUR = reg("Raipur", 21.3, 81.6, 5.5);
    public static final String RAJKOT = reg("Rajkot", 22.3, 70.8, 5.5);
    public static final String RANCHI = reg("Ranchi", 23.3, 85.3, 5.5);
    public static final String SALEM = reg("Salem", 11.7, 78.2, 5.5);
    public static final String SURAT = reg("Surat", 21.2, 72.8, 5.5);
    public static final String THANE = reg("Thane", 19.2, 73.0, 5.5);
    public static final String TIRUCHIRAPPALLI = reg("Tiruchirappalli", 10.8, 78.7, 5.5);
    public static final String TIRUPATI = reg("Tirupati", 13.6, 79.4, 5.5);
    public static final String UDAIPUR = reg("Udaipur", 24.6, 73.7, 5.5);
    public static final String VADODARA = reg("Vadodara", 22.3, 73.2, 5.5);
    public static final String VIJAYAWADA = reg("Vijayawada", 16.5, 80.6, 5.5);
    public static final String WARANGAL = reg("Warangal", 18.0, 79.6, 5.5);
    public static final String MUSSOORIE = reg("Mussoorie", 30.5, 78.1, 5.5);
    public static final String RISHIKESH = reg("Rishikesh", 30.1, 78.3, 5.5);
    public static final String HARIDWAR = reg("Haridwar", 29.9, 78.2, 5.5);
    public static final String MATHURA = reg("Mathura", 27.5, 77.7, 5.5);
    public static final String VRINDAVAN = reg("Vrindavan", 27.6, 77.7, 5.5);
    public static final String AYODHYA = reg("Ayodhya", 26.8, 82.2, 5.5);
    public static final String PRAYAGRAJ = reg("Prayagraj", 25.4, 81.8, 5.5);
    public static final String DWARKA = reg("Dwarka", 22.2, 69.0, 5.5);
    public static final String SHIRDI = reg("Shirdi", 19.8, 74.5, 5.5);

    // ─── North America ───
    public static final String SEATTLE = reg("Seattle", 47.6, -122.3, -8.0);
    public static final String KIRKLAND = reg("Kirkland", 47.7, -122.2, -8.0);
    public static final String SAN_FRANCISCO = reg("San Francisco", 37.8, -122.4, -8.0);
    public static final String FREMONT = reg("Fremont", 37.5, -122.0, -8.0);
    public static final String SAN_JOSE = reg("San Jose", 37.3, -121.9, -8.0);
    public static final String LOS_ANGELES = reg("Los Angeles", 34.1, -118.2, -8.0);
    public static final String DALLAS = reg("Dallas", 32.8, -96.8, -6.0);
    public static final String AUSTIN = reg("Austin", 30.3, -97.7, -6.0);
    public static final String HOUSTON = reg("Houston", 29.8, -95.4, -6.0);
    public static final String BOSTON = reg("Boston", 42.4, -71.1, -5.0);
    public static final String NEW_YORK = reg("New York", 40.7, -74.0, -5.0);
    public static final String CHICAGO = reg("Chicago", 41.9, -87.6, -6.0);
    public static final String ATLANTA = reg("Atlanta", 33.7, -84.4, -5.0);
    public static final String ORLANDO = reg("Orlando", 28.5, -81.4, -5.0);
    public static final String DENVER = reg("Denver", 39.7, -105.0, -7.0);
    public static final String PHOENIX = reg("Phoenix", 33.4, -112.1, -7.0);
    public static final String WASHINGTON_DC = reg("Washington DC", 38.9, -77.0, -5.0);
    public static final String MIAMI = reg("Miami", 25.8, -80.2, -5.0);
    public static final String PORTLAND = reg("Portland", 45.5, -122.7, -8.0);
    public static final String MINNEAPOLIS = reg("Minneapolis", 44.9, -93.3, -6.0);
    public static final String DETROIT = reg("Detroit", 42.3, -83.0, -5.0);
    public static final String PHILADELPHIA = reg("Philadelphia", 40.0, -75.2, -5.0);
    public static final String SAN_DIEGO = reg("San Diego", 32.7, -117.2, -8.0);
    public static final String RALEIGH = reg("Raleigh", 35.8, -78.6, -5.0);
    public static final String TORONTO = reg("Toronto", 43.7, -79.4, -5.0);
    public static final String VANCOUVER = reg("Vancouver", 49.3, -123.1, -8.0);
    public static final String MONTREAL = reg("Montreal", 45.5, -73.6, -5.0);
    public static final String CALGARY = reg("Calgary", 51.0, -114.1, -7.0);
    public static final String OTTAWA = reg("Ottawa", 45.4, -75.7, -5.0);
    public static final String MEXICO_CITY = reg("Mexico City", 19.4, -99.1, -6.0);

    // ─── Europe ───
    public static final String LONDON = reg("London", 51.5, -0.1, 0.0);
    public static final String BERLIN = reg("Berlin", 52.5, 13.4, 1.0);
    public static final String AMSTERDAM = reg("Amsterdam", 52.4, 4.9, 1.0);
    public static final String PARIS = reg("Paris", 48.9, 2.3, 1.0);
    public static final String DUBLIN = reg("Dublin", 53.3, -6.3, 0.0);
    public static final String MUNICH = reg("Munich", 48.1, 11.6, 1.0);
    public static final String ZURICH = reg("Zurich", 47.4, 8.5, 1.0);
    public static final String STOCKHOLM = reg("Stockholm", 59.3, 18.1, 1.0);
    public static final String HELSINKI = reg("Helsinki", 60.2, 24.9, 2.0);
    public static final String WARSAW = reg("Warsaw", 52.2, 21.0, 1.0);
    public static final String VIENNA = reg("Vienna", 48.2, 16.4, 1.0);
    public static final String PRAGUE = reg("Prague", 50.1, 14.4, 1.0);
    public static final String MILAN = reg("Milan", 45.5, 9.2, 1.0);
    public static final String BARCELONA = reg("Barcelona", 41.4, 2.2, 1.0);
    public static final String LISBON = reg("Lisbon", 38.7, -9.1, 0.0);
    public static final String MOSCOW = reg("Moscow", 55.8, 37.6, 3.0);
    public static final String ISTANBUL = reg("Istanbul", 41.0, 29.0, 3.0);
    public static final String EDINBURGH = reg("Edinburgh", 55.9, -3.2, 0.0);
    public static final String MANCHESTER = reg("Manchester", 53.5, -2.2, 0.0);
    public static final String BIRMINGHAM = reg("Birmingham", 52.5, -1.9, 0.0);

    // ─── Middle East ───
    public static final String DUBAI = reg("Dubai", 25.2, 55.3, 4.0);
    public static final String MUSCAT = reg("Muscat", 23.6, 58.5, 4.0);
    public static final String DOHA = reg("Doha", 25.3, 51.5, 3.0);
    public static final String RIYADH = reg("Riyadh", 24.7, 46.7, 3.0);
    public static final String KUWAIT_CITY = reg("Kuwait City", 29.4, 47.9, 3.0);
    public static final String BAHRAIN = reg("Bahrain", 26.2, 50.6, 3.0);
    public static final String TEL_AVIV = reg("Tel Aviv", 32.1, 34.8, 2.0);

    // ─── Asia Pacific ───
    public static final String SINGAPORE = reg("Singapore", 1.4, 103.8, 8.0);
    public static final String TOKYO = reg("Tokyo", 35.7, 139.7, 9.0);
    public static final String HONG_KONG = reg("Hong Kong", 22.3, 114.2, 8.0);
    public static final String KUALA_LUMPUR = reg("Kuala Lumpur", 3.1, 101.7, 8.0);
    public static final String BANGKOK = reg("Bangkok", 13.8, 100.5, 7.0);
    public static final String JAKARTA = reg("Jakarta", -6.2, 106.8, 7.0);
    public static final String SEOUL = reg("Seoul", 37.6, 127.0, 9.0);
    public static final String TAIPEI = reg("Taipei", 25.0, 121.5, 8.0);
    public static final String MANILA = reg("Manila", 14.6, 121.0, 8.0);
    public static final String HO_CHI_MINH_CITY = reg("Ho Chi Minh City", 10.8, 106.6, 7.0);
    public static final String KATHMANDU = reg("Kathmandu", 27.7, 85.3, 5.75);
    public static final String COLOMBO = reg("Colombo", 6.9, 79.9, 5.5);
    public static final String DHAKA = reg("Dhaka", 23.8, 90.4, 6.0);
    public static final String BEIJING = reg("Beijing", 39.9, 116.4, 8.0);
    public static final String SHANGHAI = reg("Shanghai", 31.2, 121.5, 8.0);
    public static final String OSAKA = reg("Osaka", 34.7, 135.5, 9.0);

    // ─── Oceania ───
    public static final String SYDNEY = reg("Sydney", -33.9, 151.2, 10.0);
    public static final String MELBOURNE = reg("Melbourne", -37.8, 145.0, 10.0);
    public static final String BRISBANE = reg("Brisbane", -27.5, 153.0, 10.0);
    public static final String PERTH = reg("Perth", -31.9, 115.9, 8.0);
    public static final String AUCKLAND = reg("Auckland", -36.8, 174.8, 12.0);

    // ─── Africa ───
    public static final String NAIROBI = reg("Nairobi", -1.3, 36.8, 3.0);
    public static final String CAPE_TOWN = reg("Cape Town", -33.9, 18.4, 2.0);
    public static final String LAGOS = reg("Lagos", 6.5, 3.4, 1.0);
    public static final String CAIRO = reg("Cairo", 30.0, 31.2, 2.0);
    public static final String JOHANNESBURG = reg("Johannesburg", -26.2, 28.0, 2.0);

    // ─── South America ───
    public static final String SAO_PAULO = reg("São Paulo", -23.5, -46.6, -3.0);
    public static final String BUENOS_AIRES = reg("Buenos Aires", -34.6, -58.4, -3.0);
    public static final String BOGOTA = reg("Bogotá", 4.7, -74.1, -5.0);
    public static final String LIMA = reg("Lima", -12.0, -77.0, -5.0);
    public static final String SANTIAGO = reg("Santiago", -33.4, -70.6, -4.0);

    // ─── Default ───
    public static final String DEFAULT = UJJAIN;

    // ─── Registry methods ───

    /**
     * Look up coordinates for a city name.
     * @param city registered city name (e.g. {@code City.SEATTLE})
     * @return location data, or {@link #DEFAULT} location if not found
     */
    public static CityLocation getLocation(String city) {
        CityLocation loc = LOCATIONS.get(city);
        return loc != null ? loc : LOCATIONS.get(DEFAULT);
    }

    /**
     * All registered city names (unmodifiable).
     * @return set of city name strings
     */
    public static Set<String> supported() {
        return Collections.unmodifiableSet(LOCATIONS.keySet());
    }

    private static String reg(String name, double lat, double lon, double utcOffset) {
        LOCATIONS.put(name, new CityLocation(lat, lon, utcOffset));
        return name;
    }
}
