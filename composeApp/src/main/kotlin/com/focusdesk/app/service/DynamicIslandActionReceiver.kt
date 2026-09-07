package com.focusdesk.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.focusdesk.core.platform.AmbientAudioEngine
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.model.Soundscape
import com.focusdesk.domain.repository.FocusRepository
import com.focusdesk.domain.repository.SettingsRepository
import com.focusdesk.domain.usecase.TimerEngineUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class DynamicIslandActionReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val koin = GlobalContext.getOrNull() ?: return
        val timerEngine = koin.get<TimerEngineUseCase>()
        val focusRepository = koin.get<FocusRepository>()
        val settingsRepository = koin.get<SettingsRepository>()
        val audioEngine = koin.get<AmbientAudioEngine>()

        when (intent.action) {
            ACTION_TOGGLE_PAUSE -> {
                receiverScope.launch {
                    val currentSession = focusRepository.getCurrentSession()
                    if (currentSession.status == SessionStatus.Running) {
                        timerEngine.pauseTimer()
                    } else if (currentSession.status == SessionStatus.Paused || currentSession.status == SessionStatus.Idle) {
                        timerEngine.startTimer()
                    }
                }
            }

            ACTION_TOGGLE_MUTE -> {
                receiverScope.launch {
                    val newSoundscape = timerEngine.toggleAudioMute()
                    val volume = settingsRepository.getSettings().soundscapeVolume
                    if (newSoundscape != Soundscape.None) {
                        audioEngine.playSoundscape(newSoundscape, volume)
                    } else {
                        audioEngine.stopSoundscape()
                    }
                }
            }

            ACTION_STOP_SESSION -> {
                receiverScope.launch {
                    timerEngine.endSessionEarly()
                    audioEngine.stopSoundscape()
                    DynamicIslandService.stop(context)
                }
            }
        }
    }

    companion object {
        const val ACTION_TOGGLE_PAUSE = "com.focusdesk.app.action.TOGGLE_PAUSE"
        const val ACTION_TOGGLE_MUTE = "com.focusdesk.app.action.TOGGLE_MUTE"
        const val ACTION_STOP_SESSION = "com.focusdesk.app.action.STOP_SESSION"
    }
}
