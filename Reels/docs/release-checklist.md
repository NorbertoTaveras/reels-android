# Release Checklist

Use this checklist before preparing a signed production build of Reels.

## Configuration

- Store the TMDB API read access token in `local.properties`, an environment variable, or a Gradle property named `TMDB_READ_TOKEN`.
- Do not commit real API tokens, keystores, generated signing files, or service account credentials.
- Keep `local.properties.example` as the only checked-in local configuration reference.

## Build Variants

- Use `stageDebug` for local development against the stage application id.
- Use `productionDebug` for production package validation with debug signing.
- Use `productionRelease` for the minified, resource-shrunk release artifact.

## Verification

Run the release build before creating a distributable artifact:

```bash
./gradlew :app:assembleProductionRelease --no-configuration-cache --max-workers=2
```

Confirm these release expectations:

- R8 minification is enabled.
- Resource shrinking is enabled.
- Network logging is disabled.
- Cleartext network traffic is disabled.
- TMDB attribution is visible in Settings.
- Light and dark themes have been checked on device or emulator.
- Core screens have loading, empty, error, retry, and offline states where applicable.

## Signing

- Configure real release signing outside source control.
- Do not commit the keystore or signing passwords.
- Replace debug signing before Play Store distribution.
- Verify `versionCode` and `versionName` before uploading a release candidate.

## Privacy And Offline Behavior

- Favorites are stored locally for offline access.
- Full browse/search caching is not part of v1.
- Favorites removal should be reversible only by re-adding the movie.
- The app should not collect account data unless a future feature explicitly introduces account support.

## Known Gradle Follow-Up

The project currently keeps these flags in `gradle.properties`:

```properties
android.builtInKotlin=false
android.newDsl=false
```

Removing them currently causes a Kotlin extension registration conflict during Gradle configuration. Revisit these flags during the next Android Gradle Plugin and Kotlin plugin upgrade.
