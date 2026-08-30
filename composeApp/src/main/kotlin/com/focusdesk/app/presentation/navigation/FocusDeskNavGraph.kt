package com.focusdesk.app.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.focusdesk.app.presentation.analytics.AnalyticsScreen
import com.focusdesk.app.presentation.analytics.AnalyticsViewModel
import com.focusdesk.app.presentation.designsystem.SwiftUiMotion
import com.focusdesk.app.presentation.settings.SettingsScreen
import com.focusdesk.app.presentation.settings.SettingsViewModel
import com.focusdesk.app.presentation.tasks.TasksScreen
import com.focusdesk.app.presentation.tasks.TasksViewModel
import com.focusdesk.app.presentation.timer.TimerScreen
import com.focusdesk.app.presentation.timer.TimerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FocusDeskNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Timer.route,
        modifier = modifier.padding(paddingValues),
        enterTransition = { SwiftUiMotion.NavEnterTransition },
        exitTransition = { SwiftUiMotion.NavExitTransition },
        popEnterTransition = { SwiftUiMotion.NavPopEnterTransition },
        popExitTransition = { SwiftUiMotion.NavPopExitTransition }
    ) {
        composable(route = Screen.Timer.route) {
            val viewModel: TimerViewModel = koinViewModel()
            TimerScreen(viewModel = viewModel)
        }

        composable(route = Screen.Tasks.route) {
            val viewModel: TasksViewModel = koinViewModel()
            TasksScreen(
                viewModel = viewModel,
                onNavigateToTimer = {
                    navController.navigate(Screen.Timer.route) {
                        popUpTo(Screen.Timer.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = Screen.Analytics.route) {
            val viewModel: AnalyticsViewModel = koinViewModel()
            AnalyticsScreen(viewModel = viewModel)
        }

        composable(route = Screen.Settings.route) {
            val viewModel: SettingsViewModel = koinViewModel()
            SettingsScreen(viewModel = viewModel)
        }
    }
}
