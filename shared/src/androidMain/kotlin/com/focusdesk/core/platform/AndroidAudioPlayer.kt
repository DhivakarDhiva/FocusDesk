package com.focusdesk.core.platform

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import com.focusdesk.domain.model.Soundscape
import java.util.Random
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin

actual class AmbientAudioEngine(private val context: Context) {

    private var currentVolume: Float = 0.65f
    private var isPlaying: Boolean = false
    private var currentSoundscape: Soundscape = Soundscape.None
    private var audioThread: Thread? = null
    private var audioTrack: AudioTrack? = null

    private val toneGenerator: ToneGenerator? by lazy {
        try {
            ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
        } catch (_: Exception) {
            null
        }
    }

    actual fun playSoundscape(soundscape: Soundscape, volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        if (soundscape == Soundscape.None) {
            stopSoundscape()
            return
        }

        if (isPlaying && currentSoundscape == soundscape) {
            setVolume(currentVolume)
            return
        }

        stopSoundscape()
        currentSoundscape = soundscape
        isPlaying = true

        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(sampleRate / 4)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.setVolume(currentVolume)
            audioTrack?.play()

            audioThread = thread(start = true, name = "FocusDesk-AudioSynth", isDaemon = true) {
                val shortBuffer = ShortArray(bufferSize)
                val random = Random()
                var sampleIndex: Long = 0
                var filterStateL = 0.0
                var filterStateR = 0.0
                var dropCooldown = 0

                while (isPlaying) {
                    when (currentSoundscape) {
                        Soundscape.Rain, Soundscape.HeavyRain -> {
                            val rainIntensity = if (currentSoundscape == Soundscape.HeavyRain) 0.12 else 0.06
                            for (i in 0 until bufferSize step 2) {
                                val white = (random.nextDouble() * 2.0 - 1.0)
                                filterStateL = (filterStateL * 0.94) + (white * rainIntensity)
                                filterStateR = (filterStateR * 0.94) + ((random.nextDouble() * 2.0 - 1.0) * rainIntensity)

                                var dropL = 0.0
                                var dropR = 0.0
                                if (dropCooldown <= 0) {
                                    if (random.nextDouble() < (if (currentSoundscape == Soundscape.HeavyRain) 0.008 else 0.003)) {
                                        val dropPitch = 1000.0 + random.nextDouble() * 2000.0
                                        dropL = sin(2.0 * PI * dropPitch * (sampleIndex % sampleRate) / sampleRate) * 0.25
                                        dropR = sin(2.0 * PI * dropPitch * ((sampleIndex + 10) % sampleRate) / sampleRate) * 0.25
                                        dropCooldown = (sampleRate * 0.03).toInt()
                                    }
                                } else {
                                    dropCooldown--
                                }

                                val sampleL = ((filterStateL * 0.75 + dropL) * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                val sampleR = ((filterStateR * 0.75 + dropR) * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                shortBuffer[i] = sampleL
                                shortBuffer[i + 1] = sampleR
                                sampleIndex++
                            }
                        }

                        Soundscape.WhiteNoise, Soundscape.PinkNoise, Soundscape.BrownNoise -> {
                            val filterFactor = when (currentSoundscape) {
                                Soundscape.BrownNoise -> 0.96
                                Soundscape.PinkNoise -> 0.92
                                else -> 0.86
                            }
                            for (i in 0 until bufferSize step 2) {
                                val white1 = (random.nextGaussian() * 0.35)
                                val white2 = (random.nextGaussian() * 0.35)
                                filterStateL = (filterStateL * filterFactor) + (white1 * (1.0 - filterFactor))
                                filterStateR = (filterStateR * filterFactor) + (white2 * (1.0 - filterFactor))

                                shortBuffer[i] = (filterStateL * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                shortBuffer[i + 1] = (filterStateR * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                            }
                        }

                        Soundscape.CoffeeShop, Soundscape.Library -> {
                            for (i in 0 until bufferSize step 2) {
                                val t = sampleIndex.toDouble() / sampleRate
                                val rumble = sin(2.0 * PI * 65.0 * t) * 0.12 + sin(2.0 * PI * 130.0 * t) * 0.06
                                val chatterNoise = (random.nextDouble() * 2.0 - 1.0) * 0.06
                                filterStateL = (filterStateL * 0.95) + (chatterNoise * 0.05)
                                filterStateR = (filterStateR * 0.95) + ((random.nextDouble() * 2.0 - 1.0) * 0.05)

                                val sampleValL = ((rumble + filterStateL) * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                val sampleValR = ((rumble + filterStateR) * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                shortBuffer[i] = sampleValL
                                shortBuffer[i + 1] = sampleValR
                                sampleIndex++
                            }
                        }

                        Soundscape.Forest, Soundscape.Birdsong -> {
                            for (i in 0 until bufferSize step 2) {
                                val t = sampleIndex.toDouble() / sampleRate
                                val wind = sin(2.0 * PI * 0.25 * t) * 0.12 + 0.2
                                val breezeNoise = (random.nextDouble() * 2.0 - 1.0) * wind * 0.25
                                filterStateL = (filterStateL * 0.92) + (breezeNoise * 0.08)

                                val chirpPhase = (sampleIndex % (sampleRate * 3)).toDouble() / sampleRate
                                val chirp = if (chirpPhase in 0.0..0.4) {
                                    sin(2.0 * PI * (2400.0 + sin(chirpPhase * 50.0) * 450.0) * chirpPhase) * 0.2
                                } else 0.0

                                val sampleL = ((filterStateL * 0.7 + chirp) * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                val sampleR = ((filterStateL * 0.7 + chirp * 0.85) * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                shortBuffer[i] = sampleL
                                shortBuffer[i + 1] = sampleR
                                sampleIndex++
                            }
                        }

                        Soundscape.Ocean -> {
                            for (i in 0 until bufferSize step 2) {
                                val t = sampleIndex.toDouble() / sampleRate
                                val swell = (sin(2.0 * PI * 0.1 * t) * 0.5 + 0.5) * 0.4
                                val surf = (random.nextDouble() * 2.0 - 1.0) * swell
                                filterStateL = (filterStateL * 0.96) + (surf * 0.04)
                                filterStateR = (filterStateR * 0.96) + ((random.nextDouble() * 2.0 - 1.0) * swell * 0.04)

                                val sampleL = (filterStateL * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                val sampleR = (filterStateR * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                shortBuffer[i] = sampleL
                                shortBuffer[i + 1] = sampleR
                                sampleIndex++
                            }
                        }

                        Soundscape.Thunderstorm, Soundscape.Fireplace -> {
                            for (i in 0 until bufferSize step 2) {
                                val crackle = if (random.nextDouble() < 0.005) (random.nextDouble() * 2.0 - 1.0) * 0.4 else 0.0
                                val white = (random.nextDouble() * 2.0 - 1.0) * 0.04
                                filterStateL = (filterStateL * 0.97) + white + crackle
                                filterStateR = (filterStateR * 0.97) + white + crackle

                                shortBuffer[i] = (filterStateL * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                shortBuffer[i + 1] = (filterStateR * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                            }
                        }

                        Soundscape.LofiBeats, Soundscape.Classical -> {
                            for (i in 0 until bufferSize step 2) {
                                val t = sampleIndex.toDouble() / sampleRate
                                val tone = sin(2.0 * PI * 220.0 * t) * 0.1 + sin(2.0 * PI * 440.0 * t) * 0.08
                                val warmNoise = (random.nextDouble() * 2.0 - 1.0) * 0.02
                                filterStateL = (filterStateL * 0.9) + warmNoise
                                filterStateR = (filterStateR * 0.9) + warmNoise

                                val s = ((tone + filterStateL) * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                                shortBuffer[i] = s
                                shortBuffer[i + 1] = s
                                sampleIndex++
                            }
                        }

                        Soundscape.None -> {
                            shortBuffer.fill(0)
                        }
                    }

                    if (isPlaying) {
                        audioTrack?.write(shortBuffer, 0, bufferSize)
                    }
                }
            }
        } catch (_: Exception) {
            stopSoundscape()
        }
    }

    actual fun stopSoundscape() {
        isPlaying = false
        currentSoundscape = Soundscape.None
        try {
            audioThread?.interrupt()
            audioTrack?.let { track ->
                try {
                    track.pause()
                    track.flush()
                    track.stop()
                } catch (_: Exception) {}
                track.release()
            }
        } catch (_: Exception) {
        } finally {
            audioThread = null
            audioTrack = null
        }
    }

    actual fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        try {
            audioTrack?.setVolume(currentVolume)
        } catch (_: Exception) {
        }
    }

    actual fun playOneShot(sound: SoundEffect) {
        try {
            when (sound) {
                SoundEffect.Tick -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
                SoundEffect.Complete -> toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 250)
                SoundEffect.Bell -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
                SoundEffect.Click -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 25)
            }
        } catch (_: Exception) {
        }
    }

    actual fun release() {
        stopSoundscape()
        try {
            toneGenerator?.release()
        } catch (_: Exception) {
        }
    }
}
