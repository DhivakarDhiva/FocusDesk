package com.focusdesk.app.presentation.timer

import androidx.lifecycle.viewModelScope
import com.focusdesk.core.mvi.MviViewModel
import com.focusdesk.domain.model.SessionMode
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.model.Soundscape
import com.focusdesk.domain.model.TaskItem
import com.focusdesk.domain.repository.SettingsRepository
import com.focusdesk.domain.repository.TaskRepository
import com.focusdesk.domain.usecase.TimerEngineUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel(
    private val timerEngineUseCase: TimerEngineUseCase,
    private val taskRepository: TaskRepository,
    private val settingsRepository: SettingsRepository
) : MviViewModel<TimerIntent, TimerState, TimerEffect>(TimerState()) {

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            timerEngineUseCase.observeSession().collect { session ->
                val isAudioActive = session.soundscape != Soundscape.None
                setState {
                    copy(
                        session = session,
                        soundscape = session.soundscape,
                        isPlayingAudio = isAudioActive
                    )
                }
                if (session.status == SessionStatus.Running && timerJob == null) {
                    startTickingLoop()
                } else if (session.status != SessionStatus.Running) {
                    stopTickingLoop()
                }
            }
        }

        viewModelScope.launch {
            taskRepository.observeTasks().collect { tasks ->
                setState { copy(availableTasks = tasks.filter { !it.isCompleted }) }
            }
        }

        viewModelScope.launch {
            settingsRepository.observeSettings().collect { settings ->
                setState {
                    copy(
                        soundVolume = settings.soundscapeVolume
                    )
                }
            }
        }
    }

    override fun handleIntent(intent: TimerIntent) {
        when (intent) {
            is TimerIntent.ToggleTimer -> handleToggleTimer()
            is TimerIntent.SkipSession -> handleSkipSession()
            is TimerIntent.RequestReset -> setState { copy(isResetDialogOpen = true) }
            is TimerIntent.ConfirmReset -> handleConfirmReset()
            is TimerIntent.DismissResetDialog -> setState { copy(isResetDialogOpen = false) }
            is TimerIntent.SelectMode -> handleSelectMode(intent.mode)
            is TimerIntent.SelectSoundscape -> handleSelectSoundscape(intent.soundscape)
            is TimerIntent.ToggleSoundscapePlayback -> {
                viewModelScope.launch {
                    val newSoundscape = timerEngineUseCase.toggleAudioMute()
                    setState {
                        copy(
                            isPlayingAudio = newSoundscape != Soundscape.None,
                            soundscape = newSoundscape
                        )
                    }
                }
            }
            is TimerIntent.SelectAssociatedTask -> handleSelectTask(intent.task)
            is TimerIntent.OpenTaskSelector -> setState { copy(isTaskSelectorOpen = true) }
            is TimerIntent.DismissTaskSelector -> setState { copy(isTaskSelectorOpen = false) }
            is TimerIntent.InternalTick -> performTick()
            is TimerIntent.StartCustomSession -> {
                viewModelScope.launch {
                    val newSession = timerEngineUseCase.startCustomSession(
                        taskTitle = intent.taskTitle,
                        category = intent.category,
                        mood = intent.mood,
                        durationMinutes = intent.durationMinutes,
                        soundscape = intent.soundscape
                    )
                    setState {
                        copy(
                            session = newSession,
                            soundscape = intent.soundscape,
                            isPlayingAudio = intent.soundscape != Soundscape.None
                        )
                    }
                }
            }
            is TimerIntent.AddFiveMinutes -> {
                viewModelScope.launch {
                    timerEngineUseCase.addSeconds(300L)
                }
            }
            is TimerIntent.EndSessionEarly -> {
                viewModelScope.launch {
                    val completed = timerEngineUseCase.endSessionEarly()
                    setState { copy(session = completed, soundscape = Soundscape.None, isPlayingAudio = false) }
                }
            }
            is TimerIntent.SetSoundVolume -> {
                setState { copy(soundVolume = intent.volume) }
            }
            is TimerIntent.PlaySound -> {
                viewModelScope.launch {
                    timerEngineUseCase.updateSoundscape(intent.soundscape)
                    setState { copy(soundscape = intent.soundscape, isPlayingAudio = true) }
                }
            }
            is TimerIntent.StopAudio -> {
                viewModelScope.launch {
                    timerEngineUseCase.updateSoundscape(Soundscape.None)
                    setState { copy(soundscape = Soundscape.None, isPlayingAudio = false) }
                }
            }
            is TimerIntent.DismissCompletedSession -> {
                viewModelScope.launch {
                    val newSession = timerEngineUseCase.resetSession()
                    setState { copy(session = newSession, soundscape = Soundscape.None, isPlayingAudio = false) }
                }
            }
        }
    }

    private fun handleToggleTimer() {
        val currentStatus = currentState.session.status
        viewModelScope.launch {
            if (currentStatus == SessionStatus.Running) {
                timerEngineUseCase.pauseTimer()
                setEffect { TimerEffect.ShowSnackbar("Session paused") }
            } else {
                timerEngineUseCase.startTimer()
                setEffect { TimerEffect.PlayTickHaptic }
            }
        }
    }

    private fun handleSkipSession() {
        viewModelScope.launch {
            timerEngineUseCase.skipSession()
            setEffect { TimerEffect.ShowSnackbar("Session skipped") }
        }
    }

    private fun handleConfirmReset() {
        viewModelScope.launch {
            timerEngineUseCase.resetTimer()
            setState { copy(isResetDialogOpen = false, soundscape = Soundscape.None, isPlayingAudio = false) }
            setEffect { TimerEffect.ShowSnackbar("Timer reset") }
        }
    }

    private fun handleSelectMode(mode: SessionMode) {
        viewModelScope.launch {
            timerEngineUseCase.switchMode(mode)
            setEffect { TimerEffect.PlayTickHaptic }
        }
    }

    private fun handleSelectSoundscape(soundscape: Soundscape) {
        viewModelScope.launch {
            timerEngineUseCase.updateSoundscape(soundscape)
            setState { copy(soundscape = soundscape, isPlayingAudio = soundscape != Soundscape.None) }
        }
    }

    private fun handleSelectTask(task: TaskItem?) {
        viewModelScope.launch {
            timerEngineUseCase.attachTask(task?.id, task?.title)
            setState { copy(isTaskSelectorOpen = false) }
            setEffect {
                TimerEffect.ShowSnackbar(
                    if (task != null) "Focuson '${task.title}'" else "Cleared task association"
                )
            }
        }
    }

    private fun startTickingLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                onIntent(TimerIntent.InternalTick)
            }
        }
    }

    private fun stopTickingLoop() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun performTick() {
        viewModelScope.launch {
            val prevRemaining = currentState.session.remainingSeconds
            val result = timerEngineUseCase.tick()
            if (result.status == SessionStatus.Completed) {
                setEffect { TimerEffect.PlayCelebrationHaptic }
                setEffect { TimerEffect.PlaySoundEffect("complete") }
            } else if (prevRemaining > 0 && prevRemaining % 60 == 0L) {
                setEffect { TimerEffect.PlayTickHaptic }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTickingLoop()
    }
}
