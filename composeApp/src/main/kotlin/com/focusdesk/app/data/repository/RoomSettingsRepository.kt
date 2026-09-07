package com.focusdesk.app.data.repository

import com.focusdesk.app.data.local.dao.UserSettingsDao
import com.focusdesk.app.data.local.entity.UserSettingsEntity
import com.focusdesk.app.data.local.entity.toDomain
import com.focusdesk.app.data.local.entity.toEntity
import com.focusdesk.domain.model.UserSettings
import com.focusdesk.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomSettingsRepository(
    private val userSettingsDao: UserSettingsDao
) : SettingsRepository {

    override fun observeSettings(): Flow<UserSettings> {
        return userSettingsDao.observeSettings().map { entity ->
            entity?.toDomain() ?: UserSettings()
        }
    }

    override suspend fun getSettings(): UserSettings {
        val entity = userSettingsDao.getSettings()
        return if (entity != null) {
            entity.toDomain()
        } else {
            val defaultEntity = UserSettingsEntity()
            userSettingsDao.upsertSettings(defaultEntity)
            defaultEntity.toDomain()
        }
    }

    override suspend fun updateSettings(settings: UserSettings) {
        val existing = userSettingsDao.getSettings()
        val lastActive = existing?.lastActiveEpochDay ?: 0L
        userSettingsDao.upsertSettings(settings.toEntity(id = 1, lastActiveEpochDay = lastActive))
    }
}
