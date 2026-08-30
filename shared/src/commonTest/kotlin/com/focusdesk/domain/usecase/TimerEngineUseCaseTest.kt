package com.focusdesk.domain.usecase

import com.focusdesk.data.repository.InMemoryFocusRepository
import com.focusdesk.data.repository.InMemorySettingsRepository
import com.focusdesk.data.repository.InMemoryTaskRepository
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TimerEngineUseCaseTest {

    private lateinit var focusRepository: InMemoryFocusRepository
    private lateinit var taskRepository: InMemoryTaskRepository
    private lateinit var settingsRepository: InMemorySettingsRepository
    private lateinit var timerEngineUseCase: TimerEngineUseCase

    @BeforeTest
    fun setUp() {
        focusRepository = InMemoryFocusRepository()
        taskRepository = InMemoryTaskRepository()
        settingsRepository = InMemorySettingsRepository()
        timerEngineUseCase = TimerEngineUseCase(focusRepository, taskRepository, settingsRepository)
    }

    @Test
    fun `test initial timer session state is Idle`() = runTest {
        val session = focusRepository.getCurrentSession()
        assertEquals(SessionStatus.Idle, session.status)
        assertEquals(SessionMode.Work, session.mode)
        assertEquals(25 * 60L, session.remainingSeconds)
    }

    @Test
    fun `test starting and pausing timer transitions state properly`() = runTest {
        timerEngineUseCase.startTimer()
        var session = focusRepository.getCurrentSession()
        assertEquals(SessionStatus.Running, session.status)

        timerEngineUseCase.pauseTimer()
        session = focusRepository.getCurrentSession()
        assertEquals(SessionStatus.Paused, session.status)
    }

    @Test
    fun `test mode switching resets target duration`() = runTest {
        timerEngineUseCase.switchMode(SessionMode.ShortBreak)
        val session = focusRepository.getCurrentSession()
        assertEquals(SessionMode.ShortBreak, session.mode)
        assertEquals(5 * 60L, session.remainingSeconds)
        assertEquals(SessionStatus.Idle, session.status)
    }

    @Test
    fun `test timer ticking decrements remaining time`() = runTest {
        timerEngineUseCase.startTimer()
        val initialRemaining = focusRepository.getCurrentSession().remainingSeconds

        val updated = timerEngineUseCase.tick()
        assertEquals(initialRemaining - 1, updated.remainingSeconds)
    }

    @Test
    fun `test attach task links task title to session`() = runTest {
        timerEngineUseCase.attachTask("test_1", "Design System Spec")
        val session = focusRepository.getCurrentSession()
        assertEquals("test_1", session.associatedTaskId)
        assertEquals("Design System Spec", session.associatedTaskTitle)
    }

    @Test
    fun `test resetting timer restores initial duration`() = runTest {
        timerEngineUseCase.startTimer()
        timerEngineUseCase.tick()
        timerEngineUseCase.resetTimer()

        val session = focusRepository.getCurrentSession()
        assertEquals(SessionStatus.Idle, session.status)
        assertEquals(25 * 60L, session.remainingSeconds)
    }
}
