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
Today (2026-10-31) in Ujjain: Kartika Krishna Shashthi
Janmashtami 2026 (Seattle): 2026-09-04

Supported cities (230):
Abu Dhabi, UAE, Accra, Ghana, Addis Ababa, Ethiopia, ...
```

## Using from Maven Central (after published)

Replace `mavenLocal()` with just `mavenCentral()` in `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.misrilibrary:tithi-engine:4.0.0'
}
```
