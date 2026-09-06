# Changelog

All notable changes to this project will be documented in this file.

## [4.1.0] — 2026-08-19

Fixed observance rule for `JANMASHTAMI_KASHMIRI` (Zarma Satam), mirroring Dart
`5.2.0`. Changed `muhurta` from `MuhurtaRule.NISHITA` to `MuhurtaRule.SUNRISE`.
The festival is now assigned to whichever day holds Krishna Saptami (tithi 7)
at sunrise instead of checking for the tithi at midnight. This shifts the
computed date for some year/city combinations and means
`FestivalDate.muhurtaStart()`/`muhurtaEnd()` are now empty/null for this
festival. Not a breaking API change — only the returned date/muhurta values
for this specific festival id are affected.

## [4.0.0] — 2026-07-01

Breaking cutover to the typed value API (mirroring Dart `5.0.0`/`5.1.1`) **plus**
the engine-revision `r3` **DATA** cutover — full Swiss-Ephemeris (`.se1`) parity
with Dart `5.1.x`. The Meeus/VSOP87 astronomy is untouched.

### Added
- **`SunriseConvention` toggle** (`model.SunriseConvention`): `UPPER_LIMB`
  (−0.833°, default) / `CENTER_DISC` (−0.5667°), threaded through `Astronomy` and
  `Panchang`/`PanchangAt` (`Panchang(MonthSystem, SunriseConvention)`).
- **`Tithi` value type** (`model.Tithi`): `Tithi.shukla(1..15)`,
  `Tithi.krishna(1..15)`, `Tithi.ofNumber(1..30)`; `number()`/`name()`/
  `paksha()`/`dayInPaksha()`.
- **`City` typed value class**: the `City.*` constants are `City` instances (not
  `String`); `City.of(name)` (throws), `City.tryOf(name)` (nullable),
  `City.values()`, and instance `name()`/`region()`/`qualifiedName()`/
  `displayName()`.
- **Typed finders**: `findDate(LunarMonth, Tithi, int year, City)`,
  `findDates(...)`, `findNext(LunarMonth, Tithi, City, LocalDate from)`.
- **Center-disc correction tables** for all cities (`<key>.center.json`), loaded
  by `CityCorrections.forCity(city, SunriseConvention.CENTER_DISC)`. Previously
  any non-upper-limb convention fell back to pure Meeus; it now uses the
  Swiss-validated center-disc tables (Meeus fallback only when a table is absent).
- **Global transition-correction list** populated
  (`GlobalTransitionCorrections`, 16,266 deltas) — transcoded verbatim from the
  Dart `global_transition_corrections.g.dart`. Shipped as the classpath resource
  `/corrections/global_transitions.csv` (an inline array literal of this size
  exceeds the JVM 64 KB method-bytecode limit).
- **15 new cities** (245 total): Louisville, Lexington, Columbia, Charleston,
  Greenville, Cleveland, Edison, Jersey City, Ashburn, Fairfax, Rockville,
  Stamford, San Antonio, Milwaukee, Buffalo.

### Changed
- **Iterative sunrise/sunset** (engine code → `r3`): `Astronomy.computeSunrise`/
  `computeSunset` now do the Dart 3-iteration refinement (declination/EOT
  re-evaluated at the rise/set instant, `Duration` day-carry, full sub-minute
  resolution).
- **`FestivalDate.month`/`isAdhika`** now carry the actual occurrence month for
  recurring festivals (resolved from the occurrence date, not the `Festival`
  placeholder), fixing the recurring-festival month bug; getters `getMonth()`/
  `isAdhika()`.
- **Upper-limb per-city tables re-ported to `r3`**: per-city transition, purnima,
  and amavasya maps are now empty — tithi-transition instants are corrected by
  the single global (city-independent) table, and purnima/amavasya boundary days
  derive from the corrected day-tithi.
- **`tithiSegments` / `tithiAtInstant` now label by segment-midpoint elongation**
  over globally-corrected transition boundaries (matching Dart `r3`
  `tithi_calculator.dart`), replacing the `r2` per-city transition-snapping +
  sunrise-anchor ±1 stepping. Transition bisection tightened to 1 s (from 30 s)
  to match Dart.

### Verified
- Java ⟷ Dart `5.1.x` parity on corrected output, gate = 0 mismatches:
  - `tithiOnDate` upper-limb: 0 / 17,986,430 (245 cities × 73,414 days, 1900–2100).
  - `tithiOnDate` center-disc: 0 / 17,986,430.
  - `tithiSegments` (245 cities × 400 days) + `tithiAtInstant` (24-city 3-hourly
    grid, 1,122,048 instants): 0 whole-tithi mismatches.
- `./gradlew build` green (147 tests).

### Breaking changes
- **Every city-keyed public method now takes `City`** (not `String`), and every
  tithi spec takes `Tithi` (not `Paksha`+`int`). Relative to `3.1.0`:
  `tithiOnDate`, `tithiAtInstant`, `tithiSegments`, `dateFor`, `recurringDates`,
  `sunrise`, `sunset`, `findDate`, `findDates`, `findNext` all take `City`/`Tithi`.
  Migrate `"Ujjain"` → `City.of("Ujjain")` (or `City.UJJAIN`) and
  `Paksha.SHUKLA, 8` → `Tithi.shukla(8)`. `getDate(...)`/`getDates(...)` are
  removed — use `findDate(...)`/`findDates(...)`.
