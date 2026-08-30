package com.focusdesk.domain.model

enum class Soundscape(val title: String, val iconName: String) {
    None("None", "volume_off"),
    Rain("Rainfall", "water_drop"),
    Forest("Forest Birds", "forest"),
    Cafe("Coffee Shop", "local_cafe"),
    Ocean("Ocean Waves", "waves"),
    WhiteNoise("White Noise", "air")
}

enum class AppThemeMode {
    System,
    Light,
    Dark,
    OledBlack
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
    val soundscapeVolume: Float = 0.5f,
    val tickSoundsEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.System,
    val useDynamicColor: Boolean = true
)
