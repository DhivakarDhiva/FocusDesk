package com.focusdesk.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.focusdesk.app.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Query("SELECT * FROM focus_sessions ORDER BY startedAtTimestamp DESC")
    fun observeAllSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE status = 'Completed' ORDER BY completedAtTimestamp DESC")
    fun observeCompletedSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE status = 'Completed' ORDER BY completedAtTimestamp DESC LIMIT :limit")
    fun observeRecentCompletedSessions(limit: Int = 20): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: String): FocusSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: FocusSessionEntity)

    @Query("DELETE FROM focus_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: String)

    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT * FROM focus_sessions WHERE status = 'Completed' AND completedAtTimestamp >= :startMs AND completedAtTimestamp <= :endMs")
    suspend fun getCompletedSessionsBetween(startMs: Long, endMs: Long): List<FocusSessionEntity>

    @Query("SELECT * FROM focus_sessions WHERE status = 'Completed' AND completedAtTimestamp >= :startMs AND completedAtTimestamp <= :endMs")
    fun observeCompletedSessionsBetween(startMs: Long, endMs: Long): Flow<List<FocusSessionEntity>>
}
