package com.focusdesk.app.presentation.designsystem

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * 1:1 Motion Design System mapping SwiftUI spring curves and easing to Jetpack Compose APIs.
 */
object SwiftUiMotion {

    /**
     * Matches SwiftUI `.animation(.snappy, value:)`
     */
    fun <T> snappy(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.82f,
        stiffness = 380f
    )

    /**
     * Matches SwiftUI `.animation(.smooth, value:)`
     */
    fun <T> smooth(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.90f,
        stiffness = 220f
    )

    /**
     * Matches SwiftUI `.animation(.bouncy, value:)`
     */
    fun <T> bouncy(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.65f,
        stiffness = 300f
    )

    /**
     * Matches SwiftUI `.animation(.easeInOut(duration: 0.3))`
     */
    fun <T> easeInOut(durationMillis: Int = 300): FiniteAnimationSpec<T> = tween(
        durationMillis = durationMillis,
        easing = FastOutSlowInEasing
    )

    /**
     * Standard Tab Screen Transition (Fade + Subtle Scale)
     */
    val ScreenTransition: ContentTransform =
        (fadeIn(animationSpec = tween(220, easing = LinearOutSlowInEasing)) +
                scaleIn(initialScale = 0.96f, animationSpec = snappy<Float>()))
            .togetherWith(
                fadeOut(animationSpec = tween(180)) +
                        scaleOut(targetScale = 1.02f, animationSpec = snappy<Float>())
            )

    /**
     * Navigation Slide Transition
     */
    val NavEnterTransition: EnterTransition =
        slideInHorizontally(initialOffsetX = { it / 3 }, animationSpec = snappy<IntOffset>()) +
                fadeIn(animationSpec = tween(200))

    val NavExitTransition: ExitTransition =
        slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = snappy<IntOffset>()) +
                fadeOut(animationSpec = tween(200))

    val NavPopEnterTransition: EnterTransition =
        slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = snappy<IntOffset>()) +
                fadeIn(animationSpec = tween(200))

    val NavPopExitTransition: ExitTransition =
        slideOutHorizontally(targetOffsetX = { it / 3 }, animationSpec = snappy<IntOffset>()) +
                fadeOut(animationSpec = tween(200))

    /**
     * Bottom Sheet & Modal Transitions
     */
    val ModalSlideIn: EnterTransition =
        slideInVertically(initialOffsetY = { it }, animationSpec = snappy<IntOffset>()) +
                fadeIn(animationSpec = tween(250))

    val ModalSlideOut: ExitTransition =
        slideOutVertically(targetOffsetY = { it }, animationSpec = snappy<IntOffset>()) +
                fadeOut(animationSpec = tween(200))
}
