package com.focusdesk.core.platform

import com.focusdesk.domain.model.Soundscape
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.Foundation.NSURL

actual class AmbientAudioEngine {

    private var player: AVAudioPlayer? = null
    private var currentVolume: Float = 0.5f

    actual fun playSoundscape(soundscape: Soundscape, volume: Float) {
        stopSoundscape()
        if (soundscape == Soundscape.None) return
        currentVolume = volume

        try {
            AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryPlayback, error = null)
            AVAudioSession.sharedInstance().setActive(true, error = null)
            // Play ambient track
            player?.numberOfLoops = -1
            player?.volume = currentVolume
            player?.play()
        } catch (_: Exception) {
            // Safe fallback
        }
    }

    actual fun stopSoundscape() {
        try {
            player?.stop()
        } catch (_: Exception) {
            // Ignore
        } finally {
            player = null
        }
    }

    actual fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        player?.volume = currentVolume
    }

    actual fun playOneShot(sound: SoundEffect) {
        val soundId: UInt = when (sound) {
            SoundEffect.Tick -> 1057u
            SoundEffect.Complete -> 1025u
            SoundEffect.Bell -> 1013u
            SoundEffect.Click -> 1104u
        }
        AudioServicesPlaySystemSound(soundId)
    }

    actual fun release() {
        stopSoundscape()
    }
}
