package com.focusdesk.app.presentation.settings

import androidx.lifecycle.viewModelScope
import com.focusdesk.core.mvi.MviViewModel
import com.focusdesk.domain.model.UserSettings
import com.focusdesk.domain.repository.FocusRepository
import com.focusdesk.domain.repository.SettingsRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val focusRepository: FocusRepository
) : MviViewModel<SettingsIntent, SettingsState, SettingsEffect>(SettingsState()) {

    init {
        viewModelScope.launch {
            settingsRepository.observeSettings().collect { userSettings ->
                setState { copy(settings = userSettings) }
            }
        }
    }

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.UpdateAppTheme -> update { copy(appTheme = intent.appTheme) }
            is SettingsIntent.UpdateDailyGoal -> update { copy(dailyGoalHours = intent.hours) }
            is SettingsIntent.UpdateNotifications -> update { copy(notificationsEnabled = intent.enabled) }
            is SettingsIntent.UpdateHaptics -> update { copy(hapticsEnabled = intent.enabled) }
            is SettingsIntent.UpdateSoundscape -> update { copy(soundscape = intent.soundscape) }
            is SettingsIntent.UpdateSoundscapeVolume -> update { copy(soundscapeVolume = intent.volume) }
            is SettingsIntent.CompleteOnboarding -> update { copy(isOnboardingCompleted = true) }
            is SettingsIntent.ResetAllData -> {
                viewModelScope.launch {
                    focusRepository.resetAllStats()
                    val resetSettings = UserSettings(
                        isOnboardingCompleted = false
                    )
                    settingsRepository.updateSettings(resetSettings)
                    setState { copy(settings = resetSettings) }
                    setEffect { SettingsEffect.ShowSnackbar("All data reset successfully") }
                }
            }
            is SettingsIntent.ResetToDefaults -> {
                viewModelScope.launch {
                    val defaults = UserSettings()
                    settingsRepository.updateSettings(defaults)
                    setState { copy(settings = defaults) }
                    setEffect { SettingsEffect.ShowSnackbar("Settings reset to defaults") }
                    setEffect { SettingsEffect.PlayFeedbackHaptic }
                }
            }
        }
    }

    private fun update(transform: UserSettings.() -> UserSettings) {
        viewModelScope.launch {
            val updated = currentState.settings.transform()
            settingsRepository.updateSettings(updated)
            setState { copy(settings = updated) }
            setEffect { SettingsEffect.PlayFeedbackHaptic }
        }
    }
}
