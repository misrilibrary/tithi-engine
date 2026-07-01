# tithi-engine (Java) — Accuracy Benchmark

**Source of truth (transitive):** the Java engine is **byte-identical** to the
Dart [`tithi_engine`](https://github.com/misrilibrary/tithi-engine-dart), which is
validated against **JPL Swiss Ephemeris `.se1` files** (`seFlgSwiEph`, Lahiri
ayanamsha). The two engines share the same Meeus/VSOP87 astronomy (evaluated in
Terrestrial Time) and the **same transcoded `.se1` correction data**, so Java
inherits Dart's `.se1` accuracy — and this is confirmed by **direct Java ⟷ Dart
parity sweeps** (below), not merely asserted.

Range **1900–2100**, all **245 cities**, both sunrise conventions
(`UPPER_LIMB` / `CENTER_DISC`) and both month systems (`PURNIMANT` / `AMANT`).

**Accuracy bar: minute-level** (panchang display + birth-time to the minute).

## Verification matrix

Evidence is **Java ⟷ Dart** parity (Dart = `.se1`-validated). *Direct* = swept in
Java against the Dart truth; *Inherited* = guaranteed by Meeus byte-identity +
verbatim-transcoded correction data (and exercised by the JUnit suite).

| # | Compute point | Status | Coverage | Evidence (Java ⟷ Dart) |
|---|---|---|---|---|
| 1 | Tithi number | 🟢 | Direct | included in tithiOnDate/segment sweeps (rows 3,7,8) |
| 2 | Transition instants | 🟢 | Inherited | global correction list transcoded verbatim (16,266 deltas); consumed identically to Dart |
| 3 | `tithiOnDate` upper-limb | 🟢 | **Exhaustive** | **0 / 17,986,430** (245 cities × 73,414 days) |
| 4 | `tithiOnDate` center-disc | 🟢 | **Exhaustive** | **0 / 17,986,430** |
| 5 | Sunrise | 🟢 | Direct (sampled) | byte-identical to Dart on an 8-city lat-spread sample (equator→polar, both hemispheres), 0 diffs to the millisecond |
| 6 | Sunset | 🟢 | Inherited | same iterative algorithm as sunrise (row 5), sign-mirrored |
| 7 | `tithiSegments` (labels + boundaries) | 🟢 | **Broad** | 245 cities × 400 days = 98,000 windows; **0 whole-tithi mismatches** |
| 8 | `tithiAtInstant` (birth-time) | 🟢 | **Broad** | 24-city 3-hourly grid 2010–2026 = **1,122,048 instants; 0 mismatches** |
| 9 | Paksha | 🟢 | Derived | pure function of tithi number |
| 10 | Amavasya / Purnima days | 🟢 | Derived | from the corrected day-tithi (rows 3–4) |
| 11 | Lunar month name | 🟢 | Inherited | same `LunarMonthResolver` + sidereal-Sun sankranti logic as Dart; JUnit month-boundary tests |
| 12 | Adhika (leap) month | 🟢 | Inherited | same resolver; JUnit adhika-naming tests vs verified Drik years |
| 13 | Offset / DST conversion | 🟢 | Broad | UTC-instant API; caller supplies the DST-aware `ZoneOffset` (IANA is the app's job) |
| 14 | `findDate` / `findDates` (tithi → date) | 🟢 | Inherited | same finder logic on the corrected day-tithi; JUnit round-trip tests |
| 15 | Coordinate input (`Location.at`) | 🟢 | Broad | 0.1° cell reuse = the city's Swiss-corrected tables; off-grid points use the uncorrected Meeus path (~99.9% day-tithi, by design) |

**14 of 14 core markers 🟢.** The two exhaustive `tithiOnDate` sweeps (35.97M
city-days across both conventions) plus the segment/instant sweeps found **zero**
mismatches vs the `.se1`-validated Dart engine.

## How parity was verified

- **Byte-identity of the astronomy.** Java `Astronomy.sunLongitude`/`moonLongitude`
  (VSOP87 + Meeus Ch.47 in TT, Espenak–Meeus ΔT) and the iterative sunrise/sunset
  are math-for-math identical to Dart. Confirmed: `Panchang.sunrise` matches Dart
  to the millisecond on the lat-spread sample.
- **Correction data transcoded, not regenerated.** The per-city tables
  (`Swiss − Meeus`) and the global transition-correction list are copied verbatim
  from the Dart `5.1.x` data. Since Meeus is byte-identical, the tables transfer
  exactly (`Meeus + corrections = Swiss`).
- **Direct sweeps.** `tithiOnDate` was compared Java-vs-Dart for all 245 cities ×
  73,414 days × 2 conventions (17,986,430 points each) → 0 mismatches;
  `tithiSegments` and `tithiAtInstant` on dense samples → 0 mismatches.

### Coverage legend
- **Exhaustive** — compared over the entire domain (every city × every day).
- **Broad** — a large representative sample (all cities × a day window; a dense
  instant grid).
- **Direct** — swept in Java against the Dart truth.
- **Inherited** — guaranteed by Meeus byte-identity + verbatim-transcoded data;
  exercised by the JUnit suite (147 tests).
- **Derived** — deterministic function of an Exhaustive/Direct quantity.

## Known residual (sub-minute, accepted)

Transition instants inherit Dart's `.se1` accuracy (≤30 s). Within ~30 s of a
transition, a `tithiSegments` label at a window edge or a `tithiAtInstant` query
can be one tithi off — sub-minute, invisible at minute-resolution display, and
identical to Dart's accepted residual. Full removal would need second-resolution
transition correction. Deferred (matches Dart).

## Coverage backlog (mirrors Dart)

- **findNext / recurringDates** — share finder logic with `findDates` (row 14) but
  aren't independently swept.
- **Polar / extreme-latitude no-rise/no-set days** — clamped approximation.
- **Muhurta window times** (nishita/madhyahna/pradosh) — spot-checked, not swept.
- **Historical civil offsets (pre-~1945)** — caller-supplied offset; not modeled.
- **Out-of-range dates** — behavior outside 1900–2100 is undefined.

## Reproducing

The parity harness lives in the private tooling repo (`tithi-engine-tool`): it
dumps `tithiOnDate` / `tithiSegments` / `tithiAtInstant` from both engines over
the grid and diffs them (gate = 0 mismatches). The Meeus/sunrise byte-identity
check compiles a small `Panchang.sunrise` dumper against the built jar and diffs
it against the Dart `Panchang.sunrise` output.
