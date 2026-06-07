# Changelog

All notable changes to this project will be documented in this file.

## [1.0.9] — 2026-06-07

### Fixed
- Kshaya tithi detection at 30→1 wraparound (Shukla Pratipada skipped)
- Kshaya detection at month span start (e.g. Krishna Pratipada in Purnimant)
- findInYear/getDates now correctly returns the previous day for skipped tithis

## [1.0.7] — 2026-06-06

### Added
- Add 21 cities (230 total): Honolulu, Tampa, Pittsburgh, Columbus, Indianapolis, Kansas City, St. Louis, Sacramento, Halifax, Regina, Gothenburg, Lyon, Naples, Zagreb, Krakow, Thessaloniki, Porto, Rotterdam, Beirut, Ankara, Redmond

## [1.0.2] — 2026-06-05

### Added
- 52 new city correction tables (209 total cities worldwide)
- New regions: Pakistan, Caribbean, Africa, Europe, Americas, Central/SE Asia, Oceania

## [1.0.0] — 2026-06-04

### Added
- Core tithi calculation engine (date → tithi, tithi → date)
- 11 built-in festivals with muhurta-accurate date resolution
- 157 cities with Swiss Ephemeris-verified correction tables
- Lunar month resolution with adhika/kshaya detection
- Purnimant and Amant month system support
- Java module system support (module-info.java)
- 200-year accuracy validation (1900–2100)
- Extensibility guard tests for festivals and cities
- JaCoCo code coverage (98%+ line, 86%+ branch)
- SpotBugs static analysis (zero warnings)
- GitHub Actions CI
- Example project demonstrating all API features
