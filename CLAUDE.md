# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Ice Fishing Catcher is an Android mobile game built with Kotlin and Jetpack Compose. It is a portrait-orientation, single-player fishing game with 30 levels of increasing difficulty.

- **Package:** `com.feelingtouch.r`
- **Min SDK:** 24 (Android 7.0), **Target SDK:** 36
- **Language:** Kotlin with Jetpack Compose UI
- **Build System:** Gradle with Kotlin DSL and version catalog (`gradle/libs.versions.toml`)

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (ProGuard + obfuscation)
./gradlew bundleRelease          # Create AAB for Play Store
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew lint                   # Run Android Lint
```

Release builds run a custom `removeProguardMap` task automatically after `bundleRelease` to strip the obfuscation mapping file from the output.

## Architecture

**Single-activity architecture** with two activities and Compose NavHost navigation:

- `LoadingActivity` → shows splash + checks network connectivity → transitions to `MainActivity`
- `MainActivity` → hosts the main game NavHost; blocks multi-touch via `dispatchTouchEvent()`

### Layer Structure

```
navigation/      Routes.kt, LoadingNav.kt, MainNav.kt
screens/         One composable per screen (stateless — state lives in activities)
ui/
  theme/         Color.kt, Theme.kt (Material3), Type.kt (custom font)
  components/    OceanButton, UnderwaterBackground, PressableWithCooldown, ScreenTitle
model/           LevelConfig.kt — procedurally generates all 30 level configs
storage/         PrefsManager.kt — SharedPreferences wrapper for player progress
audio/           SoundManager.kt — MediaPlayer for background music + SFX
```

### Key Architectural Decisions

- **Stateless composables:** `PrefsManager` and `SoundManager` are instantiated in the activities and passed down through navigation lambdas to screens.
- **Procedural level generation:** All 30 `LevelData` configs are computed algorithmically in `LevelConfig.kt` (fish speed, zone widths, target catches, max misses, points).
- **Debounced input:** `PressableWithCooldown` (1-second cooldown) prevents rapid repeated taps throughout the UI.
- **Dynamic content:** Privacy policy is loaded via WebView from a remote URL (not hardcoded).

### Gameplay Parameters (from LevelConfig)

| Parameter | Level 1 | Level 30 |
|---|---|---|
| Fish speed | 3500 ms | 1400 ms |
| Perfect zone | 10% | 4% |
| Good zone | 16% | 7% |
| Target catches | 3 | 13 |
| Max misses | 5 | 2 |
| Points (perfect) | 105 | 250 |

### OceanButton Variants

`OceanButton` has three style variants passed via the `style` parameter: `Primary`, `Accent`, `Secondary` — each uses a different gradient from the ocean color palette defined in `ui/theme/Color.kt`.

## Game: Fish Memory

The game is a card-matching memory game. `screens/GameScreen.kt` contains the full implementation.

Key domain types (all in `GameScreen.kt`): `MemoryCard`, `CardState` (FACE_DOWN / FACE_UP / MATCHED), `GameResult` (WIN / LOSE).

Level configs live in `model/MemoryLevelConfig.kt` (`MemoryLevelData.getLevel(n: Int)`). The old `model/LevelConfig.kt` is unused and can be removed.

**Card flip animation:** `MemoryCardItem` renders `CardBack` and `CardFront` as overlapping composables in a `Box`; `animateFloatAsState` drives `rotationY` 0°→180° with alpha switching at 90° for a seamless 3D flip.

**Interaction guard:** `isLocked = true` during the 900 ms mismatch delay; `card.state != FACE_DOWN` and `card.id in flippedIds` guards prevent duplicate/invalid taps.

**Lose conditions:** moves reach 0 after a mismatch (checked post-delay) or after a non-winning match; timer reaches 0 (levels 15–30 only).

**Score:** 50 + level×5 per matched pair, plus `movesLeft×10 + timeLeft×2` completion bonus on WIN.

**`PrefsManager`** now has a `totalLevelsCompleted` field (persisted as `"total_levels_completed"`); incremented on every win.

## Firebase & Ads

- Firebase Analytics, Crashlytics, and Cloud Messaging are integrated. Config is in `app/google-services.json`.
- AdMob banner ad appears on `LeaderboardScreen`. Current ad unit IDs are **test IDs** — replace before production release.
- Crashlytics mapping file upload is disabled (`mappingFileUploadEnabled = false`).
