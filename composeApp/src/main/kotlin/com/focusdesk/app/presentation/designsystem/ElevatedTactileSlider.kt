package com.focusdesk.app.presentation.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.focusdesk.core.platform.HapticFeedbackType
import kotlin.math.roundToInt

@Composable
fun ElevatedTactileSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    accentColor: Color = LocalFocusDeskColors.current.primary
) {
    val hapticEngine = LocalHapticEngine.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val stickWidthDp = 30.dp
        val stickHeightDp = 18.dp
        val density = LocalDensity.current
        val stickWidthPx = with(density) { stickWidthDp.toPx() }
        val usableTrackWidth = (widthPx - stickWidthPx).coerceAtLeast(1f)

        val normalized = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

        fun updateFromX(x: Float) {
            val rawFraction = (x - stickWidthPx / 2f) / usableTrackWidth
            val clampedFraction = rawFraction.coerceIn(0f, 1f)
            val computedValue = if (steps > 0) {
                val stepCount = steps + 1
                val steppedFraction = (clampedFraction * stepCount).roundToInt().toFloat() / stepCount
                valueRange.start + steppedFraction * (valueRange.endInclusive - valueRange.start)
            } else {
                valueRange.start + clampedFraction * (valueRange.endInclusive - valueRange.start)
            }
            if (computedValue != value) {
                hapticEngine.perform(HapticFeedbackType.Selection)
                onValueChange(computedValue)
            }
        }

        // Original Inactive Track Bar (Exact iOS reference: sleek 4.5.dp bar with rounded ends)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.5.dp)
                .clip(RoundedCornerShape(2.25.dp))
                .background(Color(0xFFE2E6DF))
        )

        // Active Track Fill (seamlessly connecting under the elevated leveling stick)
        val activeWidthDp = with(density) {
            (stickWidthPx / 2f + usableTrackWidth * normalized).toDp()
        }
        Box(
            modifier = Modifier
                .width(activeWidthDp)
                .height(4.5.dp)
                .clip(RoundedCornerShape(2.25.dp))
                .background(accentColor)
        )

        // Elevated Leveling Stick (Pill / Capsule Thumb with clear elevation & drop shadow)
        val stickOffsetDp = with(density) { (usableTrackWidth * normalized).toDp() }
        Box(
            modifier = Modifier
                .padding(start = stickOffsetDp)
                .size(width = stickWidthDp, height = stickHeightDp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(9.dp),
                    ambientColor = Color.Black.copy(alpha = 0.22f),
                    spotColor = Color.Black.copy(alpha = 0.26f)
                )
                .clip(RoundedCornerShape(9.dp))
                .background(Color.White)
                .border(0.6.dp, Color(0xFFD0D6CC), RoundedCornerShape(9.dp))
        )

        // Interactive Gesture Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        updateFromX(offset.x)
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, _ ->
                        change.consume()
                        updateFromX(change.position.x)
                    }
                }
        )
    }
}
