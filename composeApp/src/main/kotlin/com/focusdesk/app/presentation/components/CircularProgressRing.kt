package com.focusdesk.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.focusdesk.app.presentation.designsystem.SwiftUiMotion
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularProgressRing(
    progress: Float,
    gradientBrush: Brush,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    strokeWidth: Dp = 16.dp,
    size: Dp = 280.dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = SwiftUiMotion.smooth(),
        label = "circular_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2)) {
            val canvasSize = this.size.width
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(canvasSize, canvasSize)
            val topLeft = Offset(0f, 0f)

            // 1. Draw Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // 2. Draw Animated Progress Arc
            if (animatedProgress > 0f) {
                val sweep = 360f * animatedProgress
                drawArc(
                    brush = gradientBrush,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // 3. Glowing indicator tip
                val angleInRad = Math.toRadians((-90.0 + sweep.toDouble()))
                val radius = canvasSize / 2f
                val centerX = canvasSize / 2f
                val centerY = canvasSize / 2f
                val thumbX = centerX + radius * cos(angleInRad).toFloat()
                val thumbY = centerY + radius * sin(angleInRad).toFloat()

                drawCircle(
                    color = Color.White,
                    radius = strokePx * 0.42f,
                    center = Offset(thumbX, thumbY)
                )
            }
        }

        content()
    }
}
