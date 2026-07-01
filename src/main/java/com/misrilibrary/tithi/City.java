package com.misrilibrary.tithi;

import java.util.*;

/**
 * A supported city as a typed value: its canonical {@link #name()} and optional
 * {@link #region()} qualifier. Use the constants (e.g. {@code City.SEATTLE}) for
 * type-safe autocomplete, or resolve a string with {@link #of(String)} /
 * {@link #tryOf(String)}.
 *
 * <p>The coordinate/timezone registry stays internal — the engine resolves a
 * city's location itself; callers never see the geo data. Mirrors the Dart
 * {@code tithi-engine-dart} {@code City}.
 */
public final class City {

    private final String name;
    private final String region;

    private City(String name, String region) {
        this.name = name;
        this.region = region;
    }

    // ── Internal registry (name-keyed) ──────────────────────────────────────
    private static final Map<String, CityLocation> LOCATIONS = new LinkedHashMap<>();
    // Normalized key (and "City, Region" qualified form) -> canonical city name.
    private static final Map<String, String> RESOLVE = new HashMap<>();
    // Canonical name -> City value.
    private static final Map<String, City> BY_NAME = new LinkedHashMap<>();
    private static final List<City> VALUES = new ArrayList<>();

    private static String canon(String s) { return s.toLowerCase().replaceAll("\\s+", ""); }

