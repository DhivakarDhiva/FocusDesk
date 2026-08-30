package com.focusdesk.app.presentation.tasks

import com.focusdesk.data.repository.InMemoryFocusRepository
import com.focusdesk.data.repository.InMemorySettingsRepository
import com.focusdesk.data.repository.InMemoryTaskRepository
import com.focusdesk.domain.model.TaskCategory
import com.focusdesk.domain.model.TaskPriority
import com.focusdesk.domain.usecase.ManageTasksUseCase
import com.focusdesk.domain.usecase.TimerEngineUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TasksViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var taskRepository: InMemoryTaskRepository
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var timerEngineUseCase: TimerEngineUseCase
    private lateinit var viewModel: TasksViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val focusRepository = InMemoryFocusRepository()
        val settingsRepository = InMemorySettingsRepository()
        taskRepository = InMemoryTaskRepository()
        manageTasksUseCase = ManageTasksUseCase(taskRepository)
        timerEngineUseCase = TimerEngineUseCase(focusRepository, taskRepository, settingsRepository)
        viewModel = TasksViewModel(manageTasksUseCase, timerEngineUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testFilterStateUpdates() {
        viewModel.onIntent(TasksIntent.SetFilter(TaskFilter.Completed))
        assertEquals(TaskFilter.Completed, viewModel.state.value.filter)

        val completedOnly = viewModel.state.value.filteredTasks
        assertTrue(completedOnly.all { it.isCompleted })
    }

    @Test
    fun testSearchFiltering() {
        viewModel.onIntent(TasksIntent.SearchQueryChanged("KMP"))
        val results = viewModel.state.value.filteredTasks
        assertTrue(results.all { it.title.contains("KMP", ignoreCase = true) || it.description.contains("KMP", ignoreCase = true) })
    }

    @Test
    fun testCreateNewTaskSubmission() = runTest {
        viewModel.onIntent(TasksIntent.OpenAddTaskSheet)
        assertTrue(viewModel.state.value.isAddTaskSheetOpen)

        viewModel.onIntent(TasksIntent.UpdateNewTaskTitle("Write Jetpack Compose UI Tests"))
        viewModel.onIntent(TasksIntent.UpdateNewTaskPriority(TaskPriority.High))
        viewModel.onIntent(TasksIntent.UpdateNewTaskCategory(TaskCategory.Coding))
        viewModel.onIntent(TasksIntent.UpdateNewTaskPomodoros(3))

        viewModel.onIntent(TasksIntent.SubmitNewTask)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.isAddTaskSheetOpen)
        assertTrue(viewModel.state.value.tasks.any { it.title == "Write Jetpack Compose UI Tests" })
    }
}
