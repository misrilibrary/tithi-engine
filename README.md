# tithi-engine

A pure Java library for Hindu lunar calendar (tithi/panchang) calculations. Computes accurate tithis, lunar months, and festival dates for any city worldwide.

## Features

- **Tithi calculation** — date → tithi number, name, paksha, lunar month
- **Festival dates** — muhurta-accurate (nishita, madhyahna, pradosh rules)
- **Month resolution** — moment-based adhika/kshaya detection, Purnimant & Amant systems
- **Date finding** — tithi → Gregorian date in any year
- **109 cities** — per-city correction tables verified against Swiss Ephemeris
- **Pure Java 17** — no external dependencies, works on Android/server/desktop
- **200-year accuracy** — validated 1900–2100 against Drik Panchang

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

## Architecture

```
src/main/java/com/misrilibrary/tithi/
├── Panchang.java             ← Public API (single entry point)
├── Festival.java             ← Festival definitions + registry
├── City.java                 ← City name constants
├── Astronomy.java            ← Meeus Sun/Moon, sunrise/sunset
├── LunarMonthResolver.java   ← Month naming (adhika/kshaya/double Purnima)
├── TithiFinder.java          ← Internal: find tithi date in year
├── TithiUtils.java           ← Names, paksha helpers
├── MonthConverter.java       ← Purnimant ↔ Amant
├── model/                    ← TithiInfo, CityLocation, enums
└── data/
    ├── Cities.java           ← City registry (109 cities)
    └── CityCorrections.java  ← Lazy JSON correction loader

src/main/resources/corrections/
├── ujjain.json               ← Swiss-verified correction table
├── srinagar.json
├── seattle.json
└── ... (109 cities)
```

## Accuracy

| Metric | Value |
|--------|-------|
| Tithi vs Swiss Ephemeris | 0 mismatches / 73,049 days (1900–2100) |
| Month boundaries (Purnimant) | 100% (200 years, verified cities) |
| Festival dates vs Drik Panchang | 22/22 (2025–2026) |
| Meeus fallback (no correction table) | ~99.9% |

## Building

```bash
gradle build
gradle test
```

## License

Copyright © 2026 Misri Library. All rights reserved.
