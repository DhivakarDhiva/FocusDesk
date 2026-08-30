# FocusDesk - Kotlin Multiplatform (KMP) & Jetpack Compose

A production-grade, compile-ready Kotlin Multiplatform (KMP) project built with strict **Clean Architecture (Domain, Data, Presentation)** and **MVI (Model-View-Intent)** unidirectional data flow.

---

## 🏛️ Clean Architecture Breakdown

```
FocusDesk/
├── 🔵 DOMAIN LAYER (Business Logic & Entities — Pure Kotlin, 100% Platform-Agnostic)
│   ├── Models: FocusSession, TaskItem, ProductivityStats, UserSettings
│   ├── Repository Interfaces: FocusRepository, TaskRepository, SettingsRepository
│   └── Use Cases: TimerEngineUseCase, ManageTasksUseCase, GetAnalyticsUseCase
│
├── 🟢 DATA LAYER (Data Management & Storage)
│   └── Repository Implementations: InMemoryFocusRepository, InMemoryTaskRepository, InMemorySettingsRepository
│
├── 🟣 PRESENTATION LAYER (MVI State Machine & Jetpack Compose UI)
│   ├── Shared MVI Contracts & ViewModels:
│   │   ├── Timer: TimerContract (State, Intent, Effect) + TimerViewModel
│   │   ├── Tasks: TasksContract (State, Intent, Effect) + TasksViewModel
│   │   ├── Analytics: AnalyticsContract (State, Intent, Effect) + AnalyticsViewModel
│   │   └── Settings: SettingsContract (State, Intent, Effect) + SettingsViewModel
│   └── Android Compose UI:
│       ├── Design System: Color, Type, Shape, Motion (1:1 SwiftUI springs), Theme
│       ├── Reusable Components: GlassCard, PulsingGlow, CircularProgressRing, TagChip, EmptyStateView
│       ├── Screens: TimerScreen, TasksScreen, AnalyticsScreen, SettingsScreen
│       └── Navigation: Screen (Routes), FocusDeskNavGraph, FocusDeskBottomNavBar
│
└── ⚪ CORE & PLATFORM LAYER (Bridges & Dependency Injection)
    ├── MVI Base: MviState, MviIntent, MviEffect, MviViewModel
    ├── Platform Bridges (expect/actual): HapticEngine, AmbientAudioEngine
    └── Dependency Injection: SharedModule (Koin)
```

---

## 📐 Detailed Layer-by-Layer File Guide

### 1. 🔵 Domain Layer (`:shared/src/commonMain/kotlin/com/focusdesk/domain/`)
- **[FocusSession.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/model/FocusSession.kt)**: Focus session entity, modes (`Work`, `ShortBreak`, `LongBreak`), status, and progress computation.
- **[TaskItem.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/model/TaskItem.kt)**: Desk task entity with priorities (`Urgent`, `High`, `Medium`, `Low`), categories, and pomodoro estimates.
- **[ProductivityStats.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/model/ProductivityStats.kt)**: Aggregated daily/weekly metrics and streak counters.
- **[UserSettings.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/model/UserSettings.kt)**: Durations, soundscapes, automation toggles, and theme options.
- **[FocusRepository.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/repository/FocusRepository.kt)**, **[TaskRepository.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/repository/TaskRepository.kt)**, **[SettingsRepository.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/repository/SettingsRepository.kt)**: Clean repository contracts.
- **[TimerEngineUseCase.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/usecase/TimerEngineUseCase.kt)**: Session ticking, countdown math, auto-transitioning between Work & Breaks, and task binding.
- **[ManageTasksUseCase.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/usecase/ManageTasksUseCase.kt)**: Task CRUD operations, filtering, and completion toggles.
- **[GetAnalyticsUseCase.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/domain/usecase/GetAnalyticsUseCase.kt)**: Productivity reporting and metric resets.

---

