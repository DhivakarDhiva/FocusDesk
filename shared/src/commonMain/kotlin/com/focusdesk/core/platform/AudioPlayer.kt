package com.focusdesk.core.platform

import com.focusdesk.domain.model.Soundscape

enum class SoundEffect {
    Tick,
    Complete,
    Bell,
    Click
}

expect class AmbientAudioEngine {
    fun playSoundscape(soundscape: Soundscape, volume: Float)
    fun stopSoundscape()
    fun setVolume(volume: Float)
    fun playOneShot(sound: SoundEffect)
    fun release()
}
