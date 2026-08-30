package com.focusdesk.app.presentation.analytics

import androidx.lifecycle.viewModelScope
import com.focusdesk.core.mvi.MviViewModel
import com.focusdesk.domain.usecase.GetAnalyticsUseCase
import kotlinx.coroutines.launch

class AnalyticsViewModel(
    private val getAnalyticsUseCase: GetAnalyticsUseCase
) : MviViewModel<AnalyticsIntent, AnalyticsState, AnalyticsEffect>(AnalyticsState()) {

    init {
        viewModelScope.launch {
            getAnalyticsUseCase.observeStats().collect { productivityStats ->
                setState { copy(stats = productivityStats) }
            }
        }
    }

    override fun handleIntent(intent: AnalyticsIntent) {
        when (intent) {
            is AnalyticsIntent.SelectDay -> setState { copy(selectedDayIndex = intent.dayIndex) }
            is AnalyticsIntent.RequestResetStats -> setState { copy(isResetDialogOpen = true) }
            is AnalyticsIntent.DismissResetDialog -> setState { copy(isResetDialogOpen = false) }
            is AnalyticsIntent.ConfirmResetStats -> handleConfirmReset()
        }
    }

    private fun handleConfirmReset() {
        viewModelScope.launch {
            getAnalyticsUseCase.resetStats()
            setState { copy(isResetDialogOpen = false, selectedDayIndex = null) }
            setEffect { AnalyticsEffect.PlayImpactHaptic }
            setEffect { AnalyticsEffect.ShowSnackbar("Productivity metrics reset") }
        }
    }
}
