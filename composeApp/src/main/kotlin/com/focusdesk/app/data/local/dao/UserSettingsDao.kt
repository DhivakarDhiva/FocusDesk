package com.focusdesk.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.focusdesk.app.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun observeSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: UserSettingsEntity)

    @Query("UPDATE user_settings SET xp = :newXp, level = :newLevel, dayStreak = :dayStreak, bestStreak = :bestStreak, lastActiveEpochDay = :lastActiveEpochDay WHERE id = 1")
    suspend fun updateXpAndStreak(newXp: Int, newLevel: Int, dayStreak: Int, bestStreak: Int, lastActiveEpochDay: Long)

    @Query("UPDATE user_settings SET isOnboardingCompleted = :completed WHERE id = 1")
    suspend fun setOnboardingCompleted(completed: Boolean)

    @Query("DELETE FROM user_settings")
    suspend fun deleteAllSettings()
}
