package com.focusdesk.app.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import kotlin.math.abs
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Star
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.focusdesk.app.presentation.designsystem.ExitConfirmationDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.app.presentation.designsystem.LocalFocusDeskColors
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.core.platform.HapticFeedbackType
import kotlinx.coroutines.launch

data class OnboardingSlideData(
    val stepLabel: String,
    val category: String,
    val title: String,
    val description: String,
    val heroCenterIcon: ImageVector,
    val watermarkTopLeft: ImageVector,
    val watermarkTopRight: ImageVector,
    val watermarkBottomLeft: ImageVector,
    val watermarkBottomRight: ImageVector
)

val onboardingSlides = listOf(
    OnboardingSlideData(
        stepLabel = "1 / 4",
        category = "FOCUS TIMER",
        title = "One task.\nDeep focus.",
        description = "Start a distraction-free timer and do your best work, one session at a time.",
        heroCenterIcon = Icons.Outlined.Speed,
        watermarkTopLeft = Icons.Outlined.Nightlight,
        watermarkTopRight = Icons.Outlined.Headphones,
        watermarkBottomLeft = Icons.Outlined.CheckCircle,
        watermarkBottomRight = Icons.Outlined.Bolt
    ),
    OnboardingSlideData(
        stepLabel = "2 / 4",
        category = "INSIGHTS",
        title = "Watch yourself\ngrow.",
        description = "Charts, heatmaps, and streaks reveal exactly where your time goes.",
        heroCenterIcon = Icons.Outlined.BarChart,
        watermarkTopLeft = Icons.Outlined.CalendarToday,
        watermarkTopRight = Icons.Outlined.GraphicEq,
        watermarkBottomLeft = Icons.Outlined.LocalFireDepartment,
        watermarkBottomRight = Icons.Outlined.AccessTime
    ),
    OnboardingSlideData(
        stepLabel = "3 / 4",
        category = "GAMIFICATION",
        title = "Every day\ncounts.",
        description = "Earn XP, unlock achievements, and keep your focus streak alive.",
        heroCenterIcon = Icons.Outlined.LocalFireDepartment,
        watermarkTopLeft = Icons.Outlined.EmojiEvents,
        watermarkTopRight = Icons.Outlined.Star,
        watermarkBottomLeft = Icons.Outlined.Bolt,
        watermarkBottomRight = Icons.Outlined.EmojiEvents
    ),
    OnboardingSlideData(
        stepLabel = "4 / 4",
        category = "READY",
        title = "Ready to do\ngreat work?",
        description = "Your first session is one tap away. Everything is set up.",
        heroCenterIcon = Icons.Outlined.AutoAwesome,
        watermarkTopLeft = Icons.Outlined.AutoAwesome,
        watermarkTopRight = Icons.Outlined.CheckCircle,
        watermarkBottomLeft = Icons.Outlined.Headphones,
        watermarkBottomRight = Icons.Outlined.Star
    )
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { onboardingSlides.size })
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showExitConfirmationDialog by remember { mutableStateOf(false) }

    // Magical Entrance Animations on screen launch
    val topBarAnim = remember { Animatable(0f) }
    val heroAnim = remember { Animatable(0f) }
    val textAnim = remember { Animatable(0f) }
    val bottomAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            topBarAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.72f, stiffness = 280f))
        }
        launch {
            delay(90L)
            heroAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            delay(180L)
            textAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.68f, stiffness = 220f))
        }
        launch {
            delay(270L)
            bottomAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 240f))
        }
    }

    // Android Back button handling on onboarding
    BackHandler(enabled = true) {
        if (showExitConfirmationDialog) {
            showExitConfirmationDialog = false
        } else if (pagerState.currentPage > 0) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        } else {
            showExitConfirmationDialog = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(themeColors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Top Navigation Bar (Step pill and Skip)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val p = topBarAnim.value
                        translationY = (1f - p) * -30.dp.toPx()
                        scaleX = 0.92f + (0.08f * p)
                        scaleY = 0.92f + (0.08f * p)
                        alpha = p.coerceIn(0f, 1f)
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step Pill (e.g. 1 / 4) with vertical slide transition
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEBECE6))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = onboardingSlides[pagerState.currentPage].stepLabel,
                        transitionSpec = {
                            (slideInVertically { -it / 2 } + fadeIn(tween(180))) togetherWith (slideOutVertically { it / 2 } + fadeOut(tween(180)))
                        },
                        label = "step_pill_anim"
                    ) { stepText ->
                        Text(
                            text = stepText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = themeColors.textSecondary
                        )
                    }
                }

                // Skip Button (Visible on slides 1..3 with smooth exit)
                AnimatedVisibility(
                    visible = pagerState.currentPage < 3,
                    enter = fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.85f),
                    exit = fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.85f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE3ECD8))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Light)
                                onComplete()
                            }
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Skip",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = themeColors.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Horizontal Pager for the slides
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val slide = onboardingSlides[page]

                // Real-time page offset during swipe gestures
                val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).coerceIn(-1f, 1f)
                val absOffset = abs(pageOffset)

                val infiniteTransition = rememberInfiniteTransition(label = "banner_icons_$page")

                // Center icon floating oscillation & breathing pulse
                val centerFloatY by infiniteTransition.animateFloat(
                    initialValue = -6f,
                    targetValue = 6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2400, easing = EaseInOutCubic),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "center_float"
                )
                val centerPulseScale by infiniteTransition.animateFloat(
                    initialValue = 0.94f,
                    targetValue = 1.06f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "center_scale"
                )

                // Ambient glow halo scale & alpha
                val glowScale by infiniteTransition.animateFloat(
                    initialValue = 0.88f,
                    targetValue = 1.18f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2800, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "glow_scale"
                )
                val glowAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.30f,
                    targetValue = 0.65f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2800, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "glow_alpha"
                )

                // Expanding radar ripple ring
                val rippleScale by infiniteTransition.animateFloat(
                    initialValue = 0.95f,
                    targetValue = 1.55f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2200, easing = EaseOutQuad),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "ripple_scale"
                )
                val rippleAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.45f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2200, easing = EaseOutQuad),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "ripple_alpha"
                )

                // Corner watermark floating rotation & breathing
                val watermarkRotate1 by infiniteTransition.animateFloat(
                    initialValue = -10f,
                    targetValue = 10f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3400, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "wm_rot1"
                )
                val watermarkRotate2 by infiniteTransition.animateFloat(
                    initialValue = 10f,
                    targetValue = -10f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3800, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "wm_rot2"
                )
                val watermarkAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.15f,
                    targetValue = 0.35f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2600, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "wm_alpha"
                )

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Hero Card with 3D Tilt, Scale & Parallax during swipe + Spring Entrance
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .graphicsLayer {
                                val hp = heroAnim.value
                                val entranceScale = 0.88f + (0.12f * hp)
                                val entranceY = (1f - hp) * 45.dp.toPx()

                                cameraDistance = 14f * density
                                rotationY = pageOffset * -18f
                                rotationZ = pageOffset * -2.5f
                                translationY = entranceY
                                scaleX = (1f - (absOffset * 0.10f)) * entranceScale
                                scaleY = (1f - (absOffset * 0.10f)) * entranceScale
                                alpha = ((1f - absOffset * 0.35f) * hp).coerceIn(0f, 1f)
                            }
                            .shadow(
                                elevation = ((1f - absOffset) * 10f).coerceAtLeast(0f).dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = Color.Black.copy(alpha = 0.25f),
                                spotColor = Color.Black.copy(alpha = 0.35f)
                            )
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF2C3E32),
                                        Color(0xFF16231A),
                                        Color(0xFF101913)
                                    )
                                )
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Watermark icons in 4 corners with floating rotation, breathing alpha, and parallax drift
                        Icon(
                            imageVector = slide.watermarkTopLeft,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = watermarkAlpha),
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopStart)
                                .padding(top = 8.dp, start = 8.dp)
                                .graphicsLayer {
                                    translationX = pageOffset * 22.dp.toPx()
                                    rotationZ = watermarkRotate1 + (pageOffset * 15f)
                                    translationY = -watermarkRotate1 * 0.3f
                                }
                        )
                        Icon(
                            imageVector = slide.watermarkTopRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = watermarkAlpha),
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 8.dp)
                                .graphicsLayer {
                                    translationX = pageOffset * 22.dp.toPx()
                                    rotationZ = watermarkRotate2 + (pageOffset * 15f)
                                    translationY = watermarkRotate2 * 0.3f
                                }
                        )
                        Icon(
                            imageVector = slide.watermarkBottomLeft,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = watermarkAlpha),
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomStart)
                                .padding(bottom = 8.dp, start = 8.dp)
                                .graphicsLayer {
                                    translationX = pageOffset * 22.dp.toPx()
                                    rotationZ = watermarkRotate2 + (pageOffset * 15f)
                                    translationY = -watermarkRotate2 * 0.3f
                                }
                        )
                        Icon(
                            imageVector = slide.watermarkBottomRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = watermarkAlpha),
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 8.dp, end = 8.dp)
                                .graphicsLayer {
                                    translationX = pageOffset * 22.dp.toPx()
                                    rotationZ = watermarkRotate1 + (pageOffset * 15f)
                                    translationY = watermarkRotate1 * 0.3f
                                }
                        )

                        // Central Ambient Breathing Glow Ring
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .graphicsLayer {
                                    scaleX = glowScale
                                    scaleY = glowScale
                                    alpha = glowAlpha
                                }
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            Color(0xFF7CB862).copy(alpha = 0.55f),
                                            Color(0xFF38523A).copy(alpha = 0.25f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Expanding Ripple Aura Ring
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .graphicsLayer {
                                    scaleX = rippleScale
                                    scaleY = rippleScale
                                    alpha = rippleAlpha
                                }
                                .clip(CircleShape)
                                .border(1.5.dp, Color(0xFF90DB72).copy(alpha = rippleAlpha), CircleShape)
                        )

                        // Center White Floating Icon Ring with opposite parallax gliding inside card
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .graphicsLayer {
                                    translationX = pageOffset * -45.dp.toPx()
                                    translationY = centerFloatY
                                    scaleX = centerPulseScale * (1f - absOffset * 0.15f)
                                    scaleY = centerPulseScale * (1f - absOffset * 0.15f)
                                }
                                .shadow(8.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.3f))
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = slide.heroCenterIcon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Text Content with Staggered Multi-Layer Parallax + Spring Entrance
                    Column(
                        modifier = Modifier.graphicsLayer {
                            val tp = textAnim.value
                            translationY = (1f - tp) * 35.dp.toPx()
                            scaleX = 0.92f + (0.08f * tp)
                            scaleY = 0.92f + (0.08f * tp)
                            alpha = tp.coerceIn(0f, 1f)
                        }
                    ) {
                        Text(
                            text = slide.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = themeColors.primary,
                            modifier = Modifier.graphicsLayer {
                                translationX = pageOffset * -35.dp.toPx()
                                alpha = (1f - absOffset * 1.4f).coerceIn(0f, 1f)
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = slide.title,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 38.sp,
                            letterSpacing = (-0.5).sp,
                            color = themeColors.textPrimary,
                            modifier = Modifier.graphicsLayer {
                                translationX = pageOffset * -70.dp.toPx()
                                scaleX = 1f - (absOffset * 0.08f)
                                scaleY = 1f - (absOffset * 0.08f)
                                alpha = (1f - absOffset * 1.6f).coerceIn(0f, 1f)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = slide.description,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 22.sp,
                            color = themeColors.textSecondary,
                            modifier = Modifier.graphicsLayer {
                                translationX = pageOffset * -105.dp.toPx()
                                alpha = (1f - absOffset * 1.8f).coerceIn(0f, 1f)
                            }
                        )
                    }
                }
            }

            // Bottom Bar with Dynamic Progress Dots and Pop-up Action Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .graphicsLayer {
                        val bp = bottomAnim.value
                        translationY = (1f - bp) * 35.dp.toPx()
                        scaleX = 0.90f + (0.10f * bp)
                        scaleY = 0.90f + (0.10f * bp)
                        alpha = bp.coerceIn(0f, 1f)
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dynamic Indicator Dots (smoothly morphing width & color with swipe gesture)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(onboardingSlides.size) { index ->
                        val currentContinuousPage = pagerState.currentPage + pagerState.currentPageOffsetFraction
                        val pageDistance = abs(currentContinuousPage - index)
                        val activeRatio = (1f - pageDistance).coerceIn(0f, 1f)

                        val dotWidth = 6.dp + (18.dp * activeRatio)
                        val dotColor = lerp(Color(0xFFD6DDD2), themeColors.primary, activeRatio)

                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(dotWidth)
                                .clip(RoundedCornerShape(3.dp))
                                .background(dotColor)
                        )
                    }
                }

                // Action Button: Arrow Circle (slides 1..3) -> Spring Pop-up "Get Started" Pill (slide 4)
                AnimatedContent(
                    targetState = pagerState.currentPage == 3,
                    transitionSpec = {
                        if (targetState) {
                            // "Get Started" pops up with energetic spring overshoot
                            (scaleIn(
                                initialScale = 0.32f,
                                animationSpec = spring(dampingRatio = 0.50f, stiffness = 300f)
                            ) + fadeIn(animationSpec = tween(220))) togetherWith (scaleOut(
                                targetScale = 0.5f,
                                animationSpec = tween(150)
                            ) + fadeOut(animationSpec = tween(150)))
                        } else {
                            (scaleIn(
                                initialScale = 0.5f,
                                animationSpec = spring(dampingRatio = 0.65f, stiffness = 380f)
                            ) + fadeIn(animationSpec = tween(200))) togetherWith (scaleOut(
                                targetScale = 0.5f,
                                animationSpec = tween(150)
                            ) + fadeOut(animationSpec = tween(150)))
                        }
                    },
                    label = "onboard_action_btn"
                ) { isReadyStep ->
                    if (!isReadyStep) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .shadow(6.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.12f))
                                .clip(CircleShape)
                                .background(themeColors.primary)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    hapticEngine.perform(HapticFeedbackType.Medium)
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        // "Get Started" button with ambient breathing glow halo & dynamic play icon bounce
                        val readyPulse = rememberInfiniteTransition(label = "ready_cta_pulse")
                        val ctaGlowScale by readyPulse.animateFloat(
                            initialValue = 0.98f,
                            targetValue = 1.15f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = EaseInOutSine),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "cta_glow_scale"
                        )
                        val ctaGlowAlpha by readyPulse.animateFloat(
                            initialValue = 0.45f,
                            targetValue = 0.08f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = EaseInOutSine),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "cta_glow_alpha"
                        )
                        val playIconShift by readyPulse.animateFloat(
                            initialValue = 0f,
                            targetValue = 3.5f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = EaseInOutSine),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "play_icon_shift"
                        )

                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            // Pulsing ambient glow aura
                            Box(
                                modifier = Modifier
                                    .height(52.dp)
                                    .width(155.dp)
                                    .graphicsLayer {
                                        scaleX = ctaGlowScale
                                        scaleY = ctaGlowScale
                                        alpha = ctaGlowAlpha
                                    }
                                    .clip(RoundedCornerShape(26.dp))
                                    .background(themeColors.primary)
                            )

                            // Main Pop-up Button
                            Box(
                                modifier = Modifier
                                    .height(52.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(26.dp),
                                        ambientColor = themeColors.primary.copy(alpha = 0.35f),
                                        spotColor = themeColors.primary.copy(alpha = 0.45f)
                                    )
                                    .clip(RoundedCornerShape(26.dp))
                                    .background(themeColors.primary)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        hapticEngine.perform(HapticFeedbackType.Success)
                                        onComplete()
                                    }
                                    .padding(horizontal = 22.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(19.dp)
                                            .graphicsLayer { translationX = playIconShift }
                                    )
                                    Text(
                                        text = "Get Started",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = (-0.2).sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Exit Confirmation Dialog on root onboarding slide
        if (showExitConfirmationDialog) {
            ExitConfirmationDialog(
                onDismissRequest = { showExitConfirmationDialog = false },
                onConfirmExit = {
                    showExitConfirmationDialog = false
                    (context as? Activity)?.finish()
                }
            )
        }
    }
}