    public static final City ABU_DHABI = reg("Abu Dhabi", 24.5, 54.4, 4.0, "UAE");
    public static final City ACCRA = reg("Accra", 5.6, -0.2, 0.0, "Ghana");
    public static final City ADDIS_ABABA = reg("Addis Ababa", 9.0, 38.7, 3.0, "Ethiopia");
    public static final City ADELAIDE = reg("Adelaide", -34.9, 138.6, 9.5, "Australia");
    public static final City AGRA = reg("Agra", 27.2, 78.0, 5.5, "India");
    public static final City AHMEDABAD = reg("Ahmedabad", 23.0, 72.6, 5.5, "India");
    public static final City ALLAHABAD = reg("Allahabad", 25.4, 81.8, 5.5, "India");
    public static final City ALMATY = reg("Almaty", 43.2, 76.9, 6.0, "Kazakhstan");
    public static final City AMMAN = reg("Amman", 31.9, 35.9, 2.0, "Jordan");
    public static final City AMRITSAR = reg("Amritsar", 31.6, 74.9, 5.5, "India");
    public static final City AMSTERDAM = reg("Amsterdam", 52.4, 4.9, 1.0, "Netherlands");
    public static final City ANKARA = reg("Ankara", 39.9, 32.9, 3.0, "Turkey");
    public static final City ASHBURN = reg("Ashburn", 39.0, -77.5, -5.0, "VA");
    public static final City ATHENS = reg("Athens", 37.9, 23.7, 2.0, "Greece");
    public static final City ATLANTA = reg("Atlanta", 33.7, -84.4, -5.0, "GA");
    public static final City AUCKLAND = reg("Auckland", -36.8, 174.8, 12.0, "New Zealand");
    public static final City AURANGABAD = reg("Aurangabad", 19.9, 75.3, 5.5, "India");
    public static final City AUSTIN = reg("Austin", 30.3, -97.7, -6.0, "TX");
    public static final City AYODHYA = reg("Ayodhya", 26.8, 82.2, 5.5, "India");
    public static final City BAHRAIN = reg("Bahrain", 26.2, 50.6, 3.0);
    public static final City BANGALORE = reg("Bangalore", 12.9, 77.6, 5.5, "India");
    public static final City BANGKOK = reg("Bangkok", 13.8, 100.5, 7.0, "Thailand");
    public static final City BARCELONA = reg("Barcelona", 41.4, 2.2, 1.0, "Spain");
    public static final City BEIJING = reg("Beijing", 39.9, 116.4, 8.0, "China");
    public static final City BEIRUT = reg("Beirut", 33.9, 35.5, 2.0, "Lebanon");
    public static final City BERLIN = reg("Berlin", 52.5, 13.4, 1.0, "Germany");
    public static final City BHOPAL = reg("Bhopal", 23.3, 77.4, 5.5, "India");
    public static final City BHUBANESWAR = reg("Bhubaneswar", 20.3, 85.8, 5.5, "India");
    public static final City BIRMINGHAM = reg("Birmingham", 52.5, -1.9, 0.0, "UK");
    public static final City BOGOTA = reg("Bogotá", 4.7, -74.1, -5.0, "Colombia");
    public static final City BOSTON = reg("Boston", 42.4, -71.1, -5.0, "MA");
    public static final City BRAMPTON = reg("Brampton", 43.7, -79.8, -5.0, "ON");
    public static final City BRISBANE = reg("Brisbane", -27.5, 153.0, 10.0, "Australia");
    public static final City BRUSSELS = reg("Brussels", 50.9, 4.4, 1.0, "Belgium");
    public static final City BUCHAREST = reg("Bucharest", 44.4, 26.1, 2.0, "Romania");
    public static final City BUDAPEST = reg("Budapest", 47.5, 19.0, 1.0, "Hungary");
    public static final City BUENOS_AIRES = reg("Buenos Aires", -34.6, -58.4, -3.0, "Argentina");
    public static final City BUFFALO = reg("Buffalo", 42.9, -78.9, -5.0, "NY");
    public static final City CAIRO = reg("Cairo", 30.0, 31.2, 2.0, "Egypt");
    public static final City CALGARY = reg("Calgary", 51.0, -114.1, -7.0, "AB");
    public static final City CANBERRA = reg("Canberra", -35.3, 149.1, 10.0, "Australia");
    public static final City CAPE_TOWN = reg("Cape Town", -33.9, 18.4, 2.0, "South Africa");
    public static final City CHANDIGARH = reg("Chandigarh", 30.7, 76.8, 5.5, "India");
    public static final City CHARLESTON = reg("Charleston", 32.8, -79.9, -5.0, "SC");
    public static final City CHARLOTTE = reg("Charlotte", 35.2, -80.8, -5.0, "NC");
    public static final City CHENNAI = reg("Chennai", 13.1, 80.3, 5.5, "India");
    public static final City CHICAGO = reg("Chicago", 41.9, -87.6, -6.0, "IL");
    public static final City CLEVELAND = reg("Cleveland", 41.5, -81.7, -5.0, "OH");
    public static final City COIMBATORE = reg("Coimbatore", 11.0, 76.9, 5.5, "India");
    public static final City COLOMBO = reg("Colombo", 6.9, 79.9, 5.5, "Sri Lanka");
    public static final City COLUMBIA = reg("Columbia", 34.0, -81.0, -5.0, "SC");
    public static final City COLUMBUS = reg("Columbus", 39.9, -83.0, -5.0, "OH");
    public static final City COPENHAGEN = reg("Copenhagen", 55.7, 12.6, 1.0, "Denmark");
    public static final City DALLAS = reg("Dallas", 32.8, -96.8, -6.0, "TX");
    public static final City DAR_ES_SALAAM = reg("Dar es Salaam", -6.8, 39.3, 3.0, "Tanzania");
    public static final City DEHRADUN = reg("Dehradun", 30.3, 78.0, 5.5, "India");
    public static final City DELHI = reg("Delhi", 28.6, 77.2, 5.5, "India");
    public static final City DENPASAR = reg("Denpasar", -8.7, 115.2, 8.0, "Indonesia");
    public static final City DENVER = reg("Denver", 39.7, -105.0, -7.0, "CO");
    public static final City DETROIT = reg("Detroit", 42.3, -83.0, -5.0, "MI");
    public static final City DHAKA = reg("Dhaka", 23.8, 90.4, 6.0, "Bangladesh");
    public static final City DOHA = reg("Doha", 25.3, 51.5, 3.0, "Qatar");
    public static final City DUBAI = reg("Dubai", 25.2, 55.3, 4.0, "UAE");
    public static final City DUBLIN = reg("Dublin", 53.3, -6.3, 0.0, "Ireland");
    public static final City DURBAN = reg("Durban", -29.9, 31.0, 2.0, "South Africa");
    public static final City DWARKA = reg("Dwarka", 22.2, 69.0, 5.5, "India");
    public static final City EDINBURGH = reg("Edinburgh", 55.9, -3.2, 0.0, "UK");
    public static final City EDISON = reg("Edison", 40.5, -74.4, -5.0, "NJ");
    public static final City EDMONTON = reg("Edmonton", 53.5, -113.5, -7.0, "AB");
    public static final City FAIRFAX = reg("Fairfax", 38.8, -77.3, -5.0, "VA");
    public static final City FARIDABAD = reg("Faridabad", 28.4, 77.3, 5.5, "India");
    public static final City FREMONT = reg("Fremont", 37.5, -122.0, -8.0, "CA");
    public static final City GEORGETOWN = reg("Georgetown", 6.8, -58.2, -4.0, "Guyana");
    public static final City GHAZIABAD = reg("Ghaziabad", 28.7, 77.4, 5.5, "India");
    public static final City GLASGOW = reg("Glasgow", 55.9, -4.3, 0.0, "UK");
    public static final City GORAKHPUR = reg("Gorakhpur", 26.8, 83.4, 5.5, "India");
    public static final City GOTHENBURG = reg("Gothenburg", 57.7, 12.0, 1.0, "Sweden");
    public static final City GREENVILLE = reg("Greenville", 34.9, -82.4, -5.0, "SC");
    public static final City GURGAON = reg("Gurgaon", 28.5, 77.0, 5.5, "India");
    public static final City GUWAHATI = reg("Guwahati", 26.1, 91.7, 5.5, "India");
    public static final City GWALIOR = reg("Gwalior", 26.2, 78.2, 5.5, "India");
    public static final City HALIFAX = reg("Halifax", 44.6, -63.6, -4.0, "NS");
    public static final City HANOI = reg("Hanoi", 21.0, 105.9, 7.0, "Vietnam");
    public static final City HARIDWAR = reg("Haridwar", 29.9, 78.2, 5.5, "India");
    public static final City HELSINKI = reg("Helsinki", 60.2, 24.9, 2.0, "Finland");
    public static final City HO_CHI_MINH_CITY = reg("Ho Chi Minh City", 10.8, 106.6, 7.0, "Vietnam");
    public static final City HONG_KONG = reg("Hong Kong", 22.3, 114.2, 8.0);
    public static final City HONOLULU = reg("Honolulu", 21.3, -157.8, -10.0, "HI");
    public static final City HOUSTON = reg("Houston", 29.8, -95.4, -6.0, "TX");
    public static final City HUBLI = reg("Hubli", 15.4, 75.1, 5.5, "India");
    public static final City HYDERABAD = reg("Hyderabad", 17.4, 78.5, 5.5, "India");
    public static final City INDIANAPOLIS = reg("Indianapolis", 39.8, -86.2, -5.0, "IN");
    public static final City INDORE = reg("Indore", 22.7, 75.9, 5.5, "India");
    public static final City ISLAMABAD = reg("Islamabad", 33.7, 73.0, 5.0, "Pakistan");
    public static final City ISTANBUL = reg("Istanbul", 41.0, 29.0, 3.0, "Turkey");
    public static final City JABALPUR = reg("Jabalpur", 23.2, 79.9, 5.5, "India");
    public static final City JAIPUR = reg("Jaipur", 26.9, 75.8, 5.5, "India");
    public static final City JAKARTA = reg("Jakarta", -6.2, 106.8, 7.0, "Indonesia");
    public static final City JALANDHAR = reg("Jalandhar", 31.3, 75.6, 5.5, "India");
    public static final City JAMMU = reg("Jammu", 32.7, 74.9, 5.5, "India");
    public static final City JEDDAH = reg("Jeddah", 21.5, 39.2, 3.0, "Saudi Arabia");
    public static final City JERSEY_CITY = reg("Jersey City", 40.7, -74.1, -5.0, "NJ");
    public static final City JODHPUR = reg("Jodhpur", 26.3, 73.0, 5.5, "India");
    public static final City JOHANNESBURG = reg("Johannesburg", -26.2, 28.0, 2.0, "South Africa");
    public static final City KAMPALA = reg("Kampala", 0.3, 32.6, 3.0, "Uganda");
    public static final City KANPUR = reg("Kanpur", 26.4, 80.3, 5.5, "India");
    public static final City KANSAS_CITY = reg("Kansas City", 39.1, -94.6, -6.0, "MO");
    public static final City KARACHI = reg("Karachi", 24.9, 67.0, 5.0, "Pakistan");
    public static final City KATHMANDU = reg("Kathmandu", 27.7, 85.3, 5.75, "Nepal");
    public static final City KINGSTON = reg("Kingston", 18.0, -76.8, -5.0, "Jamaica");
    public static final City KIRKLAND = reg("Kirkland", 47.7, -122.2, -8.0, "WA");
    public static final City KOCHI = reg("Kochi", 10.0, 76.3, 5.5, "India");
    public static final City KOLKATA = reg("Kolkata", 22.6, 88.4, 5.5, "India");
    public static final City KOTA = reg("Kota", 25.2, 75.9, 5.5, "India");
    public static final City KRAKOW = reg("Krakow", 50.1, 19.9, 1.0, "Poland");
    public static final City KUALA_LUMPUR = reg("Kuala Lumpur", 3.1, 101.7, 8.0, "Malaysia");
    public static final City KUWAIT_CITY = reg("Kuwait City", 29.4, 47.9, 3.0, "Kuwait");
    public static final City KYIV = reg("Kyiv", 50.4, 30.5, 2.0, "Ukraine");
    public static final City LAGOS = reg("Lagos", 6.5, 3.4, 1.0, "Nigeria");
    public static final City LAHORE = reg("Lahore", 31.5, 74.3, 5.0, "Pakistan");
    public static final City LAS_VEGAS = reg("Las Vegas", 36.2, -115.2, -8.0, "NV");
    public static final City LEICESTER = reg("Leicester", 52.6, -1.1, 0.0, "UK");
    public static final City LEXINGTON = reg("Lexington", 38.0, -84.5, -5.0, "KY");
    public static final City LIMA = reg("Lima", -12.0, -77.0, -5.0, "Peru");
    public static final City LISBON = reg("Lisbon", 38.7, -9.1, 0.0, "Portugal");
    public static final City LONDON = reg("London", 51.5, -0.1, 0.0, "UK");
    public static final City LOS_ANGELES = reg("Los Angeles", 34.1, -118.2, -8.0, "CA");
    public static final City LOUISVILLE = reg("Louisville", 38.2, -85.8, -5.0, "KY");
    public static final City LUCKNOW = reg("Lucknow", 26.8, 81.0, 5.5, "India");
    public static final City LUDHIANA = reg("Ludhiana", 30.9, 75.9, 5.5, "India");
    public static final City LYON = reg("Lyon", 45.8, 4.8, 1.0, "France");
    public static final City MADRID = reg("Madrid", 40.4, -3.7, 1.0, "Spain");
    public static final City MADURAI = reg("Madurai", 9.9, 78.1, 5.5, "India");
    public static final City MANCHESTER = reg("Manchester", 53.5, -2.2, 0.0, "UK");
    public static final City MANGALORE = reg("Mangalore", 12.9, 74.9, 5.5, "India");
    public static final City MANILA = reg("Manila", 14.6, 121.0, 8.0, "Philippines");
    public static final City MATHURA = reg("Mathura", 27.5, 77.7, 5.5, "India");
    public static final City MEDELLIN = reg("Medellín", 6.2, -75.6, -5.0, "Colombia");
    public static final City MEERUT = reg("Meerut", 29.0, 77.7, 5.5, "India");
    public static final City MELBOURNE = reg("Melbourne", -37.8, 145.0, 10.0, "Australia");
    public static final City MEXICO_CITY = reg("Mexico City", 19.4, -99.1, -6.0, "Mexico");
    public static final City MIAMI = reg("Miami", 25.8, -80.2, -5.0, "FL");
    public static final City MILAN = reg("Milan", 45.5, 9.2, 1.0, "Italy");
    public static final City MILWAUKEE = reg("Milwaukee", 43.0, -87.9, -6.0, "WI");
    public static final City MINNEAPOLIS = reg("Minneapolis", 44.9, -93.3, -6.0, "MN");
    public static final City MISSISSAUGA = reg("Mississauga", 43.6, -79.7, -5.0, "ON");
    public static final City MOMBASA = reg("Mombasa", -4.1, 39.7, 3.0, "Kenya");
    public static final City MONTREAL = reg("Montreal", 45.5, -73.6, -5.0, "QC");
    public static final City MOSCOW = reg("Moscow", 55.8, 37.6, 3.0, "Russia");
    public static final City MUMBAI = reg("Mumbai", 19.1, 72.9, 5.5, "India");
    public static final City MUNICH = reg("Munich", 48.1, 11.6, 1.0, "Germany");
    public static final City MUSCAT = reg("Muscat", 23.6, 58.5, 4.0, "Oman");
    public static final City MUSSOORIE = reg("Mussoorie", 30.5, 78.1, 5.5, "India");
    public static final City MYSORE = reg("Mysore", 12.3, 76.7, 5.5, "India");
    public static final City NAGPUR = reg("Nagpur", 21.1, 79.1, 5.5, "India");
    public static final City NAIROBI = reg("Nairobi", -1.3, 36.8, 3.0, "Kenya");
    public static final City NAPLES = reg("Naples", 40.8, 14.3, 1.0, "Italy");
    public static final City NASHIK = reg("Nashik", 20.0, 73.8, 5.5, "India");
    public static final City NASHVILLE = reg("Nashville", 36.2, -86.8, -6.0, "TN");
    public static final City NEW_YORK = reg("New York", 40.7, -74.0, -5.0, "NY");
    public static final City NOIDA = reg("Noida", 28.6, 77.3, 5.5, "India");
    public static final City ORLANDO = reg("Orlando", 28.5, -81.4, -5.0, "FL");
    public static final City OSAKA = reg("Osaka", 34.7, 135.5, 9.0, "Japan");
    public static final City OSLO = reg("Oslo", 59.9, 10.8, 1.0, "Norway");
    public static final City OTTAWA = reg("Ottawa", 45.4, -75.7, -5.0, "ON");
    public static final City PANAMA_CITY = reg("Panama City", 9.0, -79.5, -5.0, "Panama");
    public static final City PARAMARIBO = reg("Paramaribo", 5.9, -55.2, -3.0, "Suriname");
    public static final City PARIS = reg("Paris", 48.9, 2.3, 1.0, "France");
    public static final City PATNA = reg("Patna", 25.6, 85.1, 5.5, "India");
    public static final City PERTH = reg("Perth", -31.9, 115.9, 8.0, "Australia");
    public static final City PHILADELPHIA = reg("Philadelphia", 40.0, -75.2, -5.0, "PA");
    public static final City PHNOM_PENH = reg("Phnom Penh", 11.6, 104.9, 7.0, "Cambodia");
    public static final City PHOENIX = reg("Phoenix", 33.4, -112.1, -7.0, "AZ");
    public static final City PITTSBURGH = reg("Pittsburgh", 40.4, -80.0, -5.0, "PA");
    public static final City PORT_LOUIS = reg("Port Louis", -20.2, 57.5, 4.0, "Mauritius");
    public static final City PORT_OF_SPAIN = reg("Port of Spain", 10.7, -61.5, -4.0, "Trinidad");
    public static final City PORTLAND = reg("Portland", 45.5, -122.7, -8.0, "OR");
    public static final City PORTO = reg("Porto", 41.2, -8.6, 0.0, "Portugal");
    public static final City PRAGUE = reg("Prague", 50.1, 14.4, 1.0, "Czechia");
    public static final City PRAYAGRAJ = reg("Prayagraj", 25.4, 81.8, 5.5, "India");
    public static final City PUNE = reg("Pune", 18.5, 73.9, 5.5, "India");
    public static final City QUITO = reg("Quito", -0.2, -78.5, -5.0, "Ecuador");
    public static final City RAIPUR = reg("Raipur", 21.3, 81.6, 5.5, "India");
    public static final City RAJKOT = reg("Rajkot", 22.3, 70.8, 5.5, "India");
    public static final City RALEIGH = reg("Raleigh", 35.8, -78.6, -5.0, "NC");
    public static final City RANCHI = reg("Ranchi", 23.3, 85.3, 5.5, "India");
    public static final City REDMOND = reg("Redmond", 47.7, -122.1, -8.0, "WA");
    public static final City REGINA = reg("Regina", 50.5, -104.6, -6.0, "SK");
    public static final City RIO_DE_JANEIRO = reg("Rio de Janeiro", -22.9, -43.2, -3.0, "Brazil");
    public static final City RISHIKESH = reg("Rishikesh", 30.1, 78.3, 5.5, "India");
    public static final City RIYADH = reg("Riyadh", 24.7, 46.7, 3.0, "Saudi Arabia");
    public static final City ROCKVILLE = reg("Rockville", 39.1, -77.2, -5.0, "MD");
    public static final City ROME = reg("Rome", 41.9, 12.5, 1.0, "Italy");
    public static final City ROTTERDAM = reg("Rotterdam", 51.9, 4.5, 1.0, "Netherlands");
    public static final City SACRAMENTO = reg("Sacramento", 38.6, -121.5, -8.0, "CA");
    public static final City SALEM = reg("Salem", 11.7, 78.2, 5.5, "India");
    public static final City SALT_LAKE_CITY = reg("Salt Lake City", 40.8, -111.9, -7.0, "UT");
    public static final City SAN_ANTONIO = reg("San Antonio", 29.4, -98.5, -6.0, "TX");
    public static final City SAN_DIEGO = reg("San Diego", 32.7, -117.2, -8.0, "CA");
    public static final City SAN_FRANCISCO = reg("San Francisco", 37.8, -122.4, -8.0, "CA");
    public static final City SAN_JOSE = reg("San Jose", 37.3, -121.9, -8.0, "CA");
    public static final City SANTIAGO = reg("Santiago", -33.4, -70.6, -4.0, "Chile");
    public static final City SEATTLE = reg("Seattle", 47.6, -122.3, -8.0, "WA");
    public static final City SEOUL = reg("Seoul", 37.6, 127.0, 9.0, "South Korea");
    public static final City SHANGHAI = reg("Shanghai", 31.2, 121.5, 8.0, "China");
    public static final City SHIRDI = reg("Shirdi", 19.8, 74.5, 5.5, "India");
    public static final City SINGAPORE = reg("Singapore", 1.4, 103.8, 8.0);
    public static final City SOFIA = reg("Sofia", 42.7, 23.3, 2.0, "Bulgaria");
    public static final City SRINAGAR = reg("Srinagar", 34.1, 74.8, 5.5, "India");
    public static final City ST_LOUIS = reg("St. Louis", 38.6, -90.2, -6.0, "MO");
    public static final City STAMFORD = reg("Stamford", 41.1, -73.5, -5.0, "CT");
    public static final City STOCKHOLM = reg("Stockholm", 59.3, 18.1, 1.0, "Sweden");
    public static final City SURAT = reg("Surat", 21.2, 72.8, 5.5, "India");
    public static final City SURREY = reg("Surrey", 49.2, -122.8, -8.0, "BC");
    public static final City SUVA = reg("Suva", -18.1, 178.4, 12.0, "Fiji");
    public static final City SYDNEY = reg("Sydney", -33.9, 151.2, 10.0, "Australia");
    public static final City SAO_PAULO = reg("São Paulo", -23.5, -46.6, -3.0, "Brazil");
    public static final City TAIPEI = reg("Taipei", 25.0, 121.5, 8.0, "Taiwan");
    public static final City TAMPA = reg("Tampa", 27.9, -82.5, -5.0, "FL");
    public static final City TASHKENT = reg("Tashkent", 41.3, 69.3, 5.0, "Uzbekistan");
    public static final City TEL_AVIV = reg("Tel Aviv", 32.1, 34.8, 2.0, "Israel");
    public static final City THANE = reg("Thane", 19.2, 73.0, 5.5, "India");
    public static final City THESSALONIKI = reg("Thessaloniki", 40.6, 22.9, 2.0, "Greece");
    public static final City THIRUVANANTHAPURAM = reg("Thiruvananthapuram", 8.5, 76.9, 5.5, "India");
    public static final City TIRUCHIRAPPALLI = reg("Tiruchirappalli", 10.8, 78.7, 5.5, "India");
    public static final City TIRUPATI = reg("Tirupati", 13.6, 79.4, 5.5, "India");
    public static final City TOKYO = reg("Tokyo", 35.7, 139.7, 9.0, "Japan");
    public static final City TORONTO = reg("Toronto", 43.7, -79.4, -5.0, "ON");
    public static final City UDAIPUR = reg("Udaipur", 24.6, 73.7, 5.5, "India");
    public static final City UJJAIN = reg("Ujjain", 23.2, 75.8, 5.5, "India");
    public static final City VADODARA = reg("Vadodara", 22.3, 73.2, 5.5, "India");
    public static final City VANCOUVER = reg("Vancouver", 49.3, -123.1, -8.0, "BC");
    public static final City VARANASI = reg("Varanasi", 25.3, 83.0, 5.5, "India");
    public static final City VIENNA = reg("Vienna", 48.2, 16.4, 1.0, "Austria");
    public static final City VIJAYAWADA = reg("Vijayawada", 16.5, 80.6, 5.5, "India");
    public static final City VISAKHAPATNAM = reg("Visakhapatnam", 17.7, 83.3, 5.5, "India");
    public static final City VRINDAVAN = reg("Vrindavan", 27.6, 77.7, 5.5, "India");
    public static final City WARANGAL = reg("Warangal", 18.0, 79.6, 5.5, "India");
    public static final City WARSAW = reg("Warsaw", 52.2, 21.0, 1.0, "Poland");
    public static final City WASHINGTON_DC = reg("Washington DC", 38.9, -77.0, -5.0);
    public static final City WELLINGTON = reg("Wellington", -41.3, 174.8, 12.0, "New Zealand");
    public static final City WINNIPEG = reg("Winnipeg", 49.9, -97.1, -6.0, "MB");
    public static final City YANGON = reg("Yangon", 16.9, 96.2, 6.5, "Myanmar");
    public static final City ZAGREB = reg("Zagreb", 45.8, 16.0, 1.0, "Croatia");
    public static final City ZURICH = reg("Zurich", 47.4, 8.5, 1.0, "Switzerland");

