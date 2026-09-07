package com.focusdesk.domain.model

enum class Soundscape(
    val title: String,
    val isPremium: Boolean = false,
    val category: String = "Nature"
) {
    None("None"),
    Rain("Rain", isPremium = false, category = "Nature"),
    HeavyRain("Heavy Rain", isPremium = true, category = "Nature"),
    Forest("Forest", isPremium = false, category = "Nature"),
    Ocean("Ocean", isPremium = false, category = "Nature"),
    Thunderstorm("Thunderstorm", isPremium = true, category = "Nature"),
    Birdsong("Birdsong", isPremium = false, category = "Nature"),
    CoffeeShop("Coffee Shop", isPremium = false, category = "Indoor"),
    Fireplace("Fireplace", isPremium = false, category = "Indoor"),
    Library("Library", isPremium = true, category = "Indoor"),
    BrownNoise("Brown Noise", isPremium = false, category = "Noise"),
    WhiteNoise("White Noise", isPremium = false, category = "Noise"),
    PinkNoise("Pink Noise", isPremium = true, category = "Noise"),
    LofiBeats("Lo-Fi Beats", isPremium = true, category = "Music"),
    Classical("Classical", isPremium = true, category = "Music")
}

enum class AppTheme(val displayName: String) {
    DefaultFocus("Default Focus"),
    PaperStudio("Paper Studio"),
    LowTide("Low Tide"),
    LastLight("Last Light"),
    NightBloom("Night Bloom")
}

data class TimerIntervals(
    val workDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val sessionsBeforeLongBreak: Int = 4
) {
    val workDurationSeconds: Long get() = workDurationMinutes * 60L
    val shortBreakDurationSeconds: Long get() = shortBreakDurationMinutes * 60L
    val longBreakDurationSeconds: Long get() = longBreakDurationMinutes * 60L
}

data class UserSettings(
    val intervals: TimerIntervals = TimerIntervals(),
    val autoStartBreaks: Boolean = false,
    val autoStartWork: Boolean = false,
    val soundscape: Soundscape = Soundscape.None,
    val soundscapeVolume: Float = 0.65f,
    val tickSoundsEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val appTheme: AppTheme = AppTheme.DefaultFocus,
    val dailyGoalHours: Int = 4,
    val isOnboardingCompleted: Boolean = false,
    val level: Int = 1,
    val xp: Int = 0,
    val dayStreak: Int = 0,
    val bestStreak: Int = 0
)
