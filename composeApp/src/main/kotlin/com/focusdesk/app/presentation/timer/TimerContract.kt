package com.focusdesk.app.presentation.timer

import com.focusdesk.core.mvi.MviEffect
import com.focusdesk.core.mvi.MviIntent
import com.focusdesk.core.mvi.MviState
import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.model.Soundscape
import com.focusdesk.domain.model.TaskItem

data class TimerState(
    val session: FocusSession = FocusSession(
        id = "initial",
        mode = SessionMode.Work,
        targetDurationSeconds = 25 * 60L,
        remainingSeconds = 25 * 60L,
        status = SessionStatus.Idle
    ),
    val soundscape: Soundscape = Soundscape.None,
    val isPlayingAudio: Boolean = false,
    val soundVolume: Float = 0.5f,
    val isResetDialogOpen: Boolean = false,
    val isTaskSelectorOpen: Boolean = false,
    val availableTasks: List<TaskItem> = emptyList()
) : MviState

sealed interface TimerIntent : MviIntent {
    data object ToggleTimer : TimerIntent
    data object SkipSession : TimerIntent
    data object RequestReset : TimerIntent
    data object ConfirmReset : TimerIntent
    data object DismissResetDialog : TimerIntent
    data class SelectMode(val mode: SessionMode) : TimerIntent
    data class SelectSoundscape(val soundscape: Soundscape) : TimerIntent
    data object ToggleSoundscapePlayback : TimerIntent
    data class SelectAssociatedTask(val task: TaskItem?) : TimerIntent
    data object OpenTaskSelector : TimerIntent
    data object DismissTaskSelector : TimerIntent
    data object InternalTick : TimerIntent
}

sealed interface TimerEffect : MviEffect {
    data class ShowSnackbar(val message: String) : TimerEffect
    data object PlayCelebrationHaptic : TimerEffect
    data object PlayTickHaptic : TimerEffect
    data class PlaySoundEffect(val effect: String) : TimerEffect
}
