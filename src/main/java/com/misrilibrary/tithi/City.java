package com.misrilibrary.tithi;

import com.misrilibrary.tithi.model.CityLocation;
import java.util.*;

/**
 * City registry — name constants + coordinate/region data in one place.
 * Use constants (e.g. {@code City.SEATTLE}) for type-safe autocomplete,
 * or pass any registered string directly.
 */
public final class City {
    private City() {}

    private static final Map<String, CityLocation> LOCATIONS = new LinkedHashMap<>();
    // Normalized key (and "City, Region" qualified form) -> canonical city name.
    private static final Map<String, String> RESOLVE = new HashMap<>();

    private static String canon(String s) { return s.toLowerCase().replaceAll("\\s+", ""); }

    public static final String ABU_DHABI = reg("Abu Dhabi", 24.5, 54.4, 4.0, "UAE");
    public static final String ACCRA = reg("Accra", 5.6, -0.2, 0.0, "Ghana");
    public static final String ADDIS_ABABA = reg("Addis Ababa", 9.0, 38.7, 3.0, "Ethiopia");
    public static final String ADELAIDE = reg("Adelaide", -34.9, 138.6, 9.5, "Australia");
    public static final String AGRA = reg("Agra", 27.2, 78.0, 5.5, "India");
    public static final String AHMEDABAD = reg("Ahmedabad", 23.0, 72.6, 5.5, "India");
    public static final String ALLAHABAD = reg("Allahabad", 25.4, 81.8, 5.5, "India");
    public static final String ALMATY = reg("Almaty", 43.2, 76.9, 6.0, "Kazakhstan");
    public static final String AMMAN = reg("Amman", 31.9, 35.9, 2.0, "Jordan");
    public static final String AMRITSAR = reg("Amritsar", 31.6, 74.9, 5.5, "India");
    public static final String AMSTERDAM = reg("Amsterdam", 52.4, 4.9, 1.0, "Netherlands");
    public static final String ANKARA = reg("Ankara", 39.9, 32.9, 3.0, "Turkey");
    public static final String ATHENS = reg("Athens", 37.9, 23.7, 2.0, "Greece");
    public static final String ATLANTA = reg("Atlanta", 33.7, -84.4, -5.0, "GA");
    public static final String AUCKLAND = reg("Auckland", -36.8, 174.8, 12.0, "New Zealand");
    public static final String AURANGABAD = reg("Aurangabad", 19.9, 75.3, 5.5, "India");
    public static final String AUSTIN = reg("Austin", 30.3, -97.7, -6.0, "TX");
    public static final String AYODHYA = reg("Ayodhya", 26.8, 82.2, 5.5, "India");
    public static final String BAHRAIN = reg("Bahrain", 26.2, 50.6, 3.0);
    public static final String BANGALORE = reg("Bangalore", 12.9, 77.6, 5.5, "India");
    public static final String BANGKOK = reg("Bangkok", 13.8, 100.5, 7.0, "Thailand");
    public static final String BARCELONA = reg("Barcelona", 41.4, 2.2, 1.0, "Spain");
    public static final String BEIJING = reg("Beijing", 39.9, 116.4, 8.0, "China");
    public static final String BEIRUT = reg("Beirut", 33.9, 35.5, 2.0, "Lebanon");
    public static final String BERLIN = reg("Berlin", 52.5, 13.4, 1.0, "Germany");
    public static final String BHOPAL = reg("Bhopal", 23.3, 77.4, 5.5, "India");
    public static final String BHUBANESWAR = reg("Bhubaneswar", 20.3, 85.8, 5.5, "India");
    public static final String BIRMINGHAM = reg("Birmingham", 52.5, -1.9, 0.0, "UK");
    public static final String BOGOTA = reg("Bogotá", 4.7, -74.1, -5.0, "Colombia");
    public static final String BOSTON = reg("Boston", 42.4, -71.1, -5.0, "MA");
    public static final String BRAMPTON = reg("Brampton", 43.7, -79.8, -5.0, "ON");
    public static final String BRISBANE = reg("Brisbane", -27.5, 153.0, 10.0, "Australia");
    public static final String BRUSSELS = reg("Brussels", 50.9, 4.4, 1.0, "Belgium");
    public static final String BUCHAREST = reg("Bucharest", 44.4, 26.1, 2.0, "Romania");
    public static final String BUDAPEST = reg("Budapest", 47.5, 19.0, 1.0, "Hungary");
    public static final String BUENOS_AIRES = reg("Buenos Aires", -34.6, -58.4, -3.0, "Argentina");
    public static final String CAIRO = reg("Cairo", 30.0, 31.2, 2.0, "Egypt");
    public static final String CALGARY = reg("Calgary", 51.0, -114.1, -7.0, "AB");
    public static final String CANBERRA = reg("Canberra", -35.3, 149.1, 10.0, "Australia");
    public static final String CAPE_TOWN = reg("Cape Town", -33.9, 18.4, 2.0, "South Africa");
    public static final String CHANDIGARH = reg("Chandigarh", 30.7, 76.8, 5.5, "India");
    public static final String CHARLOTTE = reg("Charlotte", 35.2, -80.8, -5.0, "NC");
    public static final String CHENNAI = reg("Chennai", 13.1, 80.3, 5.5, "India");
    public static final String CHICAGO = reg("Chicago", 41.9, -87.6, -6.0, "IL");
    public static final String COIMBATORE = reg("Coimbatore", 11.0, 76.9, 5.5, "India");
    public static final String COLOMBO = reg("Colombo", 6.9, 79.9, 5.5, "Sri Lanka");
    public static final String COLUMBUS = reg("Columbus", 39.9, -83.0, -5.0, "OH");
    public static final String COPENHAGEN = reg("Copenhagen", 55.7, 12.6, 1.0, "Denmark");
    public static final String DALLAS = reg("Dallas", 32.8, -96.8, -6.0, "TX");
    public static final String DAR_ES_SALAAM = reg("Dar es Salaam", -6.8, 39.3, 3.0, "Tanzania");
    public static final String DEHRADUN = reg("Dehradun", 30.3, 78.0, 5.5, "India");
    public static final String DELHI = reg("Delhi", 28.6, 77.2, 5.5, "India");
    public static final String DENPASAR = reg("Denpasar", -8.7, 115.2, 8.0, "Indonesia");
    public static final String DENVER = reg("Denver", 39.7, -105.0, -7.0, "CO");
    public static final String DETROIT = reg("Detroit", 42.3, -83.0, -5.0, "MI");
    public static final String DHAKA = reg("Dhaka", 23.8, 90.4, 6.0, "Bangladesh");
    public static final String DOHA = reg("Doha", 25.3, 51.5, 3.0, "Qatar");
    public static final String DUBAI = reg("Dubai", 25.2, 55.3, 4.0, "UAE");
    public static final String DUBLIN = reg("Dublin", 53.3, -6.3, 0.0, "Ireland");
    public static final String DURBAN = reg("Durban", -29.9, 31.0, 2.0, "South Africa");
    public static final String DWARKA = reg("Dwarka", 22.2, 69.0, 5.5, "India");
    public static final String EDINBURGH = reg("Edinburgh", 55.9, -3.2, 0.0, "UK");
    public static final String EDMONTON = reg("Edmonton", 53.5, -113.5, -7.0, "AB");
    public static final String FARIDABAD = reg("Faridabad", 28.4, 77.3, 5.5, "India");
    public static final String FREMONT = reg("Fremont", 37.5, -122.0, -8.0, "CA");
    public static final String GEORGETOWN = reg("Georgetown", 6.8, -58.2, -4.0, "Guyana");
    public static final String GHAZIABAD = reg("Ghaziabad", 28.7, 77.4, 5.5, "India");
    public static final String GLASGOW = reg("Glasgow", 55.9, -4.3, 0.0, "UK");
    public static final String GORAKHPUR = reg("Gorakhpur", 26.8, 83.4, 5.5, "India");
    public static final String GOTHENBURG = reg("Gothenburg", 57.7, 12.0, 1.0, "Sweden");
    public static final String GURGAON = reg("Gurgaon", 28.5, 77.0, 5.5, "India");
    public static final String GUWAHATI = reg("Guwahati", 26.1, 91.7, 5.5, "India");
    public static final String GWALIOR = reg("Gwalior", 26.2, 78.2, 5.5, "India");
    public static final String HALIFAX = reg("Halifax", 44.6, -63.6, -4.0, "NS");
    public static final String HANOI = reg("Hanoi", 21.0, 105.9, 7.0, "Vietnam");
    public static final String HARIDWAR = reg("Haridwar", 29.9, 78.2, 5.5, "India");
    public static final String HELSINKI = reg("Helsinki", 60.2, 24.9, 2.0, "Finland");
    public static final String HO_CHI_MINH_CITY = reg("Ho Chi Minh City", 10.8, 106.6, 7.0, "Vietnam");
    public static final String HONG_KONG = reg("Hong Kong", 22.3, 114.2, 8.0);
    public static final String HONOLULU = reg("Honolulu", 21.3, -157.8, -10.0, "HI");
    public static final String HOUSTON = reg("Houston", 29.8, -95.4, -6.0, "TX");
    public static final String HUBLI = reg("Hubli", 15.4, 75.1, 5.5, "India");
    public static final String HYDERABAD = reg("Hyderabad", 17.4, 78.5, 5.5, "India");
    public static final String INDIANAPOLIS = reg("Indianapolis", 39.8, -86.2, -5.0, "IN");
    public static final String INDORE = reg("Indore", 22.7, 75.9, 5.5, "India");
    public static final String ISLAMABAD = reg("Islamabad", 33.7, 73.0, 5.0, "Pakistan");
    public static final String ISTANBUL = reg("Istanbul", 41.0, 29.0, 3.0, "Turkey");
    public static final String JABALPUR = reg("Jabalpur", 23.2, 79.9, 5.5, "India");
    public static final String JAIPUR = reg("Jaipur", 26.9, 75.8, 5.5, "India");
    public static final String JAKARTA = reg("Jakarta", -6.2, 106.8, 7.0, "Indonesia");
    public static final String JALANDHAR = reg("Jalandhar", 31.3, 75.6, 5.5, "India");
    public static final String JAMMU = reg("Jammu", 32.7, 74.9, 5.5, "India");
    public static final String JEDDAH = reg("Jeddah", 21.5, 39.2, 3.0, "Saudi Arabia");
    public static final String JODHPUR = reg("Jodhpur", 26.3, 73.0, 5.5, "India");
    public static final String JOHANNESBURG = reg("Johannesburg", -26.2, 28.0, 2.0, "South Africa");
    public static final String KAMPALA = reg("Kampala", 0.3, 32.6, 3.0, "Uganda");
    public static final String KANPUR = reg("Kanpur", 26.4, 80.3, 5.5, "India");
    public static final String KANSAS_CITY = reg("Kansas City", 39.1, -94.6, -6.0, "MO");
    public static final String KARACHI = reg("Karachi", 24.9, 67.0, 5.0, "Pakistan");
    public static final String KATHMANDU = reg("Kathmandu", 27.7, 85.3, 5.75, "Nepal");
    public static final String KINGSTON = reg("Kingston", 18.0, -76.8, -5.0, "Jamaica");
    public static final String KIRKLAND = reg("Kirkland", 47.7, -122.2, -8.0, "WA");
    public static final String KOCHI = reg("Kochi", 10.0, 76.3, 5.5, "India");
    public static final String KOLKATA = reg("Kolkata", 22.6, 88.4, 5.5, "India");
    public static final String KOTA = reg("Kota", 25.2, 75.9, 5.5, "India");
    public static final String KRAKOW = reg("Krakow", 50.1, 19.9, 1.0, "Poland");
    public static final String KUALA_LUMPUR = reg("Kuala Lumpur", 3.1, 101.7, 8.0, "Malaysia");
    public static final String KUWAIT_CITY = reg("Kuwait City", 29.4, 47.9, 3.0, "Kuwait");
    public static final String KYIV = reg("Kyiv", 50.4, 30.5, 2.0, "Ukraine");
    public static final String LAGOS = reg("Lagos", 6.5, 3.4, 1.0, "Nigeria");
    public static final String LAHORE = reg("Lahore", 31.5, 74.3, 5.0, "Pakistan");
    public static final String LAS_VEGAS = reg("Las Vegas", 36.2, -115.2, -8.0, "NV");
    public static final String LEICESTER = reg("Leicester", 52.6, -1.1, 0.0, "UK");
    public static final String LIMA = reg("Lima", -12.0, -77.0, -5.0, "Peru");
    public static final String LISBON = reg("Lisbon", 38.7, -9.1, 0.0, "Portugal");
    public static final String LONDON = reg("London", 51.5, -0.1, 0.0, "UK");
    public static final String LOS_ANGELES = reg("Los Angeles", 34.1, -118.2, -8.0, "CA");
    public static final String LUCKNOW = reg("Lucknow", 26.8, 81.0, 5.5, "India");
    public static final String LUDHIANA = reg("Ludhiana", 30.9, 75.9, 5.5, "India");
    public static final String LYON = reg("Lyon", 45.8, 4.8, 1.0, "France");
    public static final String MADRID = reg("Madrid", 40.4, -3.7, 1.0, "Spain");
    public static final String MADURAI = reg("Madurai", 9.9, 78.1, 5.5, "India");
    public static final String MANCHESTER = reg("Manchester", 53.5, -2.2, 0.0, "UK");
    public static final String MANGALORE = reg("Mangalore", 12.9, 74.9, 5.5, "India");
    public static final String MANILA = reg("Manila", 14.6, 121.0, 8.0, "Philippines");
    public static final String MATHURA = reg("Mathura", 27.5, 77.7, 5.5, "India");
    public static final String MEDELLIN = reg("Medellín", 6.2, -75.6, -5.0, "Colombia");
    public static final String MEERUT = reg("Meerut", 29.0, 77.7, 5.5, "India");
    public static final String MELBOURNE = reg("Melbourne", -37.8, 145.0, 10.0, "Australia");
    public static final String MEXICO_CITY = reg("Mexico City", 19.4, -99.1, -6.0, "Mexico");
    public static final String MIAMI = reg("Miami", 25.8, -80.2, -5.0, "FL");
    public static final String MILAN = reg("Milan", 45.5, 9.2, 1.0, "Italy");
    public static final String MINNEAPOLIS = reg("Minneapolis", 44.9, -93.3, -6.0, "MN");
    public static final String MISSISSAUGA = reg("Mississauga", 43.6, -79.7, -5.0, "ON");
    public static final String MOMBASA = reg("Mombasa", -4.1, 39.7, 3.0, "Kenya");
    public static final String MONTREAL = reg("Montreal", 45.5, -73.6, -5.0, "QC");
    public static final String MOSCOW = reg("Moscow", 55.8, 37.6, 3.0, "Russia");
    public static final String MUMBAI = reg("Mumbai", 19.1, 72.9, 5.5, "India");
    public static final String MUNICH = reg("Munich", 48.1, 11.6, 1.0, "Germany");
    public static final String MUSCAT = reg("Muscat", 23.6, 58.5, 4.0, "Oman");
    public static final String MUSSOORIE = reg("Mussoorie", 30.5, 78.1, 5.5, "India");
    public static final String MYSORE = reg("Mysore", 12.3, 76.7, 5.5, "India");
    public static final String NAGPUR = reg("Nagpur", 21.1, 79.1, 5.5, "India");
    public static final String NAIROBI = reg("Nairobi", -1.3, 36.8, 3.0, "Kenya");
    public static final String NAPLES = reg("Naples", 40.8, 14.3, 1.0, "Italy");
    public static final String NASHIK = reg("Nashik", 20.0, 73.8, 5.5, "India");
    public static final String NASHVILLE = reg("Nashville", 36.2, -86.8, -6.0, "TN");
    public static final String NEW_YORK = reg("New York", 40.7, -74.0, -5.0, "NY");
    public static final String NOIDA = reg("Noida", 28.6, 77.3, 5.5, "India");
    public static final String ORLANDO = reg("Orlando", 28.5, -81.4, -5.0, "FL");
    public static final String OSAKA = reg("Osaka", 34.7, 135.5, 9.0, "Japan");
    public static final String OSLO = reg("Oslo", 59.9, 10.8, 1.0, "Norway");
    public static final String OTTAWA = reg("Ottawa", 45.4, -75.7, -5.0, "ON");
    public static final String PANAMA_CITY = reg("Panama City", 9.0, -79.5, -5.0, "Panama");
    public static final String PARAMARIBO = reg("Paramaribo", 5.9, -55.2, -3.0, "Suriname");
    public static final String PARIS = reg("Paris", 48.9, 2.3, 1.0, "France");
    public static final String PATNA = reg("Patna", 25.6, 85.1, 5.5, "India");
    public static final String PERTH = reg("Perth", -31.9, 115.9, 8.0, "Australia");
    public static final String PHILADELPHIA = reg("Philadelphia", 40.0, -75.2, -5.0, "PA");
    public static final String PHNOM_PENH = reg("Phnom Penh", 11.6, 104.9, 7.0, "Cambodia");
    public static final String PHOENIX = reg("Phoenix", 33.4, -112.1, -7.0, "AZ");
    public static final String PITTSBURGH = reg("Pittsburgh", 40.4, -80.0, -5.0, "PA");
    public static final String PORT_LOUIS = reg("Port Louis", -20.2, 57.5, 4.0, "Mauritius");
    public static final String PORT_OF_SPAIN = reg("Port of Spain", 10.7, -61.5, -4.0, "Trinidad");
    public static final String PORTLAND = reg("Portland", 45.5, -122.7, -8.0, "OR");
    public static final String PORTO = reg("Porto", 41.2, -8.6, 0.0, "Portugal");
    public static final String PRAGUE = reg("Prague", 50.1, 14.4, 1.0, "Czechia");
    public static final String PRAYAGRAJ = reg("Prayagraj", 25.4, 81.8, 5.5, "India");
    public static final String PUNE = reg("Pune", 18.5, 73.9, 5.5, "India");
    public static final String QUITO = reg("Quito", -0.2, -78.5, -5.0, "Ecuador");
    public static final String RAIPUR = reg("Raipur", 21.3, 81.6, 5.5, "India");
    public static final String RAJKOT = reg("Rajkot", 22.3, 70.8, 5.5, "India");
    public static final String RALEIGH = reg("Raleigh", 35.8, -78.6, -5.0, "NC");
    public static final String RANCHI = reg("Ranchi", 23.3, 85.3, 5.5, "India");
    public static final String REDMOND = reg("Redmond", 47.7, -122.1, -8.0, "WA");
    public static final String REGINA = reg("Regina", 50.5, -104.6, -6.0, "SK");
    public static final String RIO_DE_JANEIRO = reg("Rio de Janeiro", -22.9, -43.2, -3.0, "Brazil");
    public static final String RISHIKESH = reg("Rishikesh", 30.1, 78.3, 5.5, "India");
    public static final String RIYADH = reg("Riyadh", 24.7, 46.7, 3.0, "Saudi Arabia");
    public static final String ROME = reg("Rome", 41.9, 12.5, 1.0, "Italy");
    public static final String ROTTERDAM = reg("Rotterdam", 51.9, 4.5, 1.0, "Netherlands");
    public static final String SACRAMENTO = reg("Sacramento", 38.6, -121.5, -8.0, "CA");
    public static final String SALEM = reg("Salem", 11.7, 78.2, 5.5, "India");
    public static final String SALT_LAKE_CITY = reg("Salt Lake City", 40.8, -111.9, -7.0, "UT");
    public static final String SAN_DIEGO = reg("San Diego", 32.7, -117.2, -8.0, "CA");
    public static final String SAN_FRANCISCO = reg("San Francisco", 37.8, -122.4, -8.0, "CA");
    public static final String SAN_JOSE = reg("San Jose", 37.3, -121.9, -8.0, "CA");
    public static final String SANTIAGO = reg("Santiago", -33.4, -70.6, -4.0, "Chile");
    public static final String SEATTLE = reg("Seattle", 47.6, -122.3, -8.0, "WA");
    public static final String SEOUL = reg("Seoul", 37.6, 127.0, 9.0, "South Korea");
    public static final String SHANGHAI = reg("Shanghai", 31.2, 121.5, 8.0, "China");
    public static final String SHIRDI = reg("Shirdi", 19.8, 74.5, 5.5, "India");
    public static final String SINGAPORE = reg("Singapore", 1.4, 103.8, 8.0);
    public static final String SOFIA = reg("Sofia", 42.7, 23.3, 2.0, "Bulgaria");
    public static final String SRINAGAR = reg("Srinagar", 34.1, 74.8, 5.5, "India");
    public static final String ST_LOUIS = reg("St. Louis", 38.6, -90.2, -6.0, "MO");
    public static final String STOCKHOLM = reg("Stockholm", 59.3, 18.1, 1.0, "Sweden");
    public static final String SURAT = reg("Surat", 21.2, 72.8, 5.5, "India");
    public static final String SURREY = reg("Surrey", 49.2, -122.8, -8.0, "BC");
    public static final String SUVA = reg("Suva", -18.1, 178.4, 12.0, "Fiji");
    public static final String SYDNEY = reg("Sydney", -33.9, 151.2, 10.0, "Australia");
    public static final String SAO_PAULO = reg("São Paulo", -23.5, -46.6, -3.0, "Brazil");
    public static final String TAIPEI = reg("Taipei", 25.0, 121.5, 8.0, "Taiwan");
    public static final String TAMPA = reg("Tampa", 27.9, -82.5, -5.0, "FL");
    public static final String TASHKENT = reg("Tashkent", 41.3, 69.3, 5.0, "Uzbekistan");
    public static final String TEL_AVIV = reg("Tel Aviv", 32.1, 34.8, 2.0, "Israel");
    public static final String THANE = reg("Thane", 19.2, 73.0, 5.5, "India");
    public static final String THESSALONIKI = reg("Thessaloniki", 40.6, 22.9, 2.0, "Greece");
    public static final String THIRUVANANTHAPURAM = reg("Thiruvananthapuram", 8.5, 76.9, 5.5, "India");
    public static final String TIRUCHIRAPPALLI = reg("Tiruchirappalli", 10.8, 78.7, 5.5, "India");
    public static final String TIRUPATI = reg("Tirupati", 13.6, 79.4, 5.5, "India");
    public static final String TOKYO = reg("Tokyo", 35.7, 139.7, 9.0, "Japan");
    public static final String TORONTO = reg("Toronto", 43.7, -79.4, -5.0, "ON");
    public static final String UDAIPUR = reg("Udaipur", 24.6, 73.7, 5.5, "India");
    public static final String UJJAIN = reg("Ujjain", 23.2, 75.8, 5.5, "India");
    public static final String VADODARA = reg("Vadodara", 22.3, 73.2, 5.5, "India");
    public static final String VANCOUVER = reg("Vancouver", 49.3, -123.1, -8.0, "BC");
    public static final String VARANASI = reg("Varanasi", 25.3, 83.0, 5.5, "India");
    public static final String VIENNA = reg("Vienna", 48.2, 16.4, 1.0, "Austria");
    public static final String VIJAYAWADA = reg("Vijayawada", 16.5, 80.6, 5.5, "India");
    public static final String VISAKHAPATNAM = reg("Visakhapatnam", 17.7, 83.3, 5.5, "India");
    public static final String VRINDAVAN = reg("Vrindavan", 27.6, 77.7, 5.5, "India");
    public static final String WARANGAL = reg("Warangal", 18.0, 79.6, 5.5, "India");
    public static final String WARSAW = reg("Warsaw", 52.2, 21.0, 1.0, "Poland");
    public static final String WASHINGTON_DC = reg("Washington DC", 38.9, -77.0, -5.0);
    public static final String WELLINGTON = reg("Wellington", -41.3, 174.8, 12.0, "New Zealand");
    public static final String WINNIPEG = reg("Winnipeg", 49.9, -97.1, -6.0, "MB");
    public static final String YANGON = reg("Yangon", 16.9, 96.2, 6.5, "Myanmar");
    public static final String ZAGREB = reg("Zagreb", 45.8, 16.0, 1.0, "Croatia");
    public static final String ZURICH = reg("Zurich", 47.4, 8.5, 1.0, "Switzerland");

