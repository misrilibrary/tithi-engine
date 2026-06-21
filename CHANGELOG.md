# Changelog

All notable changes to this project will be documented in this file.

## [1.1.0] — 2026-06-21

### Engine accuracy overhaul (no public API change)

Output is unchanged for every supported city and every day 1900–2100 — the
per-city tables still resolve to the Swiss-Ephemeris truth. Behaviour improves
only for dates/cities outside the tabled set, which now use a much more accurate
astronomical fallback. This brings the Java engine to parity, math-for-math,
with the Dart `tithi-engine-dart` engine.

- Evaluate the Meeus Sun/Moon series in Terrestrial Time via a pure-Java
  Espenak–Meeus delta-T (UT → TT); fixes the dominant, time-growing error.
- Replace the low-accuracy Meeus Ch.25 Sun with a truncated VSOP87 series
  (mean ~1.5", max ~6.6" vs Swiss Ephemeris over 1900–2100).
- Moon longitude now carries the same nutation term as the Sun, so nutation
  cancels in the Moon–Sun elongation (tithi) and the Moon stays apparent.

### Data

- All 230 per-city correction tables regenerated against the improved engine,
  using the Dart Swiss-validated daily truth as ground truth and generating a
  Java correction only where Java's own Meeus disagrees with that truth.
- Verified by triangulation: Java (engine + regenerated tables) reproduces the
  Swiss truth on every sunrise tithi, transition, Purnima/Amavasya month
  boundary, and kshaya across all 230 cities × 73,414 days (0 mismatches), and
  the regenerated tables are byte-identical to the Dart tables for all 230
  cities (0 correction-set divergence — the engines agree exactly).
- Fewer corrections needed (more accurate engine): correction entries
  ≈30,700 → 12,834 (tithi corrections 14,850 → 6,215).
- Fixed the `medellín.json` resource name to match the city's display name.

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
