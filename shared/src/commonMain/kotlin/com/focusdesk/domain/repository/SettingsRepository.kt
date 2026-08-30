package com.focusdesk.domain.repository

import com.focusdesk.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>
    suspend fun getSettings(): UserSettings
    suspend fun updateSettings(settings: UserSettings)
}
