package com.focusdesk.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.focusdesk.app.presentation.designsystem.FocusDeskTheme
import com.focusdesk.app.presentation.timer.TimerScreen
import com.focusdesk.app.presentation.timer.TimerViewModel
import com.focusdesk.data.repository.InMemoryFocusRepository
import com.focusdesk.data.repository.InMemorySettingsRepository
import com.focusdesk.data.repository.InMemoryTaskRepository
import com.focusdesk.domain.usecase.TimerEngineUseCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: TimerViewModel

    @Before
    fun setUp() {
        val focusRepo = InMemoryFocusRepository()
        val taskRepo = InMemoryTaskRepository()
        val settingsRepo = InMemorySettingsRepository()
        val timerUseCase = TimerEngineUseCase(focusRepo, taskRepo, settingsRepo)
        viewModel = TimerViewModel(timerUseCase, taskRepo, settingsRepo)
    }

    @Test
    fun testTimerScreenElementsAreDisplayed() {
        composeTestRule.setContent {
            FocusDeskTheme {
                TimerScreen(viewModel = viewModel)
            }
        }

        // Verify mode selectors are displayed
        composeTestRule.onNodeWithText("Focus").assertIsDisplayed()
        composeTestRule.onNodeWithText("Short Break").assertIsDisplayed()
        composeTestRule.onNodeWithText("Long Break").assertIsDisplayed()

        // Verify initial action button
        composeTestRule.onNodeWithText("Start Focus").assertIsDisplayed()

        // Verify initial countdown time
        composeTestRule.onNodeWithText("25:00").assertIsDisplayed()
    }

    @Test
    fun testSwitchingToShortBreakUpdatesTimer() {
        composeTestRule.setContent {
            FocusDeskTheme {
                TimerScreen(viewModel = viewModel)
            }
        }

        // Tap Short Break
        composeTestRule.onNodeWithText("Short Break").performClick()

        // Assert time changes to 05:00
        composeTestRule.onNodeWithText("05:00").assertIsDisplayed()
    }
}
