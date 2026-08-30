package com.focusdesk.app.presentation.settings

import com.focusdesk.core.mvi.MviEffect
import com.focusdesk.core.mvi.MviIntent
import com.focusdesk.core.mvi.MviState
import com.focusdesk.domain.model.AppThemeMode
import com.focusdesk.domain.model.Soundscape
import com.focusdesk.domain.model.UserSettings

data class SettingsState(
    val settings: UserSettings = UserSettings(),
    val isDirty: Boolean = false
) : MviState

sealed interface SettingsIntent : MviIntent {
    data class UpdateWorkDuration(val minutes: Int) : SettingsIntent
    data class UpdateShortBreakDuration(val minutes: Int) : SettingsIntent
    data class UpdateLongBreakDuration(val minutes: Int) : SettingsIntent
    data class UpdateSessionsBeforeLongBreak(val count: Int) : SettingsIntent
    data class UpdateAutoStartBreaks(val enabled: Boolean) : SettingsIntent
    data class UpdateAutoStartWork(val enabled: Boolean) : SettingsIntent
    data class UpdateSoundscape(val soundscape: Soundscape) : SettingsIntent
    data class UpdateSoundscapeVolume(val volume: Float) : SettingsIntent
    data class UpdateTickSounds(val enabled: Boolean) : SettingsIntent
    data class UpdateHaptics(val enabled: Boolean) : SettingsIntent
    data class UpdateThemeMode(val themeMode: AppThemeMode) : SettingsIntent
    data class UpdateDynamicColor(val enabled: Boolean) : SettingsIntent
    data object ResetToDefaults : SettingsIntent
}

sealed interface SettingsEffect : MviEffect {
    data class ShowSnackbar(val message: String) : SettingsEffect
    data object PlayFeedbackHaptic : SettingsEffect
}
