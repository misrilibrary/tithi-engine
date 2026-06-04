# Contributing to tithi-engine

Thank you for considering contributing! This project is open to improvements, new features, and bug fixes.

## How to Contribute

1. Fork the repository
2. Create a feature branch: `git checkout -b my-feature`
3. Make your changes
4. Run tests: `./gradlew build`
5. Commit with a clear message
6. Push and open a Pull Request

## Development Setup

- Java 17+
- Gradle 8.12 (wrapper included)

```bash
./gradlew build       # compile + test + SpotBugs + JaCoCo
./gradlew test        # tests only
```

## Adding a Festival

See README.md → "Extensibility → Adding a Festival"

## Adding a City

See README.md → "Extensibility → Adding a City"

## Guidelines

- Keep the library dependency-free (no external runtime deps)
- All public API must have Javadoc
- Tests must pass before submitting a PR
- Follow existing code style (no auto-formatter wars)

## Reporting Issues

Open a GitHub issue with:
- What you expected
- What happened instead
- Steps to reproduce (date, city, expected tithi)

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.
