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
implementation("io.github.misrilibrary:tithi-engine:3.0.0")
```

**Gradle (Groovy):**
```groovy
implementation 'io.github.misrilibrary:tithi-engine:3.0.0'
```

**Maven:**
```xml
<dependency>
    <groupId>io.github.misrilibrary</groupId>
    <artifactId>tithi-engine</artifactId>
    <version>3.0.0</version>
</dependency>
```

## Quick Start

```java
import com.misrilibrary.tithi.*;
import com.misrilibrary.tithi.model.*;
import java.time.LocalDate;

Panchang panchang = new Panchang(MonthSystem.PURNIMANT);

// Date → Tithi (sunrise tithi of the panchang day; for display/observance)
TithiInfo info = panchang.tithiOnDate(LocalDate.of(2026, 2, 15), City.UJJAIN);
System.out.println(info); // "Phalguna Krishna Trayodashi"

// Festival date (returns a FestivalDate: observance date + tithi span + muhurta window)
FestivalDate shivaratri = panchang.dateFor(Festival.MAHA_SHIVARATRI, 2026, City.UJJAIN);
System.out.println("Maha Shivaratri 2026: " + shivaratri.getDate()); // 2026-02-15

// Tithi → Date
LocalDate date = panchang.getDate(LunarMonth.BHADRAPADA, Paksha.KRISHNA, 8, 2026, City.SEATTLE);
System.out.println("Janmashtami 2026 Seattle: " + date);

// Exact-moment (birth-time) tithi: pass a true UTC instant + the DST-aware offset in effect
import java.time.*;
ZoneOffset cdt = ZoneOffset.ofHours(-5);
Instant birth = LocalDateTime.of(2006, 5, 30, 20, 0).toInstant(cdt); // 8 PM CDT
TithiInfo birthTithi = panchang.tithiAtInstant(birth, "Austin", cdt);
```

## API

`Panchang` is the single entry point. The time-aware API is **UTC-instant based**:
pass true `Instant`s and, where a civil day matters, the DST-aware `ZoneOffset`
in effect (the library does no timezone resolution).

| Method | Description |
|--------|-------------|
| `panchang.tithiOnDate(date, city)` | Sunrise tithi of the panchang day (display/observance) |
| `panchang.tithiAtInstant(utcInstant, city, offset)` | Tithi at an exact UTC moment (birth-time) |
| `panchang.tithiSegments(startUtc, endUtc, city, offset)` | Every tithi segment in a UTC window (N transitions → N+1 segments) |
| `panchang.getDate(month, paksha, tithi, year, city)` | Tithi spec → Gregorian date (`null` if none) |
| `panchang.getDates(month, paksha, tithi, year, city)` | Tithi spec → all dates (adhika-aware) |
| `panchang.findNext(month, paksha, tithi, city, from)` | Next occurrence of a tithi from a date |
| `panchang.dateFor(festival, year, city)` | Festival → `FestivalDate` (date + tithi span + muhurta window) |
| `panchang.recurringDates(festival, year, city)` | Recurring festival → all occurrences in the year |
| `panchang.at(location)` | Bind to a `Location` (city name or raw `lat/lng`) → `PanchangAt` (same methods, no `city` arg) |
| `City.qualifiedName(name)` / `City.displayName(name)` | Display labels (always-qualified / selective) |
| `TithiInfo.fromStored(...)` | Render a saved tithi (optional Purnimant↔Amant display conversion) |

### City names

Every method takes a `city` name. Resolution is **case- and space-insensitive** and
accepts the qualified `"City, Region"` form, so all of these resolve to the same city:

```java
City.getLocation("New York");      // canonical
City.getLocation("new york");      // case-insensitive
City.getLocation("New York, NY");  // qualified form (as City.qualifiedName emits)
```

The canonical identity is the **(city, region)** pair: the bare name maps to the
*primary* city for that name, and the qualified `"City, Region"` form selects a
specific one when several share a name (e.g. a future `"Vancouver, WA"` vs
`"Vancouver, BC"`). There is **no fuzzy/region-stripping match** — a wrong region
(`"Vancouver, WA"` when only BC exists) is treated as unknown.

An **unsupported city throws `IllegalArgumentException`** — the engine never silently
substitutes another location, because a wrong location produces wrong sunrise-based
tithis and festival dates. To check without throwing, use `City.resolveName(name)`
(returns `null` if unsupported) or inspect `City.supported()`. Need a city added?
Open an issue: <https://github.com/misrilibrary/tithi-engine/issues>.

### By coordinates

Have raw `lat/lng` (a GPS fix, a map pin) instead of a name? Bind a `Location` with
`Panchang.at(...)`:

```java
Panchang panchang = new Panchang(MonthSystem.PURNIMANT);

// A point that rounds into a supported city's 0.1° cell reuses that city
// wholesale (Swiss-corrected). offset is optional here.
PanchangAt here = panchang.at(Location.at(47.61, -122.33));
TithiInfo info = here.tithiOnDate(LocalDate.of(2026, 2, 15));
here.source();   // LocationSource.CITY_CORRECTED