### 2. 🟢 Data Layer (`:shared/src/commonMain/kotlin/com/focusdesk/data/`)
- **[InMemoryFocusRepository.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/data/repository/InMemoryFocusRepository.kt)**: Reactive StateFlow implementation with initial mock history and weekly activity distribution.
- **[InMemoryTaskRepository.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/data/repository/InMemoryTaskRepository.kt)**: Thread-safe in-memory task repository with pre-seeded task items.
- **[InMemorySettingsRepository.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/data/repository/InMemorySettingsRepository.kt)**: User preference persistence.

---

### 3. 🟣 Presentation Layer
#### A. MVI ViewModels & Contracts (`:shared/src/commonMain/kotlin/com/focusdesk/presentation/`)
- **Timer**: [TimerContract.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/presentation/timer/TimerContract.kt) & [TimerViewModel.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/presentation/timer/TimerViewModel.kt)
- **Tasks**: [TasksContract.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/presentation/tasks/TasksContract.kt) & [TasksViewModel.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/presentation/tasks/TasksViewModel.kt)
- **Analytics**: [AnalyticsContract.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/presentation/analytics/AnalyticsContract.kt) & [AnalyticsViewModel.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/presentation/analytics/AnalyticsViewModel.kt)
- **Settings**: [SettingsContract.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/presentation/settings/SettingsContract.kt) & [SettingsViewModel.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/presentation/settings/SettingsViewModel.kt)

#### B. Jetpack Compose UI (`:composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/`)
- **Design System**: [Color.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/designsystem/Color.kt), [Type.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/designsystem/Type.kt), [Shape.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/designsystem/Shape.kt), [Motion.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/designsystem/Motion.kt), [Theme.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/designsystem/Theme.kt).
- **Reusable Components**: [GlassCard.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/components/GlassCard.kt), [CircularProgressRing.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/components/CircularProgressRing.kt), [PulsingGlow.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/components/PulsingGlow.kt), [TagChip.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/components/TagChip.kt), [EmptyStateView.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/components/EmptyStateView.kt).
- **Screens**:
  - [TimerScreen.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/timer/TimerScreen.kt)
  - [TasksScreen.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/tasks/TasksScreen.kt)
  - [AnalyticsScreen.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/analytics/AnalyticsScreen.kt)
  - [SettingsScreen.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/settings/SettingsScreen.kt)
- **Navigation**: [Screen.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/navigation/Screen.kt), [FocusDeskNavGraph.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/navigation/FocusDeskNavGraph.kt), [FocusDeskBottomNavBar.kt](file:///C:/Users/Admin/Documents/FocusDesk/composeApp/src/androidMain/kotlin/com/focusdesk/app/presentation/navigation/FocusDeskBottomNavBar.kt).

---

### 4. ⚪ Core & Platform Layer
- **MVI Base**: [MviState.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/core/mvi/MviState.kt), [MviIntent.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/core/mvi/MviIntent.kt), [MviEffect.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/core/mvi/MviEffect.kt), [MviViewModel.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/core/mvi/MviViewModel.kt).
- **Haptics (`expect`/`actual`)**: [Haptics.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/core/platform/Haptics.kt), [AndroidHaptics.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/androidMain/kotlin/com/focusdesk/core/platform/AndroidHaptics.kt), [IosHaptics.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/iosMain/kotlin/com/focusdesk/core/platform/IosHaptics.kt).
- **Ambient Audio (`expect`/`actual`)**: [AudioPlayer.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/core/platform/AudioPlayer.kt), [AndroidAudioPlayer.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/androidMain/kotlin/com/focusdesk/core/platform/AndroidAudioPlayer.kt), [IosAudioPlayer.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/iosMain/kotlin/com/focusdesk/core/platform/IosAudioPlayer.kt).
- **Dependency Injection**: [SharedModule.kt](file:///C:/Users/Admin/Documents/FocusDesk/shared/src/commonMain/kotlin/com/focusdesk/core/di/SharedModule.kt).

---

## 🛠️ Build & Run Commands
```bash
# Build Android APK
./gradlew assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest
```
