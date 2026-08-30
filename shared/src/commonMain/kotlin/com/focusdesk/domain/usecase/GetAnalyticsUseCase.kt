package com.focusdesk.domain.usecase

import com.focusdesk.domain.model.ProductivityStats
import com.focusdesk.domain.repository.FocusRepository
import kotlinx.coroutines.flow.Flow

class GetAnalyticsUseCase(
    private val focusRepository: FocusRepository
) {
    fun observeStats(): Flow<ProductivityStats> = focusRepository.observeProductivityStats()

    suspend fun resetStats() {
        focusRepository.resetAllStats()
    }
}
