# tithi-engine

A pure Java library for Hindu lunar calendar (tithi/panchang) calculations. Computes accurate tithis, lunar months, and festival dates for any city worldwide.

## Features

- **Tithi calculation** — date → tithi number, name, paksha, lunar month
- **Festival dates** — muhurta-accurate (nishita, madhyahna, pradosh rules)
- **Month resolution** — moment-based adhika/kshaya detection, Purnimant & Amant systems
- **Date finding** — tithi → Gregorian date in any year
- **109 cities** — per-city correction tables verified against Swiss Ephemeris
- **Pure Java 11** — no external dependencies, works on Android/server/desktop
- **200-year accuracy** — validated 1900–2100 against Drik Panchang

## Quick Start

```java
import com.misrilibrary.tithi.*;
import com.misrilibrary.tithi.model.*;
import java.time.LocalDate;

// Get tithi for a date
TithiCalculator calc = new TithiCalculator(MonthSystem.PURNIMANT);
TithiInfo info = calc.getTithi(LocalDate.of(2026, 2, 15), "Ujjain");
System.out.println(info); // "Phalguna Krishna Trayodashi"

// Find festival date
FestivalFinder ff = new FestivalFinder("Ujjain");
LocalDate shivaratri = ff.findDate(FestivalDef.ALL.get(0), 2026, "Ujjain");
System.out.println("Maha Shivaratri 2026: " + shivaratri); // 2026-02-15

// Find when a tithi falls in a year
TithiFinder finder = new TithiFinder(MonthSystem.PURNIMANT, "Seattle");
List<LocalDate> dates = finder.findInYear(
    LunarMonth.BHADRAPADA, Paksha.KRISHNA, 8, 2026, false);
System.out.println("Janmashtami 2026 Seattle: " + dates.get(0));
```

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
├── TithiCalculator.java      ← Public API
├── Astronomy.java            ← Meeus Sun/Moon, sunrise/sunset
├── LunarMonthResolver.java   ← Month naming (adhika/kshaya/double Purnima)
├── TithiFinder.java          ← Find tithi date in year
├── FestivalDef.java          ← Festival registry
├── FestivalFinder.java       ← Muhurta-aware date finding
├── TithiUtils.java           ← Names, paksha helpers
├── MonthConverter.java       ← Purnimant ↔ Amant
├── model/                    ← TithiInfo, CityLocation, enums
└── data/
    ├── Cities.java           ← City registry
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
