package com.focusdesk.app.presentation.sounds

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Fireplace
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.app.presentation.designsystem.ElevatedTactileSlider
import com.focusdesk.app.presentation.designsystem.LocalFocusDeskColors
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.core.platform.HapticFeedbackType
import com.focusdesk.domain.model.Soundscape

data class SoundCardData(
    val soundscape: Soundscape,
    val title: String,
    val icon: ImageVector,
    val category: String,
    val isPremium: Boolean = false
)

val soundsCatalog = listOf(
    SoundCardData(Soundscape.Rain, "Rain", Icons.Outlined.WaterDrop, "Nature"),
    SoundCardData(Soundscape.HeavyRain, "Heavy Rain", Icons.Outlined.Thunderstorm, "Nature", isPremium = true),
    SoundCardData(Soundscape.Forest, "Forest", Icons.Outlined.Forest, "Nature"),
    SoundCardData(Soundscape.Ocean, "Ocean", Icons.Outlined.Waves, "Nature"),
    SoundCardData(Soundscape.Thunderstorm, "Thunderstorm", Icons.Outlined.Thunderstorm, "Nature", isPremium = true),
    SoundCardData(Soundscape.Birdsong, "Birdsong", Icons.Outlined.AutoAwesome, "Nature"),
    SoundCardData(Soundscape.CoffeeShop, "Coffee Shop", Icons.Outlined.Coffee, "Indoor"),
    SoundCardData(Soundscape.Fireplace, "Fireplace", Icons.Outlined.Fireplace, "Indoor"),
    SoundCardData(Soundscape.Library, "Library", Icons.Outlined.LocalLibrary, "Indoor", isPremium = true),
    SoundCardData(Soundscape.BrownNoise, "Brown Noise", Icons.Outlined.GraphicEq, "Noise"),
    SoundCardData(Soundscape.WhiteNoise, "White Noise", Icons.Outlined.Air, "Noise"),
    SoundCardData(Soundscape.PinkNoise, "Pink Noise", Icons.Outlined.GraphicEq, "Noise", isPremium = true),
    SoundCardData(Soundscape.LofiBeats, "Lo-Fi Beats", Icons.Outlined.MusicNote, "Music", isPremium = true),
    SoundCardData(Soundscape.Classical, "Classical", Icons.Outlined.Headphones, "Music", isPremium = true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundsScreen(
    currentSound: Soundscape,
    isPlaying: Boolean,
    soundVolume: Float,
    onPlaySound: (Soundscape) -> Unit,
    onStopSound: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onScrollStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current
    val listState = rememberLazyListState()

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf(
        "All" to Icons.Filled.GridView,
        "Nature" to Icons.Filled.Search,
        "Indoor" to Icons.Filled.Home,
        "Noise" to Icons.Filled.GraphicEq,
        "Music" to Icons.Filled.MusicNote
    )

    val favorites = remember { mutableStateListOf<Soundscape>() }

    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 40
        }
    }

    // Magical Entrance Rearrangement Animations on screen launch
    val headerAnim = remember { Animatable(0f) }
    val categoriesAnim = remember { Animatable(0f) }
    val catalogAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            headerAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.72f, stiffness = 280f))
        }
        launch {
            delay(80L)
            categoriesAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.70f, stiffness = 240f))
        }
        launch {
            delay(160L)
            catalogAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 220f))
        }
    }

    LaunchedEffect(isScrolled) {
        onScrollStateChange(!isScrolled)
    }

    val filteredSounds = remember(selectedCategory) {
        if (selectedCategory == "All") soundsCatalog else soundsCatalog.filter { it.category == selectedCategory }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(themeColors.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(28.dp))

                // Title
                Text(
                    text = "Sounds",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    color = themeColors.textPrimary,
                    modifier = Modifier.graphicsLayer {
                        val p = headerAnim.value
                        translationY = (1f - p) * -35.dp.toPx()
                        scaleX = 0.90f + (0.10f * p)
                        scaleY = 0.90f + (0.10f * p)
                        alpha = p.coerceIn(0f, 1f)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val p = categoriesAnim.value
                            translationY = (1f - p) * 25.dp.toPx()
                            scaleX = 0.92f + (0.08f * p)
                            scaleY = 0.92f + (0.08f * p)
                            alpha = p.coerceIn(0f, 1f)
                        }
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (cat, icon) ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) themeColors.primaryAccent else themeColors.cardBackground)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.Transparent else themeColors.cardBorder,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    hapticEngine.perform(HapticFeedbackType.Selection)
                                    selectedCategory = cat
                                }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) themeColors.onPrimaryAccent else themeColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = cat,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) themeColors.onPrimaryAccent else themeColors.textPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // 2-Column Sound Cards Grid
            val rowsCount = (filteredSounds.size + 1) / 2
            items(rowsCount) { rowIndex ->
                val idx1 = rowIndex * 2
                val idx2 = idx1 + 1
                val item1 = filteredSounds[idx1]
                val item2 = if (idx2 < filteredSounds.size) filteredSounds[idx2] else null

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val p = catalogAnim.value
                            translationY = (1f - p) * 35.dp.toPx()
                            scaleX = 0.90f + (0.10f * p)
                            scaleY = 0.90f + (0.10f * p)
                            alpha = p.coerceIn(0f, 1f)
                        }
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isPlaying1 = isPlaying && currentSound == item1.soundscape
                    val isFav1 = favorites.contains(item1.soundscape)

                    SoundBigCard(
                        sound = item1,
                        isPlaying = isPlaying1,
                        isFavorite = isFav1,
                        onToggleFavorite = {
                            if (isFav1) favorites.remove(item1.soundscape) else favorites.add(item1.soundscape)
                        },
                        onClick = {
                            if (!item1.isPremium) {
                                hapticEngine.perform(HapticFeedbackType.Medium)
                                if (isPlaying1) onStopSound() else onPlaySound(item1.soundscape)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    if (item2 != null) {
                        val isPlaying2 = isPlaying && currentSound == item2.soundscape
                        val isFav2 = favorites.contains(item2.soundscape)
                        SoundBigCard(
                            sound = item2,
                            isPlaying = isPlaying2,
                            isFavorite = isFav2,
                            onToggleFavorite = {
                                if (isFav2) favorites.remove(item2.soundscape) else favorites.add(item2.soundscape)
                            },
                            onClick = {
                                if (!item2.isPremium) {
                                    hapticEngine.perform(HapticFeedbackType.Medium)
                                    if (isPlaying2) onStopSound() else onPlaySound(item2.soundscape)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(if (isPlaying) 180.dp else 120.dp))
            }
        }

        // Floating Now Playing Sheet (matching screenshot 11)
        AnimatedVisibility(
            visible = isPlaying && currentSound != Soundscape.None,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 20.dp, end = 20.dp, bottom = 84.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(26.dp), ambientColor = Color.Black.copy(alpha = 0.08f))
                    .clip(RoundedCornerShape(26.dp))
                    .background(themeColors.cardBackground)
                    .border(1.dp, themeColors.cardBorder, RoundedCornerShape(26.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFD4D8CF))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 1: Now Playing + Stop All
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE9F2E4)),
                                contentAlignment = Alignment.Center
                            ) {
                                EqualizerSoundwave(
                                    color = themeColors.primary,
                                    barCount = 4
                                )
                            }

                            Column {
                                Text(
                                    text = "Now Playing",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.textPrimary
                                )
                                Text(
                                    text = "1 ambient layer",
                                    fontSize = 12.sp,
                                    color = themeColors.textMuted
                                )
                            }
                        }

                        // Pink "■ Stop All" Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFDECEF))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    hapticEngine.perform(HapticFeedbackType.Medium)
                                    onStopSound()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(Color(0xFFD94D6E))
                                )
                                Text(
                                    text = "Stop All",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD94D6E)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 2: Sound row with volume slider, speaker icon, and X icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val currentCard = soundsCatalog.firstOrNull { it.soundscape == currentSound }
                        Icon(
                            imageVector = currentCard?.icon ?: Icons.Outlined.WaterDrop,
                            contentDescription = null,
                            tint = themeColors.primary,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = currentSound.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = themeColors.textPrimary,
                            modifier = Modifier.width(68.dp)
                        )

                        // Elevated Volume Slider
                        ElevatedTactileSlider(
                            value = soundVolume,
                            onValueChange = onVolumeChange,
                            valueRange = 0f..1f,
                            modifier = Modifier.weight(1f),
                            accentColor = themeColors.primary
                        )

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = themeColors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )

                        // Close button
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8ECE5))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    hapticEngine.perform(HapticFeedbackType.Light)
                                    onStopSound()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = themeColors.textSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Floating Headphones Icon when scrolled (frame 3826)
        AnimatedVisibility(
            visible = isScrolled,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .shadow(6.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.08f))
                    .clip(CircleShape)
                    .background(themeColors.cardBackground)
                    .border(1.dp, themeColors.cardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Headphones,
                    contentDescription = "Sounds",
                    tint = themeColors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun SoundBigCard(
    sound: SoundCardData,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    Box(
        modifier = modifier
            .height(145.dp)
            .shadow(2.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(alpha = 0.03f))
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (sound.isPremium) Color(0xFFFBF5E8) else themeColors.cardBackground
            )
            .border(
                width = 1.dp,
                color = if (sound.isPremium) Color(0xFFECD8B4) else themeColors.cardBorder,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(14.dp)
    ) {
        // Top Row: Heart / Crown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            if (sound.isPremium) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3E2C4)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 12.sp)
                }
            } else {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color(0xFFE25B81) else Color(0xFFB8C2B4),
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onToggleFavorite
                        )
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Circle with breathing pulse when playing
            Box(
                modifier = Modifier.size(54.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    val cardPulse = rememberInfiniteTransition(label = "card_pulse")
                    val haloScale by cardPulse.animateFloat(
                        initialValue = 0.95f,
                        targetValue = 1.28f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "halo_scale"
                    )
                    val haloAlpha by cardPulse.animateFloat(
                        initialValue = 0.35f,
                        targetValue = 0.06f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "halo_alpha"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = haloScale
                                scaleY = haloScale
                                alpha = haloAlpha
                            }
                            .clip(CircleShape)
                            .background(themeColors.primary)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) themeColors.primary else Color(0xFFF0F4EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = sound.icon,
                        contentDescription = null,
                        tint = if (isPlaying) Color.White else themeColors.textSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = sound.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = themeColors.textPrimary
            )

            if (isPlaying) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE5EFE1))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    EqualizerSoundwave(
                        color = themeColors.primary,
                        barCount = 3,
                        modifier = Modifier.height(10.dp)
                    )
                    Text(
                        text = "Playing",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.primary
                    )
                }
            }
        }
    }
}

@Composable
fun EqualizerSoundwave(
    color: Color,
    modifier: Modifier = Modifier,
    barCount: Int = 4
) {
    val infiniteTransition = rememberInfiniteTransition(label = "eq_bars")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(380, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eq_1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eq_2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eq_3"
    )
    val h4 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(440, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eq_4"
    )

    val heights = listOf(h1, h2, h3, h4)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.take(barCount).forEach { ratio ->
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .height((14.dp * ratio).coerceAtLeast(3.dp))
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(color)
            )
        }
    }
}
