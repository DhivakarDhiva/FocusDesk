package com.focusdesk.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focusdesk.domain.model.AppTheme
import com.focusdesk.domain.model.Soundscape
import com.focusdesk.domain.model.TimerIntervals
import com.focusdesk.domain.model.UserSettings

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val workDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val sessionsBeforeLongBreak: Int = 4,
    val autoStartBreaks: Boolean = false,
    val autoStartWork: Boolean = false,
    val soundscape: String = "None",
    val soundscapeVolume: Float = 0.65f,
    val tickSoundsEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val appTheme: String = "DefaultFocus",
    val dailyGoalHours: Int = 4,
    val isOnboardingCompleted: Boolean = false,
    val xp: Int = 0,
    val level: Int = 1,
    val dayStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastActiveEpochDay: Long = 0L
)

fun UserSettingsEntity.toDomain(): UserSettings {
    return UserSettings(
        intervals = TimerIntervals(
            workDurationMinutes = workDurationMinutes,
            shortBreakDurationMinutes = shortBreakDurationMinutes,
            longBreakDurationMinutes = longBreakDurationMinutes,
            sessionsBeforeLongBreak = sessionsBeforeLongBreak
        ),
        autoStartBreaks = autoStartBreaks,
        autoStartWork = autoStartWork,
        soundscape = runCatching { Soundscape.valueOf(soundscape) }.getOrDefault(Soundscape.None),
        soundscapeVolume = soundscapeVolume,
        tickSoundsEnabled = tickSoundsEnabled,
        hapticsEnabled = hapticsEnabled,
        notificationsEnabled = notificationsEnabled,
        appTheme = runCatching { AppTheme.valueOf(appTheme) }.getOrDefault(AppTheme.DefaultFocus),
        dailyGoalHours = dailyGoalHours,
        isOnboardingCompleted = isOnboardingCompleted,
        xp = xp,
        level = level,
        dayStreak = dayStreak,
        bestStreak = bestStreak
    )
}

fun UserSettings.toEntity(id: Int = 1, lastActiveEpochDay: Long = 0L): UserSettingsEntity {
    return UserSettingsEntity(
        id = id,
        workDurationMinutes = intervals.workDurationMinutes,
        shortBreakDurationMinutes = intervals.shortBreakDurationMinutes,
        longBreakDurationMinutes = intervals.longBreakDurationMinutes,
        sessionsBeforeLongBreak = intervals.sessionsBeforeLongBreak,
        autoStartBreaks = autoStartBreaks,
        autoStartWork = autoStartWork,
        soundscape = soundscape.name,
        soundscapeVolume = soundscapeVolume,
        tickSoundsEnabled = tickSoundsEnabled,
        hapticsEnabled = hapticsEnabled,
        notificationsEnabled = notificationsEnabled,
        appTheme = appTheme.name,
        dailyGoalHours = dailyGoalHours,
        isOnboardingCompleted = isOnboardingCompleted,
        xp = xp,
        level = level,
        dayStreak = dayStreak,
        bestStreak = bestStreak,
        lastActiveEpochDay = lastActiveEpochDay
    )
}
