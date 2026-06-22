# Changelog

All notable changes to this project will be documented in this file.

## [2.1.0] — 2026-06-21

### Added
- Recurring festival **Shukla Ashtami** (`masik_shukla_ashtami`) — monthly bright-fortnight Ashtami.
- 11 curated festivals from the Kashmiri jantri (Samvat 2082): Navreh, Thal Buth
  Vuchun, Zang Trayi, Durga Ashtami, Nirjala Ekadashi, Zyeth Ashtami, Haar
  Ashtami, Navratri (Sharad) Begins, Maha Navami, Karva Chauth, Bhai Dooj, Khichdi Amavasya, Gauri Tritiya, Kaav
  Punim (Magh Purnima), Huri Aukdoh, Huri Ashtami.
  Teil Ashtami, Holi, Sonth, Thal Barun (Navreh). **39 built-in festivals total.**

### Fixed
- Festival/date finder no longer mis-attributes a *previous* month's kshaya Purnima/Amavasya to the next month's span (e.g. `getDates(Pausha, Shukla, 15)` wrongly returning Margashirsha purnima dates). Boundary-kshaya now applies only to paksha-leading tithis (Pratipada).

### Changed
- **Nishita Kaal muhurta window** now uses the precise classical definition —
  the **8th of the night's 15 muhurtas** (the central muhurta) — instead of the
  coarse "third quarter of the night." Displayed Nishita windows now match Drik
  Panchang (e.g. Janmashtami Smarta for Seattle/Redmond 2026: 12:47–01:30 AM).
  Festival **dates are unchanged** — the day-attribution moment is still the
  night midpoint, which sits at the centre of this muhurta. Engine rev unchanged (`r2`).

## [2.0.0] — 2026-06-21

Full API parity with the Dart `tithi-engine-dart` 3.x surface. **Breaking** —
the time-of-day API is now UTC-instant based, mirroring the Dart redesign.

### Breaking changes
- **`forDate(LocalDate, city)` is removed**, replaced by
  `tithiOnDate(LocalDate, city)` — the sunrise tithi of the panchang day
  (calendar/observance display). Pure rename; same result.
- **`dateFor(...)` now returns a `FestivalDate`** (observance date + tithi
  start/end + muhurta window), not a bare `LocalDate`. Use `.getDate()` for the
  date.
- **`getDate(...)` returns `null`** when no date matches (e.g. a kshaya tithi),
  instead of throwing `NoSuchElementException`.

### Added
- **Time-aware API** (UTC-instant based — the caller resolves the DST-aware
  offset; the library does no timezone work):
  - `tithiAtInstant(Instant utcInstant, city, ZoneOffset offset)` — tithi at an
    exact moment (birth-time precision).
  - `tithiSegments(Instant startUtc, Instant endUtc, city, ZoneOffset offset)`
    → `List<TithiSegment>` — every tithi transition in a window (N → N+1
    segments), each carrying its own resolved `TithiInfo` and bounding instants.
  - `TithiSegment` value type.
- `findNext(month, paksha, tithi, city, from)` — next occurrence of a tithi.
- `recurringDates(festival, year, city)` → `List<FestivalDate>` for recurring
  (monthly) tithis.
- `TithiInfo.fromStored(...)` — render a saved tithi, with optional
  Purnimant↔Amant month-name conversion.
- **City display-name disambiguation:** `CityLocation.region`,
  `City.qualifiedName(name)` (always-qualified), `City.displayName(name)`
  (qualifies only the 14 commonly-confused names).
- **All 230 cities now registered with coordinates + region** (previously 178
  had coordinates; the other 52 fell back to the default city for sunrise).
- **Festival parity:** `FestivalTradition` enum, `recurring`/`enabledByDefault`
  flags, Kashmiri variants (Herath, Zarmasatam), and 5 recurring `masik_*`
  festivals — 18 built-in festivals total.

### Notes
- Numerical output (tithi/month/festival dates) is unchanged from 1.1.0; this
  release is an API surface change. Engine revision is still `r2`.

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