    /** Canonical name of the default reference city (internal string keying). */
    static final String DEFAULT_NAME = "Ujjain";

    /** Default reference city used wherever none is specified. */
    public static final City DEFAULT = UJJAIN;

    /**
     * City names whose bare form is commonly confused with another well-known
     * place; only these receive a qualifier from {@link #displayName()}.
     */
    private static final Set<String> AMBIGUOUS = Set.of(
        "Redmond",
        "Birmingham",
        "Manchester",
        "Naples",
        "Vancouver",
        "Athens",
        "San Jose",
        "Portland",
        "Columbus",
        "Kingston",
        "Georgetown",
        "Kochi",
        "Salem",
        "Surrey"
    );

    // ── Instance accessors ──────────────────────────────────────────────────

    /** Canonical city name (e.g. {@code "New York"}). */
    public String name() { return name; }

    /**
     * Region/country qualifier for cities whose bare name collides with another
     * well-known place (e.g. {@code "WA"} for Redmond), or {@code null} for
     * self-qualifying names. Display-only — does not affect any calculation.
     */
    public String region() { return region; }

    /**
     * Fully-qualified label: appends the region/country when present (e.g.
     * {@code "Seattle, WA"}, {@code "Tokyo, Japan"}). Returns the bare name for
     * self-qualifying cities (e.g. {@code "Singapore"}).
     */
    public String qualifiedName() { return region == null ? name : name + ", " + region; }

