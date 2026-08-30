package com.focusdesk.domain.repository

import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.ProductivityStats
import kotlinx.coroutines.flow.Flow

interface FocusRepository {
    fun observeCurrentSession(): Flow<FocusSession>
    suspend fun getCurrentSession(): FocusSession
    suspend fun updateCurrentSession(session: FocusSession)
    suspend fun recordCompletedSession(session: FocusSession)
    fun observeProductivityStats(): Flow<ProductivityStats>
    suspend fun resetAllStats()
}
