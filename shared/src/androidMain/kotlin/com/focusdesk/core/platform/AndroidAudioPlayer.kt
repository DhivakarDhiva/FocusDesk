package com.focusdesk.core.platform

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.media.AudioManager
import com.focusdesk.domain.model.Soundscape

actual class AmbientAudioEngine(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentVolume: Float = 0.5f
    private val toneGenerator: ToneGenerator? by lazy {
        try {
            ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
        } catch (_: Exception) {
            null
        }
    }

    actual fun playSoundscape(soundscape: Soundscape, volume: Float) {
        stopSoundscape()
        if (soundscape == Soundscape.None) return

        currentVolume = volume
        // Uses Android Synthesizer tone / audio stream simulation or raw asset
        try {
            // Setup MediaPlayer instance configured for loop playback
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                isLooping = true
                setVolume(currentVolume, currentVolume)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    actual fun stopSoundscape() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
        } catch (_: Exception) {
            // Ignore teardown issues
        } finally {
            mediaPlayer = null
        }
    }

    actual fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        try {
            mediaPlayer?.setVolume(currentVolume, currentVolume)
        } catch (_: Exception) {
            // Ignore volume set failures
        }
    }

    actual fun playOneShot(sound: SoundEffect) {
        try {
            when (sound) {
                SoundEffect.Tick -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
                SoundEffect.Complete -> toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
                SoundEffect.Bell -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 150)
                SoundEffect.Click -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 20)
            }
        } catch (_: Exception) {
            // Fallback gracefully
        }
    }

    actual fun release() {
        stopSoundscape()
        try {
            toneGenerator?.release()
        } catch (_: Exception) {
            // Ignore release exception
        }
    }
}
