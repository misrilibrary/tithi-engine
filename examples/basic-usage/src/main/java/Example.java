import com.misrilibrary.tithi.*;
import com.misrilibrary.tithi.model.*;
import java.time.LocalDate;

/**
 * Basic usage example for tithi-engine.
 *
 * Run: ./gradlew run
 * (Requires tithi-engine published to mavenLocal — run `./gradlew publishToMavenLocal` in the root project first)
 */
public class Example {
    public static void main(String[] args) {
        Panchang panchang = new Panchang(MonthSystem.PURNIMANT);

        // 1. Date → Tithi
        LocalDate today = LocalDate.now();
        TithiInfo info = panchang.forDate(today, City.UJJAIN);
        System.out.println("Today (" + today + ") in Ujjain: " + info);

        // 2. Festival date
        LocalDate shivaratri = panchang.dateFor(Festival.MAHA_SHIVARATRI, 2026, City.UJJAIN);
        System.out.println("Maha Shivaratri 2026 (Ujjain): " + shivaratri);

        LocalDate diwali = panchang.dateFor(Festival.DIWALI, 2026, City.SEATTLE);
        System.out.println("Diwali 2026 (Seattle): " + diwali);

        // 3. Tithi → Date
        LocalDate janmashtami = panchang.getDate(LunarMonth.BHADRAPADA, Paksha.KRISHNA, 8, 2026, City.SEATTLE);
        System.out.println("Janmashtami 2026 (Seattle): " + janmashtami);

        // 4. List all supported cities
        System.out.println("\nSupported cities (" + City.supported().size() + "): ");
        City.supported().stream().sorted().limit(10).forEach(c -> System.out.print(c + ", "));
        System.out.println("...");

        // 5. All festivals
        System.out.println("\nFestivals 2026 (Ujjain):");
        for (Festival f : Festival.all()) {
            LocalDate date = panchang.dateFor(f, 2026, City.UJJAIN);
            System.out.printf("  %-25s %s%n", f.name, date);
        }
    }
}
