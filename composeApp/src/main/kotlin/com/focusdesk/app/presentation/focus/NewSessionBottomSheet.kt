package com.focusdesk.app.presentation.focus

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Fireplace
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.app.presentation.designsystem.LocalFocusDeskColors
import com.focusdesk.app.presentation.designsystem.LocalHapticEngine
import com.focusdesk.core.platform.HapticFeedbackType
import com.focusdesk.domain.model.Soundscape

data class MoodOption(val name: String, val icon: ImageVector)

val moodOptions = listOf(
    MoodOption("Flow", Icons.Outlined.Bolt),
    MoodOption("Deep Work", Icons.Outlined.Psychology),
    MoodOption("Calm", Icons.Outlined.Eco),
    MoodOption("Energise", Icons.Outlined.LocalFireDepartment),
    MoodOption("Creative", Icons.Outlined.Brush),
    MoodOption("Nature", Icons.Outlined.Park)
)

data class SoundUiItem(
    val soundscape: Soundscape,
    val title: String,
    val icon: ImageVector,
    val isPremium: Boolean = false
)

val soundUiItems = listOf(
    SoundUiItem(Soundscape.Rain, "Rain", Icons.Outlined.WaterDrop),
    SoundUiItem(Soundscape.HeavyRain, "Heavy Rain", Icons.Outlined.Thunderstorm, isPremium = true),
    SoundUiItem(Soundscape.Forest, "Forest", Icons.Outlined.Forest),
    SoundUiItem(Soundscape.Ocean, "Ocean", Icons.Outlined.Waves),
    SoundUiItem(Soundscape.Thunderstorm, "Thunderstorm", Icons.Outlined.Thunderstorm, isPremium = true),
    SoundUiItem(Soundscape.Birdsong, "Birdsong", Icons.Outlined.AutoAwesome),
    SoundUiItem(Soundscape.CoffeeShop, "Coffee Shop", Icons.Outlined.Coffee),
    SoundUiItem(Soundscape.Fireplace, "Fireplace", Icons.Outlined.Fireplace),
    SoundUiItem(Soundscape.Library, "Library", Icons.Outlined.LocalLibrary, isPremium = true),
    SoundUiItem(Soundscape.BrownNoise, "Brown Noise", Icons.Outlined.GraphicEq),
    SoundUiItem(Soundscape.WhiteNoise, "White Noise", Icons.Outlined.Air),
    SoundUiItem(Soundscape.PinkNoise, "Pink Noise", Icons.Outlined.GraphicEq, isPremium = true),
    SoundUiItem(Soundscape.LofiBeats, "Lo-Fi Beats", Icons.Outlined.MusicNote, isPremium = true),
    SoundUiItem(Soundscape.Classical, "Classical", Icons.Outlined.Headphones, isPremium = true)
)