    /**
     * Compact label: the bare name, with a region qualifier appended only for
     * commonly-confused names (e.g. {@code "Redmond, WA"}; {@code "Delhi"}).
     */
    public String displayName() { return AMBIGUOUS.contains(name) ? qualifiedName() : name; }

    @Override
    public String toString() { return name; }

    // ── Value factories ─────────────────────────────────────────────────────

    /**
     * The {@link City} for {@code name} (case/space-insensitive, or
     * {@code "City, Region"}). Throws {@link IllegalArgumentException} if unknown.
     */
    public static City of(String name) {
        City c = tryOf(name);
        if (c == null) {
            throw new IllegalArgumentException(
                "Unsupported city: \"" + name + "\". Pick the nearest city in City.values() "
                + "and use that, or request it at "
                + "https://github.com/misrilibrary/tithi-engine/issues");
        }
        return c;
    }

    /** The {@link City} for {@code name}, or {@code null} if unknown. */
    public static City tryOf(String name) {
        String canonical = resolveName(name);
        return canonical == null ? null : BY_NAME.get(canonical);
    }

    /** All supported cities, in registration order (unmodifiable). */
    public static List<City> values() {
        return Collections.unmodifiableList(VALUES);
    }

    // ─── Static registry methods (internal name-keyed pipeline) ───

