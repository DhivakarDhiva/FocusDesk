package com.focusdesk.app.presentation.timer

import com.focusdesk.data.repository.InMemoryFocusRepository
import com.focusdesk.data.repository.InMemorySettingsRepository
import com.focusdesk.data.repository.InMemoryTaskRepository
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.usecase.TimerEngineUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var focusRepository: InMemoryFocusRepository
    private lateinit var taskRepository: InMemoryTaskRepository
    private lateinit var settingsRepository: InMemorySettingsRepository
    private lateinit var timerEngineUseCase: TimerEngineUseCase
    private lateinit var viewModel: TimerViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        focusRepository = InMemoryFocusRepository()
        taskRepository = InMemoryTaskRepository()
        settingsRepository = InMemorySettingsRepository()
        timerEngineUseCase = TimerEngineUseCase(focusRepository, taskRepository, settingsRepository)
        viewModel = TimerViewModel(timerEngineUseCase, taskRepository, settingsRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        val state = viewModel.state.value
        assertEquals(SessionMode.Work, state.session.mode)
        assertEquals(SessionStatus.Idle, state.session.status)
        assertEquals(25 * 60L, state.session.targetDurationSeconds)
    }

    @Test
    fun testSelectModeIntent() = runTest {
        viewModel.onIntent(TimerIntent.SelectMode(SessionMode.ShortBreak))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(SessionMode.ShortBreak, state.session.mode)
        assertEquals(5 * 60L, state.session.targetDurationSeconds)
    }

    @Test
    fun testToggleTimerIntent() = runTest {
        viewModel.onIntent(TimerIntent.ToggleTimer)
        testDispatcher.scheduler.runCurrent()

        val runningState = viewModel.state.value
        assertEquals(SessionStatus.Running, runningState.session.status)

        viewModel.onIntent(TimerIntent.ToggleTimer)
        testDispatcher.scheduler.runCurrent()

        val pausedState = viewModel.state.value
        assertEquals(SessionStatus.Paused, pausedState.session.status)
    }

    @Test
    fun testResetDialogInteractions() {
        viewModel.onIntent(TimerIntent.RequestReset)
        assertTrue(viewModel.state.value.isResetDialogOpen)

        viewModel.onIntent(TimerIntent.DismissResetDialog)
        assertTrue(!viewModel.state.value.isResetDialogOpen)
    }
}
