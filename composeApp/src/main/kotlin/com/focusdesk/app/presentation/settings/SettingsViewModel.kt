package com.focusdesk.app.presentation.settings

import androidx.lifecycle.viewModelScope
import com.focusdesk.core.mvi.MviViewModel
import com.focusdesk.domain.model.UserSettings
import com.focusdesk.domain.repository.SettingsRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
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
            is SettingsIntent.UpdateWorkDuration -> update {
                copy(intervals = intervals.copy(workDurationMinutes = intent.minutes))
            }
            is SettingsIntent.UpdateShortBreakDuration -> update {
                copy(intervals = intervals.copy(shortBreakDurationMinutes = intent.minutes))
            }
            is SettingsIntent.UpdateLongBreakDuration -> update {
                copy(intervals = intervals.copy(longBreakDurationMinutes = intent.minutes))
            }
            is SettingsIntent.UpdateSessionsBeforeLongBreak -> update {
                copy(intervals = intervals.copy(sessionsBeforeLongBreak = intent.count))
            }
            is SettingsIntent.UpdateAutoStartBreaks -> update {
                copy(autoStartBreaks = intent.enabled)
            }
            is SettingsIntent.UpdateAutoStartWork -> update {
                copy(autoStartWork = intent.enabled)
            }
            is SettingsIntent.UpdateSoundscape -> update {
                copy(soundscape = intent.soundscape)
            }
            is SettingsIntent.UpdateSoundscapeVolume -> update {
                copy(soundscapeVolume = intent.volume)
            }
            is SettingsIntent.UpdateTickSounds -> update {
                copy(tickSoundsEnabled = intent.enabled)
            }
            is SettingsIntent.UpdateHaptics -> update {
                copy(hapticsEnabled = intent.enabled)
            }
            is SettingsIntent.UpdateThemeMode -> update {
                copy(themeMode = intent.themeMode)
            }
            is SettingsIntent.UpdateDynamicColor -> update {
                copy(useDynamicColor = intent.enabled)
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
