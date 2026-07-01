import com.misrilibrary.tithi.*;
import com.misrilibrary.tithi.model.*;
import java.time.LocalDate;
import java.util.Comparator;

/**
 * Basic usage example for tithi-engine.
 *
 * Run: ./gradlew run
 * (Requires tithi-engine published to mavenLocal — run `./gradlew publishToMavenLocal` in the root project first)
 */
public class Example {
    public static void main(String[] args) {
        Panchang panchang = new Panchang(MonthSystem.PURNIMANT);

        // 1. Date → Tithi (sunrise tithi of the panchang day)
        LocalDate today = LocalDate.now();
        TithiInfo info = panchang.tithiOnDate(today, City.UJJAIN);
        System.out.println("Today (" + today + ") in Ujjain: " + info);

        // 2. Festival date (dateFor returns a FestivalDate: date + tithi span + muhurta window)
        FestivalDate shivaratri = panchang.dateFor(Festival.MAHA_SHIVARATRI, 2026, City.UJJAIN);
        System.out.println("Maha Shivaratri 2026 (Ujjain): " + shivaratri.getDate());

        FestivalDate diwali = panchang.dateFor(Festival.DIWALI, 2026, City.SEATTLE);
        System.out.println("Diwali 2026 (Seattle): " + diwali.getDate());

        // 3. Tithi → Date (typed: LunarMonth + Tithi + City)
        LocalDate janmashtami = panchang.findDate(
            LunarMonth.BHADRAPADA, Tithi.krishna(8), 2026, City.SEATTLE);
        System.out.println("Janmashtami 2026 (Seattle): " + janmashtami);

        // 4. List all supported cities (typed City values, display-name qualified)
        System.out.println("\nSupported cities (" + City.values().size() + "): ");
        City.values().stream().sorted(Comparator.comparing(City::name)).limit(10)
            .forEach(c -> System.out.print(c.qualifiedName() + ", "));
        System.out.println("...");

        // 5. All festivals
        System.out.println("\nFestivals 2026 (Ujjain):");
        for (Festival f : Festival.all()) {
            if (f.recurring) continue; // recurring tithis use panchang.recurringDates(...)
            FestivalDate fd = panchang.dateFor(f, 2026, City.UJJAIN);
            System.out.printf("  %-25s %s%n", f.name, fd == null ? "—" : fd.getDate());
        }
    }
}
