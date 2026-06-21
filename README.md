# tithi-engine

[![CI](https://github.com/misrilibrary/tithi-engine/actions/workflows/ci.yml/badge.svg)](https://github.com/misrilibrary/tithi-engine/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A pure Java library for Hindu lunar calendar (tithi/panchang) calculations. Computes accurate tithis, lunar months, and festival dates for any city worldwide.

## Features

- **Tithi calculation** — date → tithi number, name, paksha, lunar month
- **Festival dates** — muhurta-accurate (nishita, madhyahna, pradosh rules)
- **Month resolution** — moment-based adhika/kshaya detection, Purnimant & Amant systems
- **Date finding** — tithi → Gregorian date in any year
- **230 cities** — per-city correction tables verified against Swiss Ephemeris
- **Pure Java 17** — no external dependencies, works on Android/server/desktop
- **200-year accuracy** — validated 1900–2100 against Drik Panchang

## Installation

**Gradle (Kotlin DSL):**
```kotlin
implementation("io.github.misrilibrary:tithi-engine:1.1.0")
```

**Gradle (Groovy):**
```groovy
implementation 'io.github.misrilibrary:tithi-engine:1.1.0'
```

**Maven:**
```xml
<dependency>
    <groupId>io.github.misrilibrary</groupId>
    <artifactId>tithi-engine</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Quick Start

```java
import com.misrilibrary.tithi.*;
import com.misrilibrary.tithi.model.*;
import java.time.LocalDate;

Panchang panchang = new Panchang(MonthSystem.PURNIMANT);

// Date → Tithi
TithiInfo info = panchang.forDate(LocalDate.of(2026, 2, 15), City.UJJAIN);
System.out.println(info); // "Phalguna Krishna Trayodashi"

// Festival date
LocalDate shivaratri = panchang.dateFor(Festival.MAHA_SHIVARATRI, 2026, City.UJJAIN);
System.out.println("Maha Shivaratri 2026: " + shivaratri); // 2026-02-15

// Tithi → Date
LocalDate date = panchang.getDate(LunarMonth.BHADRAPADA, Paksha.KRISHNA, 8, 2026, City.SEATTLE);
System.out.println("Janmashtami 2026 Seattle: " + date);
```

## API

| Method | Description |
|--------|-------------|
| `panchang.forDate(date, city)` | Gregorian date → full TithiInfo |
| `panchang.getDate(month, paksha, tithi, year, city)` | Tithi spec → Gregorian date |
| `panchang.getDates(month, paksha, tithi, year, city)` | Tithi spec → all dates (adhika-aware) |
| `panchang.dateFor(festival, year, city)` | Festival → date with muhurta rules |

## Supported Festivals

| Festival | Muhurta Rule |
|----------|-------------|
| Maha Shivaratri | Nishita (midnight) |
| Holika Dahan | Pradosh (evening) |
| Ram Navami | Madhyahna (midday) |
| Akshaya Tritiya | Madhyahna (midday) |
| Guru Purnima | Sunrise |
| Raksha Bandhan | Sunrise |
| Janmashtami (Smarta) | Nishita (midnight) |
| Janmashtami (ISKCON) | Sunrise |
| Ganesh Chaturthi | Sunrise |
| Vijayadashami | Sunrise |
| Diwali / Lakshmi Puja | Pradosh (evening) |

## Extensibility

### Adding a Festival

1. Open `Festival.java`
2. Add a constant:
   ```java
   public static final Festival KARVA_CHAUTH = new Festival(
       "karva_chauth", "Karva Chauth",
       LunarMonth.KARTIKA, Paksha.KRISHNA, 4, MuhurtaRule.PRADOSH);
   ```
3. Add it to the `ALL` list (same file, bottom):
   ```java
   private static final List<Festival> ALL = List.of(
       ...,
       KARVA_CHAUTH  // ← add here
   );
   ```
4. Run `./gradlew test` — `ExtensibilityGuardTest` will fail if you forget step 3.

### Adding a City

One file: `City.java`. Add a single line using `reg()`:

```java
public static final String RISHIKESH = reg("Rishikesh", 30.1, 78.3, 5.5);
```

This simultaneously creates the constant and registers the city with coordinates. That's it.

*(Optional)* Add a `src/main/resources/corrections/rishikesh.json` for Swiss Ephemeris-level accuracy. Without it, Meeus fallback gives ~99.9%. See the [Dart companion repo](https://github.com/misrilibrary/tithi-engine-dart) for the correction generation tooling.

Run `./gradlew test` — `ExtensibilityGuardTest` will fail if you declare a constant without using `reg()`.

### Adding a Muhurta Rule

1. Add to the `MuhurtaRule` enum
2. Handle the new case in `Panchang.muhurtaUtc()` switch statement

## Architecture

```
src/main/java/com/misrilibrary/tithi/
├── Panchang.java             ← Public API (single entry point)
├── Festival.java             ← Festival definitions + registry
├── City.java                 ← City constants + registry (single file)
├── Astronomy.java            ← VSOP87 Sun + Meeus Moon (TT/ΔT), sunrise/sunset
├── LunarMonthResolver.java   ← Month naming (adhika/kshaya/double Purnima)
├── TithiFinder.java          ← Internal: find tithi date in year
├── TithiUtils.java           ← Names, paksha helpers
├── MonthConverter.java       ← Purnimant ↔ Amant
├── model/                    ← TithiInfo, CityLocation, enums
└── data/
    └── CityCorrections.java  ← Lazy JSON correction loader

src/main/resources/corrections/
├── ujjain.json               ← Swiss-verified correction table
├── srinagar.json
├── seattle.json
└── ... (230 cities)
```

## Accuracy

| Metric | Value |
|--------|-------|
| Tithi vs Swiss Ephemeris | 0 mismatches / 73,414 days × 230 cities (1900–2100) |
| Month boundaries (Purnimant) | 100% (200 years, verified cities) |
| Festival dates vs Drik Panchang | 22/22 (2025–2026) |
| Meeus fallback (no correction table) | ~99.9% |
| Test coverage | 123 tests, 98%+ line, 86%+ branch |

> **Note:** The Maven group ID is `io.github.misrilibrary` but the Java package is `com.misrilibrary.tithi`. This is intentional and standard practice — the two don't need to match.

## Cross-platform parity

This is the Java implementation of [tithi-engine-dart](https://github.com/misrilibrary/tithi-engine-dart) (Dart). Both compute identical tithi/panchang results, validated against the Swiss Ephemeris.

The two packages **version independently** — each version string is a semver compatibility contract for *that* ecosystem. What stays locked in step is the **astronomy engine revision** (the correctness-critical part) and the feature parity tracked below.

- **Engine revision:** `r2` — VSOP87 Sun + Meeus Moon in Terrestrial Time (Espenak–Meeus ΔT), nutation cancelled in the Moon–Sun elongation. Java `1.1.0+` ⟷ Dart `2.1.0+`. Verified: regenerated tables byte-identical across both, 0 mismatches over 230 cities × 73,414 days.

| Capability | Java (tithi-engine) | Dart (tithi_engine) |
|---|---|---|
| Astronomy engine rev `r2` (VSOP87/TT) | `1.1.0+` | `2.1.0+` |
| 230 cities, Swiss-verified tables | `1.1.0+` | `2.1.0+` |
| City display-name disambiguation (`region` / `qualifiedName` / `displayName`) | _planned_ | `2.2.0+` |
| Time-aware API (`tithiOnDate` / `tithiAtInstant` / `tithiSegments`) | _planned_ | `3.0.0+` |
| `recurringDates` / `findNext` / `TithiInfo.fromStored` | _planned_ | `2.0.0+` |

## Building

```bash
./gradlew build    # compile + test + SpotBugs + JaCoCo + JARs
./gradlew test     # tests only
```

## License

Licensed under the [Apache License 2.0](LICENSE).