    /**
     * Location for a {@code city} (case/space-insensitive, or {@code "City, Region"}).
     *
     * <p><b>Internal</b> — not part of the public API (the engine never exposes its
     * geo/timezone data). Package-private so internal callers and same-package tests
     * can resolve coordinates.
     *
     * @throws IllegalArgumentException if the city is not supported.
     */
    static CityLocation getLocation(String city) {
        String name = resolveName(city);
        if (name != null) return LOCATIONS.get(name);
        CityLocation adhoc = adHocLocation(city);
        if (adhoc != null) return adhoc;
        throw new IllegalArgumentException(
            "Unsupported city: \"" + city + "\". Pick the nearest city in City.supported() "
            + "and use that, or request it at "
            + "https://github.com/misrilibrary/tithi-engine/issues");
    }

    /**
     * Resolve any reasonable city spelling to a registered city name, or
     * {@code null} if it matches no supported city. Matches (case/space-insensitive)
     * the bare name (the <i>primary</i> city-region) and the {@code "City, Region"}
     * qualified form. No region-stripping fuzzy match — {@code "Vancouver, WA"}
     * never silently resolves to {@code "Vancouver, BC"}.
     */
    public static String resolveName(String city) {
        if (city == null) return null;
        return RESOLVE.get(canon(city));
    }