// A point outside every city's cell is Meeus-only (~99.97%) and REQUIRES the
// DST-aware UTC offset (used to frame the civil day).
PanchangAt remote = panchang.at(Location.at(0.0, -140.0, Duration.ofHours(-9)));
remote.source(); // LocationSource.MEEUS_RAW

// Location.city(name) is the named-city form of the same binding.
panchang.at(Location.city("Seattle")).tithiOnDate(LocalDate.of(2026, 2, 15));
```

`PanchangAt` exposes the same read methods as `Panchang` minus the `city` argument
(`tithiOnDate`, `tithiAtInstant`, `tithiSegments`, `getDate(s)`, `findNext`, `dateFor`,
`recurringDates`). Cities are stored at ~0.1° (~11 km), so any point within a city's cell
is treated as that city; see `City.cityForCell(lat,lng)`.

> **Recommendation:** prefer `Location.city(name)` (or a coordinate that lands in a
> supported city's cell) when you can. A named city carries Swiss‑Ephemeris correction
> tables — guaranteed accuracy. Off‑grid coordinates are Meeus‑only (~99.97% on
> day‑assignment): excellent, but the rare knife‑edge days a city's correction would fix
> are not covered. Use raw coordinates only when no nearby supported city exists.

## Supported Festivals

| Festival | Muhurta Rule | Tradition |
|----------|-------------|-----------|
| Herath (Maha Shivaratri, Kashmiri) | Nishita (midnight) | Kashmiri |
| Maha Shivaratri | Nishita (midnight) | General |
| Holika Dahan | Pradosh (evening) | General |
| Ram Navami | Madhyahna (midday) | General |
| Akshaya Tritiya | Madhyahna (midday) | General |
| Guru Purnima | Sunrise | General |
| Raksha Bandhan | Sunrise | General |
| Zarmasatam (Janmashtami, Kashmiri) | Nishita (midnight) | Kashmiri |
| Janmashtami (Smarta) | Nishita (midnight) | Smarta |
| Janmashtami (ISKCON) | Sunrise | Vaishnava |
| Ganesh Chaturthi | Sunrise | General |
| Vijayadashami | Sunrise | General |
| Diwali / Lakshmi Puja | Pradosh (evening) | General |

Plus 6 **recurring** monthly tithis (`recurring = true`), enumerated via
`recurringDates(...)`: Krishna Ashtami, Shukla Ashtami, Krishna Ekadashi,
Shukla Ekadashi, Purnima, Amavasya.

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
| Test coverage | 140 tests, high line/branch coverage |

> **Note:** The Maven group ID is `io.github.misrilibrary` but the Java package is `com.misrilibrary.tithi`. This is intentional and standard practice — the two don't need to match.

## Cross-platform parity

This is the Java implementation of [tithi-engine-dart](https://github.com/misrilibrary/tithi-engine-dart) (Dart). Both compute identical tithi/panchang results, validated against the Swiss Ephemeris.

The two packages **version independently** — each version string is a semver compatibility contract for *that* ecosystem. What stays locked in step is the **astronomy engine revision** (the correctness-critical part) and the feature parity tracked below.

- **Engine revision:** `r2` — VSOP87 Sun + Meeus Moon in Terrestrial Time (Espenak–Meeus ΔT), nutation cancelled in the Moon–Sun elongation. Java `1.1.0+` ⟷ Dart `2.1.0+`. Verified: regenerated tables byte-identical across both, 0 mismatches over 230 cities × 73,414 days.

| Capability | Java (tithi-engine) | Dart (tithi_engine) |
|---|---|---|
| Astronomy engine rev `r2` (VSOP87/TT) | `1.1.0+` | `2.1.0+` |
| 230 cities, Swiss-verified tables | `1.1.0+` | `2.1.0+` |
| City display-name disambiguation (`region` / `qualifiedName` / `displayName`) | `2.0.0+` | `2.2.0+` |
| Time-aware API (`tithiOnDate` / `tithiAtInstant` / `tithiSegments`) | `2.0.0+` | `3.0.0+` |
| `recurringDates` / `findNext` / `TithiInfo.fromStored` | `2.0.0+` | `2.0.0+` |
| Strict city resolution (`resolveName`, fail-fast on unknown, `"City, Region"` form) | `3.0.0+` | `4.0.0+` |
| Coordinate input (`Location` / `Panchang.at`, 0.1° cell reuse) | `3.0.0+` | `4.0.0+` |

> **API generation:** Java `2.0.0` reaches feature parity with Dart `3.x` (the
> time-aware, UTC-instant API). Java `3.0.0` ⟷ Dart `4.0.0` add strict city
> resolution (unknown cities now throw — a behavior break) and coordinate input
> (`Location` / `Panchang.at`). The version numbers differ because each is its
> own ecosystem's semver.

## Building

```bash
./gradlew build    # compile + test + SpotBugs + JaCoCo + JARs
./gradlew test     # tests only
```

## License

Licensed under the [Apache License 2.0](LICENSE).
