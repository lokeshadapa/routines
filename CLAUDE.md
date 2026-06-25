# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Routines** is an Android app (Kotlin + Jetpack Compose) that lets users create and run timed daily routines. See `init.md` for feature requirements, `wireframes.md` for screen mockups, `status.md` for current progress, and `android_best_practices.md` for the grounding rules on architecture, tooling, and UI expectations.

The Android project lives in the `Routines/` subdirectory. All Gradle commands must be run from there.

## Build & Run Commands

Run from `Routines/`:

```bash
# Debug build
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single unit test class
./gradlew test --tests "com.example.routines.ui.main.MainScreenViewModelTest"

# Install and launch on connected device
./gradlew installDebug
```

## Architecture

**Pattern:** MVVM + Repository, Single Activity, 100% Kotlin.

**Layer flow:**
```
Compose UI → ViewModel → Repository → Room DAOs → SQLite
```

- **UI layer** (`ui/main/`): Compose screens (`HomeScreen`, `CreateRoutineScreen`, `RunningRoutineScreen`). No business logic here.
- **ViewModel** (`ui/viewmodel/RoutineViewModel`): Single shared ViewModel exposes `allRoutines: StateFlow` and `getTasksForRoutine(): Flow`. Uses a manual `Factory` — Hilt `@HiltViewModel` is not yet applied.
- **Repository** (`data/repository/RoutineRepository`): Wraps `RoutineDao` and `TaskDao`. All database ops go through here.
- **Room entities** (`data/local/Entities.kt`): `RoutineEntity` (id, name, createdAt) and `TaskEntity` (id, routineId, name, durationSeconds, icon, orderPosition). Tasks cascade-delete when the parent routine is deleted.

**Hilt DI is partially set up:** `DatabaseModule` (`di/`) provides Room DAOs and `RoutineRepository` via Hilt, but `MainActivity` currently instantiates `RoutineViewModel` manually using `RoutineViewModel.Factory` rather than Hilt injection. `RoutinesApplication` is missing `@HiltAndroidApp` and the Hilt Gradle plugin is not applied in `app/build.gradle.kts`.

**Navigation:** Custom state-based system in `Navigation.kt` using `sealed class Screen` + `mutableStateOf`. Not Jetpack Navigation Compose (Nav3 libraries are included as dependencies but not used yet).

**Stale file:** `MainScreenViewModel` and `DataRepository` / `DefaultDataRepository` are scaffolding artifacts from project init and are not used by any active screen. They can be deleted when cleaning up.

## Key Libraries (from `gradle/libs.versions.toml`)

| Library | Purpose |
|---|---|
| Compose BOM 2026.03.01 | Compose UI + Material 3 |
| Room 2.6.1 | Local SQLite via KSP |
| Hilt 2.59.2 + KSP | Dependency injection |
| Navigation3 1.0.1 | Included but not yet wired |
| Coroutines + Flow | Async / reactive data |

## Design Reference: Tomato App

**The Tomato app (`/Users/lokesh/Documents/Tomato`) is the gold standard for UI, transitions, timer design, notifications, and anything related to design and UX.** Before making any UI/UX decision, check how Tomato handles it first.

Key reference files:
- `androidApp/src/main/java/org/nsh07/pomodoro/ui/AppScreen.kt` — navigation bar island pattern (`HorizontalFloatingToolbar` + `ToggleButton`)
- `shared/src/androidMain/kotlin/org/nsh07/pomodoro/ui/timerScreen/TimerScreen.kt` — timer ring, font sizing, animation

## Development Rules (from `android_best_practices.md`)

- Use the `android-cli` tool/skill for creating the project, managing the SDK, running builds, and diagnostics — do not invoke `adb` or `sdkmanager` directly.
- Prioritize premium aesthetics in Compose UI — avoid basic or generic layouts. Material 3 dynamic colors, dark/light theme support, and polished interactions are expected.
- Keep the data model flexible so that new task types can be added to a routine without restructuring the schema.
- All background work must use Kotlin Coroutines/Flow — no threads or callbacks.

## Testing

See `Routines/test-framework.md` for the full testing guide — layer structure, patterns for DAO tests, migration tests, Robolectric tests, and TDD workflow for new features.

Quick commands (run from `Routines/`):
```bash
./gradlew :app:testDebugUnitTest          # unit + Robolectric (no device)
./gradlew :app:connectedDebugAndroidTest  # Room DAO + migration tests (device required)
```

## Pending Work (`status.md` Phase 4)

- Animations and screen transitions
- Drag-and-drop task reordering (use `orderPosition` field on `TaskEntity`)
- Google Account nightly backup (design to be additive over Room)