    /** All registered city names (unmodifiable). */
    public static Set<String> supported() {
        return Collections.unmodifiableSet(LOCATIONS.keySet());
    }

    // ── Coordinate (0.1°) cell index + ad-hoc locations ───────────────────────
    private static volatile Map<String, String> CELL_TO_CITY;
    private static final Map<String, CityLocation> AD_HOC = new java.util.concurrent.ConcurrentHashMap<>();

    private static String cellKey(double lat, double lng) {
        return Math.round(lat * 10) + "|" + Math.round(lng * 10);
    }

    private static Map<String, String> cellIndex() {
        Map<String, String> idx = CELL_TO_CITY;
        if (idx == null) {
            idx = new HashMap<>();
            for (Map.Entry<String, CityLocation> e : LOCATIONS.entrySet()) {
                idx.putIfAbsent(cellKey(e.getValue().getLatitude(), e.getValue().getLongitude()), e.getKey());
            }
            CELL_TO_CITY = idx;
        }
        return idx;
    }

    /** Registered city whose stored 0.1&deg; cell contains (lat,lng), or {@code null}. */
    public static String cityForCell(double lat, double lng) {
        return cellIndex().get(cellKey(lat, lng));
    }

    /**
     * Register raw coordinates so the engine can address them through its normal
     * name-keyed pipeline, returning a stable opaque key. Used for points that
     * don't fall in a supported city's cell (Meeus-only). Idempotent per point.
     */
    public static String registerAdHocLocation(double lat, double lng, double utcOffsetHours) {
        String key = String.format(java.util.Locale.US, "@%.4f,%.4f@%s", lat, lng, utcOffsetHours);
        AD_HOC.computeIfAbsent(key, k -> new CityLocation(lat, lng, utcOffsetHours));
        return key;
    }

