package com.focusdesk.app.di

import com.focusdesk.app.data.local.FocusDeskDatabase
import com.focusdesk.app.data.repository.RoomFocusRepository
import com.focusdesk.app.data.repository.RoomSettingsRepository
import com.focusdesk.app.data.repository.RoomTaskRepository
import com.focusdesk.app.presentation.analytics.AnalyticsViewModel
import com.focusdesk.app.presentation.settings.SettingsViewModel
import com.focusdesk.app.presentation.timer.TimerViewModel
import com.focusdesk.core.platform.AmbientAudioEngine
import com.focusdesk.domain.repository.FocusRepository
import com.focusdesk.domain.repository.SettingsRepository
import com.focusdesk.domain.repository.TaskRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule: Module = module {
    // Room Database & DAOs
    single { FocusDeskDatabase.getInstance(androidContext()) }
    single { get<FocusDeskDatabase>().focusSessionDao() }
    single { get<FocusDeskDatabase>().userSettingsDao() }
    single { get<FocusDeskDatabase>().taskDao() }

    // Persistent Room Repositories
    single<FocusRepository> { RoomFocusRepository(get(), get(), get()) }
    single<SettingsRepository> { RoomSettingsRepository(get()) }
    single<TaskRepository> { RoomTaskRepository(get()) }

    // Platform Services & ViewModels
    single { AmbientAudioEngine(get()) }
    viewModel { TimerViewModel(get(), get(), get()) }
    viewModel { AnalyticsViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
}

