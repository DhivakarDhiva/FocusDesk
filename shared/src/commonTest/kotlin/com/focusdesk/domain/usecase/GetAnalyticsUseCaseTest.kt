package com.focusdesk.domain.usecase

import com.focusdesk.data.repository.InMemoryFocusRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetAnalyticsUseCaseTest {

    private lateinit var focusRepository: InMemoryFocusRepository
    private lateinit var getAnalyticsUseCase: GetAnalyticsUseCase

    @BeforeTest
    fun setUp() {
        focusRepository = InMemoryFocusRepository()
        getAnalyticsUseCase = GetAnalyticsUseCase(focusRepository)
    }

    @Test
    fun `test observing stats returns valid seed metrics`() = runTest {
        val stats = getAnalyticsUseCase.observeStats().first()
        assertEquals(0, stats.totalFocusMinutesToday)
        assertEquals(0, stats.currentDayStreak)
        assertEquals(7, stats.weeklyDistribution.size)
    }

    @Test
    fun `test resetting stats zeroes out metrics`() = runTest {
        getAnalyticsUseCase.resetStats()
        val stats = getAnalyticsUseCase.observeStats().first()
        assertEquals(0, stats.totalFocusMinutesToday)
        assertEquals(0, stats.totalFocusMinutesWeek)
        assertEquals(0, stats.currentDayStreak)
        assertEquals(0, stats.completedTasksToday)
    }
}
