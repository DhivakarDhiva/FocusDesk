package com.focusdesk.core.di

import com.focusdesk.data.repository.InMemoryFocusRepository
import com.focusdesk.data.repository.InMemorySettingsRepository
import com.focusdesk.data.repository.InMemoryTaskRepository
import com.focusdesk.domain.repository.FocusRepository
import com.focusdesk.domain.repository.SettingsRepository
import com.focusdesk.domain.repository.TaskRepository
import com.focusdesk.domain.usecase.GetAnalyticsUseCase
import com.focusdesk.domain.usecase.ManageTasksUseCase
import com.focusdesk.domain.usecase.TimerEngineUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val sharedModule: Module = module {
    // Data Layer: Repository Implementations
    single<FocusRepository> { InMemoryFocusRepository() }
    single<TaskRepository> { InMemoryTaskRepository() }
    single<SettingsRepository> { InMemorySettingsRepository() }

    // Domain Layer: Business Use Cases
    single { TimerEngineUseCase(get(), get(), get()) }
    single { ManageTasksUseCase(get()) }
    single { GetAnalyticsUseCase(get()) }
}
