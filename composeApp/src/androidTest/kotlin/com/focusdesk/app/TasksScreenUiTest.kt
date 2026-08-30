package com.focusdesk.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.focusdesk.app.presentation.designsystem.FocusDeskTheme
import com.focusdesk.app.presentation.tasks.TasksScreen
import com.focusdesk.app.presentation.tasks.TasksViewModel
import com.focusdesk.data.repository.InMemoryFocusRepository
import com.focusdesk.data.repository.InMemorySettingsRepository
import com.focusdesk.data.repository.InMemoryTaskRepository
import com.focusdesk.domain.usecase.ManageTasksUseCase
import com.focusdesk.domain.usecase.TimerEngineUseCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasksScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: TasksViewModel

    @Before
    fun setUp() {
        val taskRepo = InMemoryTaskRepository()
        val focusRepo = InMemoryFocusRepository()
        val settingsRepo = InMemorySettingsRepository()
        val manageTasks = ManageTasksUseCase(taskRepo)
        val timerUseCase = TimerEngineUseCase(focusRepo, taskRepo, settingsRepo)
        viewModel = TasksViewModel(manageTasks, timerUseCase)
    }

    @Test
    fun testTasksScreenDisplaysHeaderAndFilters() {
        composeTestRule.setContent {
            FocusDeskTheme {
                TasksScreen(viewModel = viewModel, onNavigateToTimer = {})
            }
        }

        // Verify title
        composeTestRule.onNodeWithText("Desk Tasks").assertIsDisplayed()

        // Verify filter tabs
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active").assertIsDisplayed()
        composeTestRule.onNodeWithText("Done").assertIsDisplayed()

        // Verify FAB
        composeTestRule.onNodeWithContentDescription("Add Task").assertIsDisplayed()
    }

    @Test
    fun testOpeningAddTaskBottomSheet() {
        composeTestRule.setContent {
            FocusDeskTheme {
                TasksScreen(viewModel = viewModel, onNavigateToTimer = {})
            }
        }

        // Click Add Task FAB
        composeTestRule.onNodeWithContentDescription("Add Task").performClick()

        // Verify bottom sheet title is displayed
        composeTestRule.onNodeWithText("New Desk Task").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Task").assertIsDisplayed()
    }
}