@Composable
fun NewSessionBottomSheet(
    onDismiss: () -> Unit,
    onStartFocusing: (taskTitle: String, category: String, mood: String, durationMinutes: Int, soundscape: Soundscape) -> Unit,
    modifier: Modifier = Modifier,
    initialDurationMinutes: Int = 25
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current

    var taskTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Work") }
    var selectedMood by remember { mutableStateOf("Calm") }
    var selectedDurationMinutes by remember { mutableIntStateOf(initialDurationMinutes) }
    var selectedBreakMinutes by remember { mutableIntStateOf(5) }
    var selectedSound by remember { mutableStateOf(Soundscape.Birdsong) }

    val categories = listOf("Work", "Study", "Creative", "Reading")
    val durations = listOf(5, 15, 25, 45, 90)
    val breaks = listOf(0, 5, 10, 15, 20)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(themeColors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Row (Close Button and Title)
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circular Close Button on Left
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEBEBE5))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            hapticEngine.perform(HapticFeedbackType.Light)
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = themeColors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "New Session",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                // Section: TASK
                SectionLabel(title = "TASK")
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(themeColors.cardBackground)
                        .border(1.dp, themeColors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (taskTitle.isEmpty()) {
                        Text(
                            text = "e.g. Write chapter 3",
                            fontSize = 15.sp,
                            color = themeColors.textMuted
                        )
                    }
                    BasicTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = themeColors.textPrimary
                        ),
                        cursorBrush = SolidColor(themeColors.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section: CATEGORY
                SectionLabel(title = "CATEGORY")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        val chipIcon = when (cat) {
                            "Work" -> Icons.Outlined.Work
                            "Study" -> Icons.AutoMirrored.Outlined.MenuBook
                            "Creative" -> Icons.Outlined.Brush
                            else -> Icons.AutoMirrored.Outlined.MenuBook
                        }
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
                                    imageVector = chipIcon,
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

                // Section: MOOD
                SectionLabel(title = "MOOD")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    moodOptions.forEach { mood ->
                        val isSelected = selectedMood == mood.name
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) themeColors.navBarSelectedPill else themeColors.cardBackground)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) themeColors.primary else themeColors.cardBorder,
                                        shape = CircleShape
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        hapticEngine.perform(HapticFeedbackType.Selection)
                                        selectedMood = mood.name
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = mood.icon,
                                    contentDescription = mood.name,
                                    tint = if (isSelected) themeColors.primary else themeColors.textSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = mood.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) themeColors.textPrimary else themeColors.textMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section: FOCUS DURATION
                SectionLabel(title = "FOCUS DURATION")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durations.forEach { dur ->
                        val isSelected = selectedDurationMinutes == dur
                        Box(
                            modifier = Modifier
                                .height(38.dp)
                                .clip(RoundedCornerShape(19.dp))
                                .background(if (isSelected) themeColors.primaryAccent else themeColors.cardBackground)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.Transparent else themeColors.cardBorder,
                                    shape = RoundedCornerShape(19.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    hapticEngine.perform(HapticFeedbackType.Selection)
                                    selectedDurationMinutes = dur
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${dur}m",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) themeColors.onPrimaryAccent else themeColors.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section: BREAK DURATION
                SectionLabel(title = "BREAK DURATION")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    breaks.forEach { brk ->
                        val isSelected = selectedBreakMinutes == brk
                        val label = if (brk == 0) "No break" else "${brk}m"
                        Box(
                            modifier = Modifier
                                .height(38.dp)
                                .clip(RoundedCornerShape(19.dp))
                                .background(if (isSelected) themeColors.primaryAccent else themeColors.cardBackground)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.Transparent else themeColors.cardBorder,
                                    shape = RoundedCornerShape(19.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    hapticEngine.perform(HapticFeedbackType.Selection)
                                    selectedBreakMinutes = brk
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) themeColors.onPrimaryAccent else themeColors.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section: AMBIENT SOUND
                SectionLabel(title = "AMBIENT SOUND")
                Spacer(modifier = Modifier.height(10.dp))

                // 2-Column Sound Grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in soundUiItems.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val item1 = soundUiItems[i]
                            val item2 = if (i + 1 < soundUiItems.size) soundUiItems[i + 1] else null

                            SoundGridCard(
                                item = item1,
                                isSelected = selectedSound == item1.soundscape,
                                onClick = {
                                    if (!item1.isPremium) {
                                        hapticEngine.perform(HapticFeedbackType.Selection)
                                        selectedSound = item1.soundscape
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            if (item2 != null) {
                                SoundGridCard(
                                    item = item2,
                                    isSelected = selectedSound == item2.soundscape,
                                    onClick = {
                                        if (!item2.isPremium) {
                                            hapticEngine.perform(HapticFeedbackType.Selection)
                                            selectedSound = item2.soundscape
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(90.dp))
            }

            // Pinned Bottom Button: "▶ Start Focusing · 25 min"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(27.dp),
                            ambientColor = themeColors.primaryAccent.copy(alpha = 0.3f),
                            spotColor = themeColors.primaryAccent.copy(alpha = 0.4f)
                        )
                        .clip(RoundedCornerShape(27.dp))
                        .background(themeColors.primaryAccent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            hapticEngine.perform(HapticFeedbackType.Success)
                            onStartFocusing(
                                if (taskTitle.isBlank()) "Test" else taskTitle,
                                selectedCategory,
                                selectedMood,
                                selectedDurationMinutes,
                                selectedSound
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = themeColors.onPrimaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Start Focusing · $selectedDurationMinutes min",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.onPrimaryAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    val themeColors = LocalFocusDeskColors.current
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = themeColors.textMuted
    )
}

@Composable
private fun SoundGridCard(
    item: SoundUiItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalFocusDeskColors.current
    Box(
        modifier = modifier
            .height(72.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(themeColors.cardBackground)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) themeColors.primary else themeColors.cardBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) themeColors.navBarSelectedPill else Color(0xFFF1F4EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = if (isSelected) themeColors.primary else themeColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeColors.textPrimary
                    )
                    if (item.isPremium) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "👑 Premium",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFB5802E)
                            )
                        }
                    } else {
                        Text(
                            text = "Tap to preview",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = themeColors.textMuted
                        )
                    }
                }
            }

            if (item.isPremium) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFFD4A853),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
