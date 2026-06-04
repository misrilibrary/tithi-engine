# Example: Basic Usage

Demonstrates how to consume `tithi-engine` as a Gradle dependency.

## Setup

First, publish the library to your local Maven cache:

```bash
cd ../..
./gradlew publishToMavenLocal
```

## Run

```bash
cd examples/basic-usage
./gradlew run
```

## Expected Output

```
Today (2026-06-04) in Ujjain: Jyeshtha Krishna Chaturthi
Maha Shivaratri 2026 (Ujjain): 2026-02-15
Diwali 2026 (Seattle): 2026-10-20
Janmashtami 2026 (Seattle): 2026-08-14

Supported cities (109):
Agra, Ahmedabad, Allahabad, ...

Festivals 2026 (Ujjain):
  Maha Shivaratri          2026-02-15
  Holika Dahan             2026-03-02
  ...
```

## Using from Maven Central (after published)

Replace `mavenLocal()` with just `mavenCentral()` in `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.misrilibrary:tithi-engine:1.0.0'
}
```
