# 📘 FocusDesk — Comprehensive Developer & Architecture Manual
> **Document Version:** 1.0.0  
> **Target Audience:** Core Contributors, Mobile Engineers, KMP Architects & Onboarding Developers  
> **Platforms:** Android (SDK 26–35) | Kotlin Multiplatform (KMP) iOS Framework Ready  
> **Tech Stack:** Kotlin 2.1.0 • Jetpack Compose 2025.01.00 (BOM) • Room Database 2.6.1 • Koin 4.0.2 • Coroutines 1.10.1  
> **Primary Patterns:** Clean Architecture (Domain / Data / Presentation) • MVI Unidirectional Data Flow  

---

## 📑 Table of Contents
1. [Executive Overview & Philosophy](#1-executive-overview--philosophy)
2. [Why the Folder Structure is Laid Out Like This](#2-why-the-folder-structure-is-laid-out-like-this)
3. [Architecture Paradigm: Clean Architecture + MVI](#3-architecture-paradigm-clean-architecture--mvi)
4. [Domain Layer Deep Dive](#4-domain-layer-deep-dive)
5. [Data Layer & Offline-First Persistence Deep Dive](#5-data-layer--offline-first-persistence-deep-dive)
6. [Hardware, Sound Synthesis & System Services Deep Dive](#6-hardware-sound-synthesis--system-services-deep-dive)
7. [Presentation & UI Layer Deep Dive](#7-presentation--ui-layer-deep-dive)
8. [Dependency Injection Graph (Koin)](#8-dependency-injection-graph-koin)
9. [Next Developer Handbook: How-To Recipes & Gotchas](#9-next-developer-handbook-how-to-recipes--gotchas)

---

## 1. Executive Overview & Philosophy

### 1.1 What is FocusDesk?
**FocusDesk** is an offline-first, tactile deep-work desk companion engineered for high-performance knowledge workers, developers, and students. It merges the time-tested **Pomodoro Technique** with modern mobile OS capabilities:
- **System-level Dynamic Island & Live Activity Status**: Utilizing standard Android `MediaSession` and `Notification.MediaStyle` to display live countdown chips directly around the screen punch-hole camera on modern Android devices (ColorOS/OxygenOS Aqua Dynamics, Xiaomi HyperOS, Samsung One UI, Android 14/15).
- **Procedural Ambient Audio Engine**: On-device real-time mathematical audio synthesis generating binaural rain, Brownian/pink/white noise, coffee shop ambiance, and forest birdsong directly via Android `AudioTrack` with **zero audio asset files**, saving tens of megabytes in bundle size while avoiding loop stutter.
- **Strict Clean Architecture + MVI Unidirectional Data Flow**: Clear decoupling between UI, business rules, and hardware bridges.
- **Offline-First Room Persistence**: Local persistence using SQLite Room with reactive `StateFlow` and automated streak/XP gamification algorithms.
- **Tactile Glassmorphic Design System**: Custom physics-based motion matching Apple's `.snappy`, `.smooth`, and `.bouncy` curves with five curated design palettes.

### 1.2 Core Architectural Principles
1. **Unidirectional Data Flow (MVI)**: State flows DOWN; Intents flow UP; Side Effects flow SIDEWAYS via one-shot channels. There are no two-way data bindings or untracked state mutations.
2. **Platform Abstraction (KMP `expect`/`actual`)**: Core business rules and domain logic are 100% pure Kotlin in `commonMain`. Platform-dependent hardware (Audio streaming, Haptic vibration) is abstracted behind `expect` declarations.
3. **Zero-Asset Sound Synthesis**: Rather than bundling 50MB of looped MP3/WAV files, sounds are synthesized mathematically at 44.1 kHz 16-bit PCM in real-time.
4. **Resilient Background Execution**: Ticking and media notifications run under an Android Foreground Service so user focus sessions are never interrupted by OS task killers or battery optimizations.

---

## 2. Why the Folder Structure is Laid Out Like This

The repository is structured into two Gradle modules: **`:shared`** (Kotlin Multiplatform shared core) and **`:composeApp`** (Android application module).

```
FocusDesk/
├── shared/                                    # KMP Cross-Platform Core
│   └── src/
│       ├── commonMain/kotlin/com/focusdesk/   # Pure Kotlin (No Android/Java imports)
│       │   ├── core/
│       │   │   ├── di/SharedModule.kt         # Platform-independent Koin DI modules
│       │   │   ├── mvi/                       # Base MVI classes (State, Intent, Effect, ViewModel)
│       │   │   └── platform/                  # expect declarations (AudioPlayer, Haptics)
│       │   ├── data/repository/               # In-memory mock repositories (for iOS & JVM tests)
│       │   └── domain/
│       │       ├── model/                     # Pure domain data models & enums
│       │       ├── repository/                # Domain repository interfaces
│       │       └── usecase/                   # Business use cases (TimerEngine, ManageTasks, etc.)
│       ├── androidMain/kotlin/com/focusdesk/  # Android actual implementations
│       │   └── core/platform/                 # AndroidAudioPlayer (AudioTrack), AndroidHaptics (Vibrator)
│       ├── iosMain/kotlin/com/focusdesk/      # iOS actual implementations (Framework ready)
│       │   └── core/platform/                 # IosAudioPlayer (AVAudioEngine), IosHaptics (UIFeedback)
│       └── commonTest/kotlin/com/focusdesk/   # Pure Kotlin unit tests
│
└── composeApp/                                # Android Application Target Module
    └── src/main/
        ├── AndroidManifest.xml                # Permissions, Services, Receivers, MainActivity
        └── kotlin/com/focusdesk/app/
            ├── FocusDeskApplication.kt        # Application class initializing Koin
            ├── MainActivity.kt                # Single-Activity entrypoint & navigation host
            ├── data/                          # Android Local Storage Layer
            │   ├── local/
            │   │   ├── FocusDeskDatabase.kt   # Room Database class with singleton & prepopulate callback
            │   │   ├── dao/                   # FocusSessionDao, TaskDao, UserSettingsDao
            │   │   └── entity/                # Room entities & toDomain() / toEntity() mappers
            │   └── repository/                # Room implementations of domain repositories
            ├── di/
            │   └── AppModule.kt               # Android Koin module (Room DB, DAOs, ViewModels, AudioEngine)
            ├── presentation/                  # Jetpack Compose UI Layer
            │   ├── analytics/                 # Insights screen, charts, productivity metrics
            │   ├── designsystem/              # Theme, Color palettes, Typography, Shapes, Motion, Tactile Slider
            │   ├── focus/                     # Quick-start cards & new session bottom sheet
            │   ├── home/                      # Dashboard, streak counter, daily progress ring
            │   ├── navigation/                # Bottom navigation bar & Screen routes
            │   ├── onboarding/                # First-run setup experience
            │   ├── settings/                  # Custom timer durations, sounds, themes, data reset
            │   ├── sounds/                    # Ambient noise browser & tactile volume mixer
            │   └── timer/                     # Full-screen Pomodoro circular timer & completion celebration
            └── service/                       # Background Execution & OS Integration
                ├── DynamicIslandService.kt    # Foreground service with MediaSession & Notification.MediaStyle
                └── DynamicIslandActionReceiver.kt # Broadcast receiver handling notification action intents
```

### 2.1 Why this separation?
- **Separation of Concerns**: UI rendering (`composeApp`) changes frequently when redesigning cards, buttons, or transitions. The core business rules (`TimerEngineUseCase`, `FocusSession`) change rarely. Keeping them in separate modules prevents UI experiments from breaking core timer calculations.
- **Portability to iOS & Desktop**: Everything inside `shared/src/commonMain` is 100% pure Kotlin. When building an iOS app with SwiftUI or Compose Multiplatform, you reuse `shared` without rewriting a single line of business logic or timer state calculation.
- **Test Speed**: Unit tests inside `shared/src/commonTest` execute on the local JVM in less than 2 seconds without launching Android emulators or Robolectric mocks.
- **Data Layer Isolation**: Notice that `RoomFocusRepository` lives in `composeApp` while `FocusRepository` (the interface) lives in `shared/domain/repository`. This adheres strictly to the **Dependency Inversion Principle**: Domain defines the abstraction; Data provides the concrete SQL implementation.

---

## 3. Architecture Paradigm: Clean Architecture + MVI

### 3.1 Clean Architecture Layer Hierarchy
```
┌──────────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                      │
│   Jetpack Compose UI (Screens, Dialogs, Custom Canvas)   │
│   MVI ViewModels (TimerViewModel, SettingsViewModel)     │
└────────────────────────────┬─────────────────────────────┘
                             │ observes StateFlow & triggers Intent
                             ▼
┌──────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                         │
│   Use Cases: TimerEngineUseCase, ManageTasksUseCase      │
│   Domain Models: FocusSession, UserSettings, TaskItem    │
│   Repository Interfaces: FocusRepository, TaskRepository │
│          (100% Platform-Agnostic Pure Kotlin)            │
└────────────────────────────▲─────────────────────────────┘
                             │ implements interfaces
                             │
┌────────────────────────────┴─────────────────────────────┐
│                      DATA LAYER                          │
│   Local Persistence: Room Database, DAOs, SQLite Tables  │
│   Concrete Repositories: RoomFocusRepository, RoomTask...│
│   Hardware Bridges: AmbientAudioEngine, HapticEngine     │
└──────────────────────────────────────────────────────────┘
```

### 3.2 The MVI Engine (`MviViewModel<I, S, E>`)
Every feature screen in FocusDesk is driven by the base MVI framework defined in [MviViewModel.kt](file:///c:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/core/mvi/MviViewModel.kt):

| Component | Class / Interface | Responsibility |
| :--- | :--- | :--- |
| **State** | `MviState` | An immutable data class capturing 100% of the UI state for a screen. |
| **Intent** | `MviIntent` | A sealed interface defining every possible user action or system event. |
| **Effect** | `MviEffect` | A sealed interface for one-off transient events (snackbars, haptics, navigation). |
| **Reducer** | `setState { copy(...) }` | A pure function modifying state via Kotlin Coroutines `MutableStateFlow.update`. |
| **Channel** | `_effects.send(effect)` | A buffered coroutine channel ensuring side-effects are received exactly once. |

#### Complete Execution Flow (Example: User Taps "Start Focus")
1. **User Interaction**: User taps the large Play button on `TimerScreen`.
2. **Intent Dispatch**: `TimerScreen` invokes `viewModel.onIntent(TimerIntent.ToggleTimer)`.
3. **Intent Handling**: `TimerViewModel.handleIntent(TimerIntent.ToggleTimer)` runs inside `viewModelScope`.
4. **Use Case Execution**: The ViewModel checks `session.status`. If `Idle` or `Paused`, it calls `timerEngineUseCase.startTimer()`.
5. **State Mutation**:
   - `TimerEngineUseCase` updates `FocusSession.status` to `Running` and calls `focusRepository.updateCurrentSession(session)`.
   - `RoomFocusRepository` updates its backing `_currentSession: MutableStateFlow<FocusSession>`.
   - `TimerViewModel` collects this update, mutates its `TimerState` via `setState { copy(session = session) }`.
   - A one-shot effect is emitted: `setEffect { TimerEffect.PlayTickHaptic }`.
6. **UI Recomposition**: `TimerScreen` observes `state.collectAsStateWithLifecycle()`. Compose recomposes the Play button into a Pause button and animates the progress ring with physics-based springs.
7. **System Sync**: `DynamicIslandService` observes `focusRepository.observeCurrentSession()` in background, builds the `Notification.MediaStyle`, and pins the live countdown chip to the Android status bar.

---

## 4. Domain Layer Deep Dive

The Domain layer is located at `shared/src/commonMain/kotlin/com/focusdesk/domain/`. It contains zero platform dependencies and acts as the heart of FocusDesk.

### 4.1 Domain Models & Enums

#### `FocusSession`
Represents an active or completed focus/break interval:
- `id: String`: Unique UUID string (e.g. `"session_1741459200000"`).
- `mode: SessionMode`: Enum values `Work` (25m default), `ShortBreak` (5m default), `LongBreak` (15m default).
- `taskTitle: String`, `category: String`, `mood: String`: Metadata associated with the focus session.
- `soundscape: Soundscape`: Selected procedural ambient soundscape (e.g. `Rain`, `BrownNoise`, `CoffeeShop`, `None`).
- `targetDurationSeconds: Long`: Target length of the interval in seconds.
- `remainingSeconds: Long`: Seconds remaining on the clock.
- `status: SessionStatus`: State machine status (`Idle`, `Running`, `Paused`, `Completed`).
- `currentRound: Int`, `totalRounds: Int`: Progress through Pomodoro intervals before triggering a long break.
- `associatedTaskId: String?`: Optional foreign key linking this session to a specific `TaskItem`.
- `progress: Float`: Computed property returning normalized completion between `0.0f` and `1.0f`.
- `formattedRemainingTime: String`: Computed MM:SS display string with zero padding.

#### `UserSettings` & `TimerIntervals`
Maintains user configuration and gamification stats:
- `intervals: TimerIntervals`: Stores work, short break, and long break durations in minutes.
- `autoStartBreaks: Boolean` & `autoStartWork: Boolean`: Auto-transition flags.
- `soundscape: Soundscape` & `soundscapeVolume: Float`: Persisted ambient audio volume.
- `tickSoundsEnabled`, `hapticsEnabled`, `notificationsEnabled`: System toggle preferences.
- `appTheme: AppTheme`: Active theme (`DefaultFocus`, `PaperStudio`, `LowTide`, `LastLight`, `NightBloom`).
- `dailyGoalHours: Int`: Daily target focus goal in hours.
- `level: Int`, `xp: Int`: Gamification level progression (100 XP per level).
- `dayStreak: Int`, `bestStreak: Int`: Daily consecutive streak tracking.

#### `TaskItem`, `TaskPriority`, `TaskCategory`
Represents actionable work items in the task manager:
- `TaskPriority`: `Low` (1), `Medium` (2), `High` (3), `Urgent` (4).
- `TaskCategory`: `DeepWork`, `Coding`, `Writing`, `Planning`, `Design` with designated hex colors.
- `estimatedPomodoros: Int` vs `completedPomodoros: Int`: Estimated vs actual focus rounds spent.

#### `ProductivityStats` & `DailyFocusMetric`
Represents aggregated analytics for charts and insights:
- `totalFocusMinutesToday: Int`: Focus time logged today.
- `totalFocusMinutesWeek: Int`: Total focus time logged since Monday of current week.
- `weeklyDistribution: List<DailyFocusMetric>`: 7 data points (Monday to Sunday) containing daily focus minutes and session counts.

---

### 4.2 Use Cases & Function Reference

#### `TimerEngineUseCase`
The primary state machine orchestrating focus sessions.

| Function Signature | Why It Exists & When It Is Called | Concurrency & Threading Notes |
| :--- | :--- | :--- |
| `observeSession(): Flow<FocusSession>` | Called by ViewModels and Services to receive real-time timer updates. | Dispatches via `StateFlow` on background or main thread safely. |
| `startTimer()` | Called when user taps Play. Transitions session status from `Idle` or `Paused` to `Running`. Sets `startedAtTimestamp` on first run. | Resets `lastTickEpochMs` to prevent immediate tick drop. |
| `pauseTimer()` | Called when user taps Pause or locks phone with pause action. Transitions `status` to `Paused`. | Freezes countdown without discarding remaining seconds. |
| `tick(force: Boolean = false): FocusSession` | **CRITICAL FUNCTION**. Called every second by the timer coroutine. Decrements `remainingSeconds` by 1. If remaining reaches 0, marks status as `Completed`, calls `focusRepository.recordCompletedSession()`, and resets soundscape. | **Concurreny Guard:** Uses `lastTickEpochMs` to enforce a minimum 850ms interval between ticks. This prevents double-ticking when both UI and Foreground Service trigger ticks simultaneously. |
| `resetTimer()` | Called when user confirms reset in dialog. Resets remaining and target seconds to match `UserSettings.intervals.workDurationSeconds`. | Sets status back to `Idle` and stops audio. |
| `switchMode(mode: SessionMode)` | Called when user manually selects Work, Short Break, or Long Break tabs. Loads duration for that mode from settings. | Sets status to `Idle` ready to start. |
| `skipSession()` | Called when user clicks Skip button. Automatically computes the next mode (Work -> Short Break, or Work -> Long Break after `totalRounds` rounds). | Increments or resets round count accordingly. |
| `startCustomSession(title, category, mood, durationMinutes, soundscape)` | Called from the "Start Custom Session" bottom sheet or quick-start pills on Home Screen. Creates a new `FocusSession` with custom parameters and starts immediately (`Running`). | Persists immediately to repository. |
| `addSeconds(seconds: Long)` | Called when user clicks "+5 min" button on active timer screen. Adds duration to both `remainingSeconds` and `targetDurationSeconds`. | Allows extending deep work without restarting. |
| `endSessionEarly(): FocusSession` | Called when user clicks "End Session" in dialog or notification. Immediately completes session and records completed minutes. | Safely records partial session in Room DB. |
| `updateSoundscape(soundscape: Soundscape)` | Called when user changes soundscape on Sounds screen or active timer. Updates current session and persists to UserSettings if not None. | Synchronizes audio player immediately. |
| `toggleAudioMute(defaultSoundscape): Soundscape` | Called when user clicks Mute button on TimerScreen, Bottom Control Card, or Notification. If currently playing, saves soundscape to settings and mutes. If muted, restores saved soundscape. | Preserves user's preferred soundscape across sessions. |

#### `ManageTasksUseCase`
Handles task CRUD operations and progress tracking:
- `observeAllTasks()`, `observeActiveTasks()`, `observeCompletedTasks()`: Reactive flows for task lists.
- `createTask(title, description, priority, category, estimatedPomodoros)`: Generates random ID and inserts task.
- `toggleTask(id)`: Inverts completion status and records completion timestamp.
- `deleteTask(id)`: Permanently removes task from repository.

#### `GetAnalyticsUseCase`
- `observeStats(): Flow<ProductivityStats>`: Reactive observation of weekly aggregated focus distribution, total minutes today, streak, and recent sessions.
- `resetStats()`: Resets all recorded sessions and clears XP/streaks.

---

## 5. Data Layer & Offline-First Persistence Deep Dive

The Data layer is split into interfaces in `shared/domain/repository/` and Room persistence implementations in `composeApp/src/main/kotlin/com/focusdesk/app/data/`.

### 5.1 Room Database Architecture (`FocusDeskDatabase`)
- Configured with `version = 1`, `exportSchema = false`, and `fallbackToDestructiveMigration()`.
- Thread-safe singleton using double-checked locking:
  ```kotlin
  fun getInstance(context: Context): FocusDeskDatabase {
      return INSTANCE ?: synchronized(this) {
          Room.databaseBuilder(context.applicationContext, FocusDeskDatabase::class.java, "focusdesk.db")
              .fallbackToDestructiveMigration()
              .addCallback(DatabaseCallback())
              .build().also { INSTANCE = it }
      }
  }
  ```
- **Prepopulation Callback (`DatabaseCallback`)**: When the database is created for the first time on a fresh install, a Room callback fires on `Dispatchers.IO`:
  - Inserts default `UserSettingsEntity` (ID = 1, 25m work, 5m short break, 15m long break, 4h daily goal).
  - Inserts 4 starter tasks demonstrating task priorities, categories, and estimated pomodoros.

### 5.2 DAOs (Data Access Objects)
1. **`FocusSessionDao`**:
   - `@Upsert suspend fun upsertSession(session: FocusSessionEntity)`: Inserts or updates session record.
   - `@Query("SELECT * FROM focus_sessions WHERE status = 'Completed' ORDER BY completedAtTimestamp DESC") fun observeCompletedSessions(): Flow<List<FocusSessionEntity>>`: Emits fresh lists whenever a session completes.
   - `@Query("DELETE FROM focus_sessions") suspend fun deleteAllSessions()`: Clears history on data reset.
2. **`UserSettingsDao`**:
   - Single-row table with fixed primary key `id = 1`.
   - `@Query("UPDATE user_settings SET xp = :newXp, level = :newLevel, dayStreak = :dayStreak, bestStreak = :bestStreak, lastActiveEpochDay = :lastActiveEpochDay WHERE id = 1") suspend fun updateXpAndStreak(...)`: Atomic update for gamification metrics.
3. **`TaskDao`**:
   - Reactive task queries and pomodoro completion counter increments.

### 5.3 Entity Mappers (`toDomain()` & `toEntity()`)
Why do we maintain separate entity classes (`FocusSessionEntity`, `TaskEntity`, `UserSettingsEntity`) instead of using Room annotations directly on domain models?
- **Decoupling**: Domain models must remain pure Kotlin Multiplatform classes so iOS and JVM unit tests can use them without Android Room dependencies.
- **Schema Evolution**: Database columns can be renamed or restructured without changing the public domain API used by UI components.

### 5.4 `RoomFocusRepository` Algorithms

#### Gamification & Daily Streak Calculation
When `recordCompletedSession(session)` is called:
```kotlin
val nowMs = System.currentTimeMillis()
val currentEpochDay = nowMs / (1000L * 60 * 60 * 24)

val newStreak = when {
    settings.lastActiveEpochDay == currentEpochDay -> settings.dayStreak.coerceAtLeast(1)
    settings.lastActiveEpochDay == currentEpochDay - 1 -> settings.dayStreak + 1
    else -> 1 // Streak broken, restart from 1
}
val bestStreak = maxOf(settings.bestStreak, newStreak)
val pointsEarned = maxOf(sessionMinutes, 6)
val newXp = settings.xp + pointsEarned
val newLevel = (newXp / 100) + 1
```

#### Weekly Distribution Aggregation Algorithm
`observeProductivityStats()` uses `combine(focusSessionDao.observeCompletedSessions(), taskDao.observeAllTasks(), userSettingsDao.observeSettings())`:
- Calculates Monday start-of-week timestamp via `Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY }`.
- Allocates 7 day buckets (M, T, W, T, F, S, S) and maps each session's `completedAtTimestamp` to the matching weekday index (0 = Monday, 6 = Sunday).
- Calculates completed tasks today by checking `task.completedAtTimestamp >= todayStartMs`.

---

## 6. Hardware, Sound Synthesis & System Services Deep Dive

### 6.1 Procedural Ambient Audio Synthesizer (`AmbientAudioEngine`)

Located in `shared/src/androidMain/kotlin/com/focusdesk/core/platform/AndroidAudioPlayer.kt`.

#### Why Procedural Synthesis Instead of MP3 Files?
1. **Zero Bundle Footprint**: Bundling 12 ambient audio loops of high quality requires ~60–80 MB of APK space. Procedural synthesis requires **0 MB** of assets—the APK remains lightweight (~5 MB).
2. **Infinite Variation & No Loop Seams**: Pre-recorded audio loops repeat every 30–60 seconds, creating noticeable audio artifacts, clicks, and listener fatigue. Mathematical synthesis generates dynamic, infinite, non-repeating soundscapes.
3. **Parametric Control**: Sound parameters (rain intensity, wind pitch, swell speed) can be modulated in real-time based on volume or user preferences.

#### Audio Engine Technical Specifications
- **Format**: 44,100 Hz, 16-bit Signed PCM, Stereo (2 channels: Left and Right interleaved).
- **Audio Output Pipeline**: Android `AudioTrack` configured with `AudioAttributes.USAGE_MEDIA` and `AudioAttributes.CONTENT_TYPE_MUSIC` in `AudioTrack.MODE_STREAM`.
- **Worker Thread**: Dedicated daemon background thread named `"FocusDesk-AudioSynth"`.
- **Buffer**: Calculated dynamically via `AudioTrack.getMinBufferSize()` with safety coercion to `sampleRate / 4`.

#### Synthesis Algorithms by Soundscape
- **Rain & Heavy Rain**:
  First-order low-pass IIR filtered white noise combined with Poisson random drop generator:
  ```kotlin
  val rainIntensity = if (currentSoundscape == Soundscape.HeavyRain) 0.12 else 0.06
  val white = (random.nextDouble() * 2.0 - 1.0)
  filterStateL = (filterStateL * 0.94) + (white * rainIntensity)
  // Drop generation with randomized sinusoidal pitch (1000 - 3000 Hz)
  if (dropCooldown <= 0 && random.nextDouble() < dropProbability) {
      val dropPitch = 1000.0 + random.nextDouble() * 2000.0
      dropL = sin(2.0 * PI * dropPitch * phase) * 0.25
  }
  ```
- **White, Pink, and Brown Noise**:
  Samples Gaussian distribution (`random.nextGaussian() * 0.35`) and updates filter state with damping factors:
  - Brown Noise: `filterFactor = 0.96` (deep warm rumble)
  - Pink Noise: `filterFactor = 0.92` (1/f spectral density)
  - White Noise: `filterFactor = 0.86` (crisp flat distribution)
- **Coffee Shop & Library**:
  Blends 65 Hz and 130 Hz low-frequency rumbles (simulating HVAC and refrigerator compressors) with subtle high-frequency chatter noise:
  ```kotlin
  val rumble = sin(2.0 * PI * 65.0 * t) * 0.12 + sin(2.0 * PI * 130.0 * t) * 0.06
  val chatterNoise = (random.nextDouble() * 2.0 - 1.0) * 0.06
  ```
- **Forest & Birdsong**:
  Modulates wind noise with an ultra-low frequency 0.25 Hz sine envelope, periodically triggering 2,400 Hz bird chirps with pitch inflection.
- **Ocean**:
  0.10 Hz sinusoidal swell modulating filtered surf noise, replicating crashing beach waves.
- **Thunderstorm & Fireplace**:
  Filtered background noise interspersed with stochastic high-amplitude crackle impulses (`random.nextDouble() < 0.005`).

#### One-Shot Tone Generation
Using Android `ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)`:
- `SoundEffect.Tick`: `TONE_PROP_BEEP` (30ms)
- `SoundEffect.Complete`: `TONE_CDMA_ALERT_CALL_GUARD` (250ms)
- `SoundEffect.Bell`: `TONE_PROP_ACK` (180ms)
- `SoundEffect.Click`: `TONE_PROP_BEEP2` (25ms)

---

### 6.2 Haptic Feedback Engine (`HapticEngine`)

Located in `shared/src/androidMain/kotlin/com/focusdesk/core/platform/AndroidHaptics.kt`.
Abstracts Android vibration across SDK versions:
- **API 29+ (Android 10+)**: Utilizes `VibrationEffect.createPredefined()`:
  - `Light` & `Selection`: `VibrationEffect.EFFECT_TICK`
  - `Medium`: `VibrationEffect.EFFECT_CLICK`
  - `Heavy`: `VibrationEffect.EFFECT_HEAVY_CLICK`
  - `Success`: Multi-step waveform `longArrayOf(0, 40, 60, 40)` with amplitude envelope `intArrayOf(0, 180, 0, 255)`.
  - `Warning`: Pulsed waveform `longArrayOf(0, 60, 40, 60)` with amplitude `intArrayOf(0, 200, 0, 200)`.
  - `Error`: Triple alert burst `longArrayOf(0, 80, 50, 80, 50, 80)`.
- **Pre-API 29 Fallback**: Gracefully falls back to legacy `Vibrator.vibrate(durationMs)`.

---

### 6.3 Dynamic Island & Foreground Service (`DynamicIslandService`)

Located in `composeApp/src/main/kotlin/com/focusdesk/app/service/DynamicIslandService.kt`.

#### How the System Dynamic Island Integration Works
Modern Android manufacturer skins (OnePlus/Oppo ColorOS Aqua Dynamics, Xiaomi HyperOS Island, Samsung One UI Now Bar, and stock Android 14/15 Live Notifications) inspect active foreground services for two criteria:
1. An active Android **`MediaSession`** with valid `PlaybackState` and `MediaMetadata`.
2. A notification configured with **`Notification.MediaStyle`**.

When these two conditions are met, the Android system firmware automatically extracts the notification icon, title, and playback state to render the live activity chip directly in the camera punch-hole island or status bar!

#### Dynamic Bitmap Generation (`createTimerBadgeBitmap`)
Instead of displaying a static application icon, `DynamicIslandService` dynamically renders a 256x256 pixel bitmap on an off-screen Android `Canvas`:
- Dark rounded background `#12141C`.
- Circular track `#252836`.
- Glowing progress arc (`#4DEEAA` when running, `#E5A43B` when paused).
- Center-aligned monospaced time text (`MM:SS`).
- Pinned as `Notification.setLargeIcon(timerBitmap)` and `MediaMetadata.METADATA_KEY_ALBUM_ART`.

#### Remote Interactive Notification Actions
The notification provides three interactive actions:
1. **Pause / Resume**: Dispatches `DynamicIslandActionReceiver.ACTION_TOGGLE_PAUSE`.
2. **Mute / Sound**: Dispatches `DynamicIslandActionReceiver.ACTION_TOGGLE_MUTE`.
3. **Stop**: Dispatches `DynamicIslandActionReceiver.ACTION_STOP_SESSION`.

#### Session Completion Handling
When a focus session finishes:
- `DynamicIslandService.showCompletionNotification()` emits a high-priority alert on `COMPLETION_CHANNEL_ID`.
- Activates system vibration (`longArrayOf(0, 350, 200, 350)`) and plays the device notification chime.
- Tapping the notification brings `MainActivity` to the foreground via `MainActivity.ACTION_MAXIMIZE_TIMER`.

---

## 7. Presentation & UI Layer Deep Dive

### 7.1 Single-Activity Architecture (`MainActivity.kt`)
FocusDesk uses a single `ComponentActivity` architecture with pure Jetpack Compose UI:
- **Edge-to-Edge**: Configured via `enableEdgeToEdge()` and `WindowCompat.setDecorFitsSystemWindows(window, false)`.
- **Window Management Modes**:
  1. **Full-Screen Focus Mode**: Active when `TimerScreen` is expanded. Displays large progress circle and ambient sound controls.
  2. **Minimized Floating Session Card**: When the user presses the system back button or minimizes the timer, `TimerScreen` slides away and `ActiveFocusSessionCard` appears docked above the bottom bar.
  3. **Idle Auto-Compression**: If the user does not touch the screen for 8 seconds, `isBottomBarIdleCompressed` collapses the navigation bar to maximize screen canvas. Any touch instantly restores it.

### 7.2 Custom Design System

#### 5 Curated Theme Palettes
Defined in [Color.kt](file:///c:/Users/Admin/Documents/FocusDesk/composeApp/src/main/kotlin/com/focusdesk/app/presentation/designsystem/Color.kt):
1. **Default Focus**: Forest Olive (`#4A683F`), Vibrant Lime (`#D4F384`), Dark Forest (`#16251C`).
2. **Paper Studio**: Warm Craft Paper (`#F5EBE1`), Terracotta (`#8E532B`), Amber (`#E5A169`).
3. **Low Tide**: Sage Mint (`#E9F1ED`), Sea Teal (`#2B5C56`), Aquamarine (`#9EE0D4`).
4. **Last Light**: Peach Blush (`#F8EFE9`), Rust Red (`#B55739`), Sunset Coral (`#F5AC95`).
5. **Night Bloom**: Soft Lavender (`#F1EEF6`), Deep Violet (`#5D4786`), Lilac Glow (`#C0A6EB`).

#### Physics-Based Spring Motion (`SwiftUiMotion.kt`)
Matches Apple SwiftUI animation curves:
- `snappy()`: `dampingRatio = 0.82f, stiffness = 380f` (Used for tab switches and button clicks).
- `smooth()`: `dampingRatio = 0.90f, stiffness = 220f` (Used for slider movements).
- `bouncy()`: `dampingRatio = 0.65f, stiffness = 300f` (Used for celebration dialogs and popups).

#### `ElevatedTactileSlider` Component
A custom elevated slider component replacing standard Material sliders:
- Capsule leveling stick thumb with 5.dp drop shadow and subtle border.
- Integrated gesture handling (`detectTapGestures` + `detectHorizontalDragGestures`).
- Haptic feedback tick emitted on every stepped detent increment.

### 7.3 Screen Catalog

| Screen | File Location | Key Capabilities & Features |
| :--- | :--- | :--- |
| **HomeScreen** | `presentation/home/HomeScreen.kt` | Daily focus target ring, current day streak, gamification level & XP bar, quick preset pills (25m, 45m, 60m), recent session logs. |
| **FocusScreen** | `presentation/focus/FocusScreen.kt` | Deep work preset selector, category filters, and "Custom Session" button launching the bottom sheet. |
| **TimerScreen** | `presentation/timer/TimerScreen.kt` | Large animated circular timer canvas, remaining MM:SS, pause/resume, +5 minutes extension, soundscape mute toggle, full minimize button. |
| **SoundsScreen** | `presentation/sounds/SoundsScreen.kt` | Grid of 15 procedural ambient soundscapes, real-time volume slider, category filtering (Nature, Indoor, Noise, Music). |
| **AnalyticsScreen** | `presentation/analytics/AnalyticsScreen.kt` | Weekly focus distribution bar chart, daily breakdown, completion percentage, streak records, recent session history. |
| **SettingsScreen** | `presentation/settings/SettingsScreen.kt` | Custom work/break interval sliders, theme palette picker (5 themes), daily goal hours slider, haptic/notification toggles, data reset. |
| **OnboardingScreen** | `presentation/onboarding/OnboardingScreen.kt` | Interactive first-time setup guiding the user through permissions, theme selection, and goal setting. |
| **SessionCompleteScreen** | `presentation/timer/SessionCompleteScreen.kt` | Celebration screen displaying earned XP, congratulatory message, and completion chime. |

---

## 8. Dependency Injection Graph (Koin)

FocusDesk uses **Koin 4.0.2** for lightweight multiplatform dependency injection without annotation processing overhead or reflection.

### 8.1 Module Architecture
```
┌───────────────────────────────────────────────────────────┐
│              SHARED CORE (commonMain)                     │
│                                                           │
│  domainModule:                                            │
│    single { TimerEngineUseCase(get(), get(), get()) }     │
│    single { ManageTasksUseCase(get()) }                   │
│    single { GetAnalyticsUseCase(get()) }                  │
│                                                           │
│  inMemoryDataModule:                                      │
│    single<FocusRepository> { InMemoryFocusRepository() } │
│    single<TaskRepository>  { InMemoryTaskRepository()  } │
│    single<SettingsRepository> { InMemorySettingsRepository() }
└─────────────────────────────▲─────────────────────────────┘
                              │ overrides repositories
┌─────────────────────────────┴─────────────────────────────┐
│              ANDROID APP (composeApp)                     │
│                                                           │
│  appModule:                                               │
│    single { FocusDeskDatabase.getInstance(androidContext()) }
│    single { get<FocusDeskDatabase>().focusSessionDao() }  │
│    single { get<FocusDeskDatabase>().userSettingsDao() }  │
│    single { get<FocusDeskDatabase>().taskDao() }          │
│                                                           │
│    // Concrete Room Repositories:                         │
│    single<FocusRepository> { RoomFocusRepository(...) }   │
│    single<SettingsRepository> { RoomSettingsRepository() }│
│    single<TaskRepository> { RoomTaskRepository(...) }     │
│                                                           │
│    // Platform Services & ViewModels:                     │
│    single { AmbientAudioEngine(get()) }                   │
│    viewModel { TimerViewModel(get(), get(), get()) }      │
│    viewModel { AnalyticsViewModel(get()) }                │
│    viewModel { SettingsViewModel(get(), get()) }          │
└───────────────────────────────────────────────────────────┘
```

### 8.2 Application Initialization (`FocusDeskApplication.kt`)
On application launch, Koin starts in `FocusDeskApplication.onCreate()`:
```kotlin
startKoin {
    androidLogger(Level.DEBUG)
    androidContext(this@FocusDeskApplication)
    modules(domainModule, appModule)
}
```

### 8.3 Injection in Jetpack Compose
In Composable functions, ViewModels are resolved using Koin's Compose integration:
```kotlin
val timerViewModel: TimerViewModel = koinViewModel()
val settingsViewModel: SettingsViewModel = koinViewModel()
```
Singletons like `AmbientAudioEngine` and `HapticEngine` are injected into `CompositionLocalProvider` at the root of `FocusDeskTheme`:
- `LocalAudioPlayer.current`
- `LocalHapticEngine.current`
- `LocalFocusDeskColors.current`

---

## 9. Next Developer Handbook: How-To Recipes & Gotchas

### 9.1 Recipe: How to Add a New Soundscape
1. **Define the Enum**:
   Open [UserSettings.kt](file:///c:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/model/UserSettings.kt) and add your sound to `enum class Soundscape`:
   ```kotlin
   Campfire("Campfire", isPremium = false, category = "Nature")
   ```
2. **Implement Synthesis Math**:
   Open [AndroidAudioPlayer.kt](file:///c:/Users/Admin/Documents/FocusDesk/shared/src/androidMain/kotlin/com/focusdesk/core/platform/AndroidAudioPlayer.kt) and add a `when` branch inside the synthesis loop:
   ```kotlin
   Soundscape.Campfire -> {
       for (i in 0 until bufferSize step 2) {
           val crackle = if (random.nextDouble() < 0.008) (random.nextDouble() * 2.0 - 1.0) * 0.5 else 0.0
           val hiss = (random.nextDouble() * 2.0 - 1.0) * 0.03
           filterStateL = (filterStateL * 0.95) + hiss + crackle
           filterStateR = (filterStateR * 0.95) + hiss + crackle
           shortBuffer[i] = (filterStateL * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
           shortBuffer[i + 1] = (filterStateR * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
       }
   }
   ```
3. **Done!** The new soundscape automatically appears in `SoundsScreen`, the bottom sheet picker, and the active session cards without requiring any asset files.

---

### 9.2 Recipe: How to Add a New Theme Palette
1. Open [UserSettings.kt](file:///c:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/model/UserSettings.kt) and append the new theme to `AppTheme`:
   ```kotlin
   enum class AppTheme(val displayName: String) {
       // ... existing themes ...
       NordicFrost("Nordic Frost")
   }
   ```
2. Open [Color.kt](file:///c:/Users/Admin/Documents/FocusDesk/composeApp/src/main/kotlin/com/focusdesk/app/presentation/designsystem/Color.kt) and define the palette:
   ```kotlin
   val NordicFrostColors = FocusDeskThemeColors(
       theme = AppTheme.NordicFrost,
       background = Color(0xFFECEFF4),
       cardBackground = Color(0xFFF8FAFC),
       cardBorder = Color(0xFFD8DEE9),
       primary = Color(0xFF5E81AC),
       primaryAccent = Color(0xFF88C0D0),
       onPrimaryAccent = Color(0xFF1B2B34),
       darkCardBackground = Color(0xFF2E3440),
       darkCardGlow = Color(0x3388C0D0),
       textPrimary = Color(0xFF2E3440),
       textSecondary = Color(0xFF4C566A),
       textMuted = Color(0xFF98A2B3),
       navBarBackground = Color(0xFFF8FAFC),
       navBarSelectedPill = Color(0xFFD8DEE9),
       divider = Color(0xFFE5E9F0)
   )
   ```
3. Add the mapping inside `fun getThemeColors(theme: AppTheme)` in `Color.kt`.
4. The new theme will immediately show up in the `SettingsScreen` theme picker and dynamically re-theme the entire application.

---

### 9.3 Recipe: How to Add a New Room Entity
1. Create the entity file in `composeApp/src/main/kotlin/com/focusdesk/app/data/local/entity/`.
2. Annotate with `@Entity(tableName = "your_table_name")`.
3. Create the corresponding `@Dao` interface in `dao/`.
4. Register the entity in [FocusDeskDatabase.kt](file:///c:/Users/Admin/Documents/FocusDesk/composeApp/src/main/kotlin/com/focusdesk/app/data/local/FocusDeskDatabase.kt):
   ```kotlin
   @Database(
       entities = [
           FocusSessionEntity::class,
           UserSettingsEntity::class,
           TaskEntity::class,
           YourNewEntity::class // Add here
       ],
       version = 1,
       exportSchema = false
   )
   ```
5. Register the DAO in [AppModule.kt](file:///c:/Users/Admin/Documents/FocusDesk/composeApp/src/main/kotlin/com/focusdesk/app/di/AppModule.kt):
   ```kotlin
   single { get<FocusDeskDatabase>().yourNewDao() }
   ```

---

### 9.4 Critical Gotchas & Developer Tips

#### 1. Double-Ticking in Multiple Observers (The 850ms Guard)
**Gotcha**: Both `TimerViewModel` (via `startTickingLoop()`) and `DynamicIslandService` observe the current focus session. If both layers were to issue tick calls independently, the timer could run at 2x speed.  
**Solution**: `TimerEngineUseCase.tick()` enforces:
```kotlin
val now = System.currentTimeMillis()
if (!force && lastTickEpochMs > 0 && (now - lastTickEpochMs) < 850L) {
    return current // Drop redundant tick
}
lastTickEpochMs = now
```
Always keep this debounce guard in place!

#### 2. AudioTrack Thread Safety
Never invoke `AudioTrack.write()` from the Android Main thread. Audio synthesis must always execute inside the dedicated background thread (`audioThread = thread(isDaemon = true)`). Writing audio on the main thread causes UI stutter and ANR crashes.

#### 3. Room Recomposition Optimization
When observing Room data in Compose UI, always use `collectAsStateWithLifecycle()` from `androidx.lifecycle.compose`. This ensures Room queries and coroutine flows are automatically paused when the app moves to the background, preventing battery drain.

#### 4. Running Unit Tests
Execute all unit tests across KMP and Android targets:
```bash
./gradlew testDebugUnitTest
```

---
*Manual compiled for FocusDesk development team.*
