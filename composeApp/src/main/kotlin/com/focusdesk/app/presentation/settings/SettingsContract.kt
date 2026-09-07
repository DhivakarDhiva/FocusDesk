package com.focusdesk.app.presentation.settings

import com.focusdesk.core.mvi.MviEffect
import com.focusdesk.core.mvi.MviIntent
import com.focusdesk.core.mvi.MviState
import com.focusdesk.domain.model.AppTheme
import com.focusdesk.domain.model.Soundscape
import com.focusdesk.domain.model.UserSettings

data class SettingsState(
    val settings: UserSettings = UserSettings(),
    val isDirty: Boolean = false
) : MviState

sealed interface SettingsIntent : MviIntent {
    data class UpdateAppTheme(val appTheme: AppTheme) : SettingsIntent
    data class UpdateDailyGoal(val hours: Int) : SettingsIntent
    data class UpdateNotifications(val enabled: Boolean) : SettingsIntent
    data class UpdateHaptics(val enabled: Boolean) : SettingsIntent
    data class UpdateSoundscape(val soundscape: Soundscape) : SettingsIntent
    data class UpdateSoundscapeVolume(val volume: Float) : SettingsIntent
    data object CompleteOnboarding : SettingsIntent
    data object ResetAllData : SettingsIntent
    data object ResetToDefaults : SettingsIntent
}

sealed interface SettingsEffect : MviEffect {
    data class ShowSnackbar(val message: String) : SettingsEffect
    data object PlayFeedbackHaptic : SettingsEffect
}
