package com.focusdesk.data.repository

import com.focusdesk.domain.model.UserSettings
import com.focusdesk.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemorySettingsRepository : SettingsRepository {

    private val _settings = MutableStateFlow(UserSettings())

    override fun observeSettings(): Flow<UserSettings> = _settings.asStateFlow()

    override suspend fun getSettings(): UserSettings = _settings.value

    override suspend fun updateSettings(settings: UserSettings) {
        _settings.value = settings
    }
}