- **`CityLocation` and `City.getLocation` are internalized** (package-private,
  no longer part of the public API), mirroring Dart 5.0/5.1.1. The engine
  resolves a city's coordinates itself; callers address places via `City` /
  `Location`.

### Notes
- The Meeus/VSOP87 astronomy is unchanged and byte-identical to Dart; only the
  correction DATA and its wiring changed. Engine code + DATA revision is now `r3`
  (full `.se1` parity for both sunrise conventions).

## [3.1.0] — 2026-06-22

### Added
- **Sunrise/sunset** in the public API: `Panchang.sunrise(date, city)` and
  `Panchang.sunset(date, city)` (also on `Panchang.at(location)`), returning UTC
  `Instant`s. Meeus astronomy (~1-minute accuracy); no per-city correction (the
  tables adjust tithi, not sun times). Inherits strict city resolution.

## [3.0.1] — 2026-06-22

### Fixed
- Festival data corrections (curated against the Kashmiri jantri):
  - **Zang Trayi** corrected to Chaitra Shukla **3** (was Shukla 2).
  - Removed duplicate entries: **Holika Dahan** (use **Holi**, both Phalguna S.15)
    and **Thal Buth Vuchun** (use **Navreh**, both Chaitra S.1).
  - Removed **Thal Barun (Navreh)** — a *relative* observance (the day before
    Navreh: Chaitra K.15 normally, K.14 when Chaitra S.1 is kshaya) that a fixed
    tithi cannot represent. Deferred pending relative-festival support.
  - **36 built-in festivals.** (Removes the `HOLIKA_DAHAN`, `THAL_BUTH_VUCHUN`,
    `THAL_BARUN_NAVREH` constants added in 3.0.0.)

## [3.0.0] — 2026-06-21

### Added
- Recurring festival **Shukla Ashtami** (`masik_shukla_ashtami`) — monthly bright-fortnight Ashtami.
- 11 curated festivals from the Kashmiri jantri (Samvat 2082): Navreh, Thal Buth
  Vuchun, Zang Trayi, Durga Ashtami, Nirjala Ekadashi, Zyeth Ashtami, Haar
  Ashtami, Navratri (Sharad) Begins, Maha Navami, Karva Chauth, Bhai Dooj, Khichdi Amavasya, Gauri Tritiya, Kaav
  Punim (Magh Purnima), Huri Aukdoh, Huri Ashtami.
  Teil Ashtami, Holi, Sonth, Thal Barun (Navreh). **39 built-in festivals total.**
- **Case/space-insensitive city resolution** plus the qualified `"City, Region"`
  form: `City.getLocation("new york")` and `City.getLocation("New York, NY")` now
  resolve the same as `"New York"`. New `City.resolveName(name)` returns the
  canonical name or `null` (non-throwing probe).
- **Coordinate input** via `Location` + `Panchang.at(Location)`: `Location.city(name)`
  or `Location.at(lat, lng[, offset])`. A point that rounds into a supported city's
  **0.1° cell** reuses that city wholesale (Swiss-corrected, `LocationSource.CITY_CORRECTED`);
  a point outside every cell is Meeus-only (`LocationSource.MEEUS_RAW`) and requires an
  offset. `panchang.at(loc).tithiOnDate(date)` etc.

### Fixed
- Festival/date finder no longer mis-attributes a *previous* month's kshaya Purnima/Amavasya to the next month's span (e.g. `getDates(Pausha, Shukla, 15)` wrongly returning Margashirsha purnima dates). Boundary-kshaya now applies only to paksha-leading tithis (Pratipada).

### Changed
- **Nishita Kaal muhurta window** now uses the precise classical definition —
  the **8th of the night's 15 muhurtas** (the central muhurta) — instead of the
  coarse "third quarter of the night." Displayed Nishita windows now match Drik
  Panchang (e.g. Janmashtami Smarta for Seattle/Redmond 2026: 12:47–01:30 AM).
  Festival **dates are unchanged** — the day-attribution moment is still the
  night midpoint, which sits at the centre of this muhurta. Engine rev unchanged (`r2`).
- **Behavior change — unsupported cities now fail fast.** `City.getLocation(name)`
  (and therefore every `Panchang` call) throws `IllegalArgumentException` for an
  unknown or wrong-region city instead of silently falling back to the default
  reference city. A wrong location yields wrong sunrise-based tithis/festival dates,
  so the engine refuses to guess. There is **no fuzzy/region-stripping match**.
  Validate with `City.resolveName(name)` (returns `null`) or `City.supported()`.
  `CityCorrections.forCity(...)` is unaffected (still returns empty maps for an
  unregistered city). Request new cities at
  <https://github.com/misrilibrary/tithi-engine/issues>.

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
