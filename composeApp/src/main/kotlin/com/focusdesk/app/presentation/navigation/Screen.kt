package com.focusdesk.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Timer : Screen(
        route = "timer_screen",
        titleResId = com.focusdesk.app.R.string.nav_timer,
        selectedIcon = Icons.Filled.HourglassEmpty,
        unselectedIcon = Icons.Outlined.HourglassEmpty
    )

    data object Tasks : Screen(
        route = "tasks_screen",
        titleResId = com.focusdesk.app.R.string.nav_tasks,
        selectedIcon = Icons.Filled.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircleOutline
    )

    data object Analytics : Screen(
        route = "analytics_screen",
        titleResId = com.focusdesk.app.R.string.nav_analytics,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )

    data object Settings : Screen(
        route = "settings_screen",
        titleResId = com.focusdesk.app.R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    companion object {
        val bottomNavScreens = listOf(Timer, Tasks, Analytics, Settings)
    }
}
