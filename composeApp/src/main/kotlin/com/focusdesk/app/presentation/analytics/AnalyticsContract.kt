package com.focusdesk.app.presentation.analytics

import com.focusdesk.core.mvi.MviEffect
import com.focusdesk.core.mvi.MviIntent
import com.focusdesk.core.mvi.MviState
import com.focusdesk.domain.model.ProductivityStats

data class AnalyticsState(
    val stats: ProductivityStats = ProductivityStats(),
    val isResetDialogOpen: Boolean = false,
    val selectedDayIndex: Int? = null
) : MviState

sealed interface AnalyticsIntent : MviIntent {
    data class SelectDay(val dayIndex: Int?) : AnalyticsIntent
    data object RequestResetStats : AnalyticsIntent
    data object DismissResetDialog : AnalyticsIntent
    data object ConfirmResetStats : AnalyticsIntent
}

sealed interface AnalyticsEffect : MviEffect {
    data class ShowSnackbar(val message: String) : AnalyticsEffect
    data object PlayImpactHaptic : AnalyticsEffect
}
