# Reels

Reels is a movies-first Android app powered by The Movie Database API. It is built with Kotlin, Jetpack Compose, Material 3, Hilt, Retrofit/OkHttp, Paging, Room, Coroutines, and Flow.

## Setup

Create or update `local.properties` at the project root and add your TMDB API read access token:

```properties
TMDB_READ_TOKEN=your_tmdb_v4_read_access_token
```

The token can also be supplied as an environment variable or Gradle property named `TMDB_READ_TOKEN`. Do not commit real API tokens.

## Build Variants

- `stageDebug`: stage application id and stage app name.
- `productionDebug`: production application id with debug signing.
- `productionRelease`: minified and resource-shrunk release build.

Common local commands:

```bash
./gradlew :app:assembleProductionDebug
./gradlew :app:installProductionDebug
./gradlew :app:assembleProductionRelease
```

Before preparing a signed build, review the [release checklist](docs/release-checklist.md).

## Architecture

The app uses MVVM with unidirectional UI state/events:

- `presentation`: Compose screens, UI state, events, effects, and ViewModels.
- `domain`: app-facing data models, repository contracts, and use cases.
- `data`: Retrofit services, DTOs, Room entities/DAOs, repository implementations, paging sources, and mappers.
- `di`: Hilt modules and qualifiers.

UI reads explicit state from ViewModels and sends user actions back as events. One-off navigation and snackbar messages are exposed as effects.

## TMDB Attribution

This product uses the TMDB API but is not endorsed or certified by TMDB.
