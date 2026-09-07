package com.focusdesk.core.platform

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

actual class HapticEngine(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    @SuppressLint("MissingPermission")
    actual fun perform(type: HapticFeedbackType) {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effect = when (type) {
                HapticFeedbackType.Light -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                HapticFeedbackType.Medium -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                HapticFeedbackType.Heavy -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                HapticFeedbackType.Success -> VibrationEffect.createWaveform(
                    longArrayOf(0, 40, 60, 40),
                    intArrayOf(0, 180, 0, 255),
                    -1
                )
                HapticFeedbackType.Warning -> VibrationEffect.createWaveform(
                    longArrayOf(0, 60, 40, 60),
                    intArrayOf(0, 200, 0, 200),
                    -1
                )
                HapticFeedbackType.Error -> VibrationEffect.createWaveform(
                    longArrayOf(0, 80, 50, 80, 50, 80),
                    intArrayOf(0, 255, 0, 255, 0, 255),
                    -1
                )
                HapticFeedbackType.Selection -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
            }
            vib.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            val duration = when (type) {
                HapticFeedbackType.Light, HapticFeedbackType.Selection -> 15L
                HapticFeedbackType.Medium -> 35L
                HapticFeedbackType.Heavy -> 60L
                HapticFeedbackType.Success -> 80L
                HapticFeedbackType.Warning, HapticFeedbackType.Error -> 120L
            }
            @Suppress("DEPRECATION")
            vib.vibrate(duration)
        }
    }
}
