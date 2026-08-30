package com.focusdesk.app.di

import com.focusdesk.app.presentation.analytics.AnalyticsViewModel
import com.focusdesk.app.presentation.settings.SettingsViewModel
import com.focusdesk.app.presentation.tasks.TasksViewModel
import com.focusdesk.app.presentation.timer.TimerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule: Module = module {
    viewModel { TimerViewModel(get(), get(), get()) }
    viewModel { TasksViewModel(get(), get()) }
    viewModel { AnalyticsViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
