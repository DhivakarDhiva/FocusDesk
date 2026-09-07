package com.focusdesk.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen(
        route = "home_screen",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Focus : Screen(
        route = "focus_screen",
        title = "Focus",
        selectedIcon = Icons.Filled.Bolt,
        unselectedIcon = Icons.Outlined.Bolt
    )

    data object Insights : Screen(
        route = "insights_screen",
        title = "Insights",
        selectedIcon = Icons.AutoMirrored.Filled.ShowChart,
        unselectedIcon = Icons.AutoMirrored.Outlined.ShowChart
    )

    data object Sounds : Screen(
        route = "sounds_screen",
        title = "Sounds",
        selectedIcon = Icons.Filled.Headphones,
        unselectedIcon = Icons.Outlined.Headphones
    )

    data object Settings : Screen(
        route = "settings_screen",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    companion object {
        val bottomNavScreens: List<Screen>
            get() = listOf(Home, Focus, Insights, Sounds, Settings)
    }
}