    /** Coordinates previously registered via {@link #registerAdHocLocation}, or {@code null}. */
    static CityLocation adHocLocation(String key) {
        return AD_HOC.get(key);
    }

    /**
     * Fully-qualified label for a city name (static form of {@link #qualifiedName()}).
     * Returns the bare name for self-qualifying or unknown names.
     */
    public static String qualifiedName(String city) {
        CityLocation loc = LOCATIONS.get(city);
        String region = loc != null ? loc.getRegion() : null;
        return region == null ? city : city + ", " + region;
    }

    /**
     * Compact label for a city name (static form of {@link #displayName()}).
     */
    public static String displayName(String city) {
        if (!AMBIGUOUS.contains(city)) return city;
        return qualifiedName(city);
    }

    private static City reg(String name, double lat, double lon, double utcOffset) {
        return reg(name, lat, lon, utcOffset, null);
    }

    private static City reg(String name, double lat, double lon, double utcOffset, String region) {
        LOCATIONS.put(name, new CityLocation(lat, lon, utcOffset, region));
        RESOLVE.put(canon(name), name);
        if (region != null) RESOLVE.put(canon(name + ", " + region), name);
        City city = new City(name, region);
        BY_NAME.put(name, city);
        VALUES.add(city);
        return city;
    }
}
