package com.focusdesk.app.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PulsingGlow(
    isPulsing: Boolean,
    glowColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 320.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_glow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = if (isPulsing) 0.15f else 0.05f,
        targetValue = if (isPulsing) 0.45f else 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = if (isPulsing) 0.92f else 0.90f,
        targetValue = if (isPulsing) 1.08f else 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = (size.toPx() / 2f) * scale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = alpha),
                        glowColor.copy(alpha = alpha * 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(size.toPx() / 2f, size.toPx() / 2f),
                    radius = radius
                ),
                radius = radius,
                center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            )
        }
        content()
    }
}
