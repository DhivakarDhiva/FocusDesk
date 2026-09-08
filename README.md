# 🎯 FocusDesk

<div align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-2025.01.00-4285F4.svg?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20Ready-3DDC84.svg?style=for-the-badge&logo=android&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI-FF6F00.svg?style=for-the-badge)

**A modern, production-grade productivity & deep-work desk companion built with Kotlin Multiplatform (KMP), Jetpack Compose, Room Database, and native Dynamic Island / Live Activity integration.**

[Key Features](#-key-features) • [Architecture](#-clean-architecture--mvi) • [Tech Stack](#-tech-stack) • [Project Structure](#-project-structure) • [Screens](#-application-tour) • [Getting Started](#-getting-started)

</div>

---

## 🌟 Overview

**FocusDesk** is engineered for high-performance deep work sessions. Built with strict **Clean Architecture (Domain, Data, Presentation)** and **MVI (Model-View-Intent)** unidirectional data flow, it combines a sleek glassmorphic tactile UI with advanced Android system capabilities—including system status-bar Dynamic Island live chips, real-time procedural audio synthesis for focus soundscapes, and persistent offline-first Room storage.

---

## ✨ Key Features

### 🏝️ Dynamic Island & Live Status Activities
- **System-Native Dynamic Island**: Hosts an active foreground `MediaSession` and `Notification.MediaStyle` to project live session countdowns directly into Android status bar chips and punch-hole islands (ColorOS/OxygenOS Aqua Dynamics, Xiaomi HyperOS Island, Samsung One UI, and Android 14/15 Live Notifications).
- **Background Persistence**: Interactive actions allow users to pause, resume, or skip sessions directly from the notification shade or lock screen.

### ⏱️ Precision Focus Engine (Pomodoro & Deep Work)
- **State Machine Timer**: Seamlessly alternates between **Deep Work**, **Short Break**, and **Long Break** intervals.
- **Configurable Intervals**: Full customization of work periods, break durations, and long break cycle thresholds.
- **Auto-Transitions**: Optional auto-start for break periods and work sessions with subtle haptic cues.

### 🎧 Procedural Ambient Audio Synthesizer
- **On-Device Audio Generation**: Synthesizes ambient noise in real-time (Brown Noise, White Noise, Pink Noise) alongside nature soundscapes (Rain, Thunderstorm, Forest, Ocean, Coffee Shop, Library, Lo-Fi).
- **Tactile Volume Control**: Fluid elevated slider components for dynamic background audio mixing during focus intervals.

### 📊 Deep Productivity Analytics & Gamification
- **Session History & Heatmaps**: Daily and weekly focus distribution bar charts.
- **Task & Category Tracking**: Tag-based analytics to measure focus time spent across projects.
- **Gamified Consistency**: XP progression, level milestones, current day streaks, and best streak records.

### 🎨 Premium Tactile Design System
- **Dark-Themed Glassmorphism**: Translucent card elevations, glowing neon accent rings, and smooth spring physics.
- **Multi-Palette Theme Engine**: 5 bespoke color palettes (*Default Focus*, *Paper Studio*, *Low Tide*, *Last Light*, *Night Bloom*).
- **Fluid Spring Animations**: Micro-interactions fine-tuned with custom stiffness and damping ratios.

### 💾 Offline-First Architecture
- **Room Database**: Complete local persistence with SQLite Room, reactive Kotlin `StateFlow` queries, and automated domain entity mappers.

---

## 🏛️ Clean Architecture & MVI

The project adheres strictly to **Clean Architecture** with a **Unidirectional Data Flow (MVI)** pattern across layers:

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                      │
│   Jetpack Compose UI  ◄──────►  MVI ViewModel (StateFlow)   │
│   (Screens, Components, Themes) (State, Intent, Effect)     │
└──────────────────────────────┬──────────────────────────────┘
                               │ invokes
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                        DOMAIN LAYER                         │
│       Use Cases (TimerEngine, ManageTasks, GetAnalytics)    │
│            Repository Interfaces & Domain Entities          │
│                (100% Pure Kotlin Multiplatform)             │
└──────────────────────────────▲──────────────────────────────┘
                               │ implements
                               │
┌──────────────────────────────┴──────────────────────────────┐
│                         DATA LAYER                          │
│   Room Database (DAOs, Entities)  │  In-Memory / Remote API │
│   Platform Bridges (AudioPlayer, Haptics expect/actual)     │
└─────────────────────────────────────────────────────────────┘
```

### MVI Unidirectional Data Flow Cycle
1. **User Action / Event** triggers an `MviIntent` (e.g., `TimerIntent.StartSession`).
2. **ViewModel / UseCase** processes business logic and emits an updated immutable `MviState` (e.g., `TimerState`).
3. **UI Observes State** via `collectAsStateWithLifecycle()` and automatically recomposes.
4. **Side Effects** (haptics, navigation, dialogs) fire as one-time `MviEffect` channels.

---

## 🛠️ Tech Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | [Kotlin 2.1.0](https://kotlinlang.org/) | Multiplatform-ready language & coroutines |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) | Declarative UI with Compose BOM `2025.01.00` |
| **Design System** | [Material 3](https://m3.material.io/) | Enhanced with custom glassmorphism and tactile components |
| **Database** | [AndroidX Room 2.6.1](https://developer.android.com/training/data-storage/room) | SQLite abstraction layer with KSP code generation |
| **Dependency Injection** | [Koin 4.0.2](https://insert-koin.io/) | Lightweight multiplatform & Android dependency injection |
| **Concurrency** | [Kotlinx Coroutines 1.10.1](https://github.com/Kotlin/kotlinx.coroutines) | Structured concurrency, `Flow`, and `StateFlow` |
| **Background & Overlay**| Android Foreground Service + `MediaSession` | Dynamic Island status bar activity support |
| **Platform Target** | Android SDK 35 (Min SDK 26) / iOS Framework | Cross-platform framework binary configured |

---

## 📁 Project Structure

```
FocusDesk/
├── composeApp/                                # Android Application Module
│   └── src/main/
│       ├── AndroidManifest.xml                # Permissions & DynamicIslandService declaration
│       ├── kotlin/com/focusdesk/app/
│       │   ├── FocusDeskApplication.kt        # Koin DI initialization
│       │   ├── MainActivity.kt                # Root activity & navigation host
│       │   ├── data/                          # Data Layer implementations
│       │   │   ├── local/
│       │   │   │   ├── FocusDeskDatabase.kt   # Room Database definition
│       │   │   │   ├── dao/                   # FocusSessionDao, TaskDao, UserSettingsDao
│       │   │   │   └── entity/                # Room entities & TypeConverters
│       │   │   └── repository/                # RoomFocusRepository, RoomSettingsRepository...
│       │   ├── di/
│       │   │   └── AppModule.kt               # Android & Room Koin module
│       │   ├── presentation/                  # UI & Presentation Layer
│       │   │   ├── analytics/                 # Insights screen, charts, session history
│       │   │   ├── designsystem/              # Color, Type, Shape, Motion, Tactile Slider
│       │   │   ├── focus/                     # Active focus view & session creation sheet
│       │   │   ├── home/                      # Dashboard, streak counter, quick start
│       │   │   ├── navigation/                # Bottom navigation bar & Screen routes
│       │   │   ├── onboarding/                # Guided interactive onboarding experience
│       │   │   ├── settings/                  # Timer interval sliders, sounds, theme picker
│       │   │   ├── sounds/                    # Ambient soundscapes & audio mixer
│       │   │   └── timer/                     # Pomodoro timer, circular progress, complete screen
│       │   └── service/
│       │       ├── DynamicIslandService.kt    # Foreground service with MediaSession
│       │       └── DynamicIslandActionReceiver.kt # Broadcast receiver for notification actions
│       └── res/                               # Icons, vector drawables, themes, strings
│
└── shared/                                    # Kotlin Multiplatform Shared Core Module
    └── src/
        ├── commonMain/kotlin/com/focusdesk/
        │   ├── core/
        │   │   ├── di/SharedModule.kt         # Platform-independent DI
        │   │   ├── mvi/                       # MviState, MviIntent, MviEffect, MviViewModel
        │   │   └── platform/                  # expect declarations (AudioPlayer, Haptics)
        │   ├── data/repository/               # Domain repository interfaces & in-memory mocks
        │   └── domain/
        │       ├── model/                     # FocusSession, TaskItem, UserSettings, Soundscape
        │       └── usecase/                   # TimerEngineUseCase, ManageTasksUseCase, etc.
        ├── androidMain/kotlin/com/focusdesk/
        │   └── core/platform/                 # actual implementations (AndroidAudioPlayer, AndroidHaptics)
        └── iosMain/kotlin/com/focusdesk/
            └── core/platform/                 # actual implementations (IosAudioPlayer, IosHaptics)
```

---

## 📱 Application Tour

| **Dashboard (Home)** | **Active Focus & Timer** | **Ambient Soundscapes** |
|:---:|:---:|:---:|
| Daily target ring, streaks, XP level gamification, and quick presets. | High-precision circular timer with tactile pause, resume, and skip controls. | Real-time procedural white/pink/brown noise and nature sound mixing. |

| **Productivity Insights** | **Theme & Timer Settings** | **Live Status Dynamic Island** |
|:---:|:---:|:---:|
| Weekly activity distribution, completion ratios, and deep session logs. | Fine-grained intervals, automated breaks, and 5 bespoke color palettes. | Status bar punch-hole live activity chip with remote lockscreen controls. |

---

## 🚀 Getting Started

### Prerequisites
- **JDK**: Version 17 or higher
- **Android Studio**: Ladybug (2024.2+) or newer
- **Android SDK**: API 35 (Compile), API 26+ (Minimum)

### Clone & Open
```bash
git clone https://github.com/DhivakarDhiva/FocusDesk.git
cd FocusDesk
```

### Build & Run Android App
```bash
# Build Debug APK
./gradlew assembleDebug

# Install and run on connected device/emulator
./gradlew installDebug
```

### Run Tests
```bash
# Run unit tests across shared and Android modules
./gradlew testDebugUnitTest
```

---

## 🧪 Testing Strategy

FocusDesk is designed with testability at its core:
- **Domain Use Cases**: Tested with deterministic coroutine test dispatchers (`TimerEngineUseCaseTest`, `GetAnalyticsUseCaseTest`).
- **Data Layer Mappers**: Tested to guarantee bi-directional accuracy between Room database entities and Domain models (`RoomEntityMapperTest`).
- **MVI ViewModels**: Verified via event-driven intent testing (`TimerViewModelTest`).

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