    /** Default reference city used wherever none is specified. */
    public static final String DEFAULT = UJJAIN;

    /**
     * City names whose bare form is commonly confused with another well-known
     * place; only these receive a qualifier from {@link #displayName}.
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

    // ─── Registry methods ───

    /**
     * Look up coordinates for a city name.
     * @param city registered city name (e.g. {@code City.SEATTLE})
     * @return location data, or {@link #DEFAULT} location if not found
     */
    /**
     * Location for a {@code city} (case/space-insensitive, or {@code "City, Region"}).
     *
     * @throws IllegalArgumentException if the city is not supported. The engine
     *     never silently substitutes a different location, because a wrong location
     *     yields wrong sunrise-based tithis and festival dates. Use
     *     {@link #resolveName} (returns {@code null}) or {@link #supported()} to
     *     validate input first.
     */
    public static CityLocation getLocation(String city) {
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
    // The canonical spatial key is the 0.1° cell (cities are stored at 1 decimal,
    // ~11 km). A lat/long that rounds into a supported city's cell reuses that
    // city wholesale (coords + Swiss corrections); points outside every city's
    // cell are addressed as "ad-hoc" locations through the normal name-keyed
    // pipeline (Meeus-only — no correction table). See design doc.
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
    public static CityLocation adHocLocation(String key) {
        return AD_HOC.get(key);
    }

    /**
     * Fully-qualified label: appends the region/country for <b>every</b> city
     * that has one (e.g. {@code "Seattle"} &rarr; {@code "Seattle, WA"},
     * {@code "Tokyo"} &rarr; {@code "Tokyo, Japan"}). Use in pickers/search
     * lists. Returns the bare name for self-qualifying cities (e.g.
     * {@code "Singapore"}) and unknown names.
     */
    public static String qualifiedName(String city) {
        CityLocation loc = LOCATIONS.get(city);
        String region = loc != null ? loc.getRegion() : null;
        return region == null ? city : city + ", " + region;
    }

    /**
     * Compact label: the bare city name, with a region qualifier appended
     * <b>only</b> for commonly-confused names (e.g. {@code "Redmond"} &rarr;
     * {@code "Redmond, WA"}; {@code "Delhi"} &rarr; {@code "Delhi"}). Use in
     * headers/confirmation UI. The selective subset of {@link #qualifiedName}.
     */
    public static String displayName(String city) {
        if (!AMBIGUOUS.contains(city)) return city;
        return qualifiedName(city);
    }

    private static String reg(String name, double lat, double lon, double utcOffset) {
        LOCATIONS.put(name, new CityLocation(lat, lon, utcOffset));
        RESOLVE.put(canon(name), name);
        return name;
    }

    private static String reg(String name, double lat, double lon, double utcOffset, String region) {
        LOCATIONS.put(name, new CityLocation(lat, lon, utcOffset, region));
        RESOLVE.put(canon(name), name);
        RESOLVE.put(canon(name + ", " + region), name);
        return name;
    }
}
