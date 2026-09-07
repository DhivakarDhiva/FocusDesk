package com.focusdesk.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import com.focusdesk.app.presentation.analytics.AnalyticsScreen
import com.focusdesk.app.presentation.analytics.AnalyticsViewModel
import com.focusdesk.app.presentation.designsystem.EndSessionConfirmationDialog
import com.focusdesk.app.presentation.designsystem.ExitConfirmationDialog
import com.focusdesk.app.presentation.designsystem.FocusDeskTheme
import com.focusdesk.app.presentation.designsystem.LocalFocusDeskColors
import com.focusdesk.app.presentation.designsystem.LocalAudioPlayer
import com.focusdesk.app.presentation.designsystem.SwiftUiMotion
import com.focusdesk.app.presentation.focus.FocusScreen
import com.focusdesk.app.presentation.focus.NewSessionBottomSheet
import com.focusdesk.app.presentation.home.HomeScreen
import com.focusdesk.app.presentation.navigation.FocusDeskBottomNavBar
import com.focusdesk.app.presentation.navigation.Screen
import com.focusdesk.app.presentation.onboarding.OnboardingScreen
import com.focusdesk.app.presentation.settings.SettingsIntent
import com.focusdesk.app.presentation.settings.SettingsScreen
import com.focusdesk.app.presentation.settings.SettingsViewModel
import com.focusdesk.app.presentation.sounds.SoundsScreen
import com.focusdesk.app.presentation.timer.ActiveFocusSessionCard
import com.focusdesk.app.presentation.timer.SessionCompleteScreen
import com.focusdesk.app.presentation.timer.TimerIntent
import com.focusdesk.app.presentation.timer.TimerScreen
import com.focusdesk.app.presentation.timer.TimerViewModel
import com.focusdesk.domain.model.SessionStatus
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.focusdesk.app.service.DynamicIslandService
import com.focusdesk.core.platform.SoundEffect
import kotlinx.coroutines.flow.MutableStateFlow
import com.focusdesk.domain.model.Soundscape
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val isTimerScreenMinimizedFlow = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        setContent {
            val settingsViewModel: SettingsViewModel = koinViewModel()
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
            val userSettings = settingsState.settings

            FocusDeskTheme(appTheme = userSettings.appTheme) {
                FocusDeskAppRoot(
                    settingsViewModel = settingsViewModel,
                    isTimerScreenMinimizedFlow = isTimerScreenMinimizedFlow
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == ACTION_MAXIMIZE_TIMER) {
            isTimerScreenMinimizedFlow.value = false
        }
    }

    override fun onStart() {
        super.onStart()
        DynamicIslandService.notifyForeground(this)
    }

    override fun onStop() {
        DynamicIslandService.notifyBackground(this)
        super.onStop()
    }

    companion object {
        const val ACTION_MAXIMIZE_TIMER = "com.focusdesk.app.action.MAXIMIZE_TIMER"
    }
}

@Composable
fun FocusDeskAppRoot(
    settingsViewModel: SettingsViewModel,
    isTimerScreenMinimizedFlow: MutableStateFlow<Boolean>
) {
    val timerViewModel: TimerViewModel = koinViewModel()
    val analyticsViewModel: AnalyticsViewModel = koinViewModel()

    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    val timerState by timerViewModel.state.collectAsStateWithLifecycle()
    val analyticsState by analyticsViewModel.state.collectAsStateWithLifecycle()

    val userSettings = settingsState.settings
    val themeColors = LocalFocusDeskColors.current
    val audioEngine = LocalAudioPlayer.current

    // Check Onboarding State
    if (!userSettings.isOnboardingCompleted) {
        OnboardingScreen(
            onComplete = {
                settingsViewModel.onIntent(SettingsIntent.CompleteOnboarding)
            }
        )
        return
    }

    val context = LocalContext.current
    val screenBackStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var isBottomBarVisible by remember { mutableStateOf(true) }
    var showNewSessionSheet by remember { mutableStateOf(false) }
    var newSessionInitialDuration by remember { mutableIntStateOf(25) }

    var showExitConfirmationDialog by remember { mutableStateOf(false) }
    var showEndSessionDialog by remember { mutableStateOf(false) }
    val isTimerScreenMinimized by isTimerScreenMinimizedFlow.collectAsStateWithLifecycle()
    fun setTimerScreenMinimized(minimized: Boolean) {
        isTimerScreenMinimizedFlow.value = minimized
    }

    var lastInteractionTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isBottomBarIdleCompressed by remember { mutableStateOf(false) }

    val isSessionActive = timerState.session.status == SessionStatus.Running ||
            timerState.session.status == SessionStatus.Paused
    val isSessionCompleted = timerState.session.status == SessionStatus.Completed

    // Synchronize Ambient Audio with State:
    // Audio plays automatically ONLY when focus session is Running,
    // or when the user is explicitly on the Sounds screen playing ambient sound.
    LaunchedEffect(
        timerState.session.soundscape,
        timerState.session.status,
        timerState.isPlayingAudio,
        timerState.soundVolume,
        currentScreen
    ) {
        val sound = timerState.session.soundscape
        val status = timerState.session.status

        val shouldPlay = when (status) {
            SessionStatus.Running -> timerState.isPlayingAudio && sound != Soundscape.None
            SessionStatus.Paused -> false // Paused during active session
            SessionStatus.Completed -> false // Session ended/completed
            SessionStatus.Idle -> currentScreen == Screen.Sounds && timerState.isPlayingAudio && sound != Soundscape.None
        }

        if (shouldPlay) {
            audioEngine.playSoundscape(sound, timerState.soundVolume)
        } else {
            audioEngine.stopSoundscape()
        }
    }

    // When leaving the Sounds screen without an active focus session, stop audio and close player
    LaunchedEffect(currentScreen, isSessionActive) {
        if (!isSessionActive && currentScreen != Screen.Sounds && timerState.isPlayingAudio) {
            audioEngine.stopSoundscape()
            timerViewModel.onIntent(TimerIntent.StopAudio)
        }
    }

    LaunchedEffect(isSessionActive) {
        if (isSessionActive) {
            DynamicIslandService.start(context)
        } else {
            setTimerScreenMinimized(false)
            DynamicIslandService.stop(context)
        }
    }

    // Request Notification Permission on Android 13+ (API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { _ -> }
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Trigger Notification Alert with Sound and Audio Chime when Focus Timer finishes
    LaunchedEffect(isSessionCompleted) {
        if (isSessionCompleted) {
            audioEngine.stopSoundscape()
            audioEngine.playOneShot(SoundEffect.Complete)
            DynamicIslandService.showCompletionNotification(context, timerState.session)
        }
    }

    fun navigateTo(screen: Screen) {
        if (currentScreen != screen) {
            screenBackStack.remove(screen)
            screenBackStack.add(screen)
            currentScreen = screen
            lastInteractionTimestamp = System.currentTimeMillis()
        }
    }

    // Android System Back Button & Gesture Navigation Handler
    BackHandler(enabled = true) {
        when {
            showExitConfirmationDialog -> {
                showExitConfirmationDialog = false
            }
            showEndSessionDialog -> {
                showEndSessionDialog = false
            }
            showNewSessionSheet -> {
                showNewSessionSheet = false
            }
            isSessionCompleted -> {
                timerViewModel.onIntent(TimerIntent.DismissCompletedSession)
            }
            isSessionActive && !isTimerScreenMinimized -> {
                // Minimize full-screen timer into the punch-hole dynamic island
                setTimerScreenMinimized(true)
            }
            screenBackStack.size > 1 -> {
                screenBackStack.removeAt(screenBackStack.lastIndex)
                currentScreen = screenBackStack.last()
                lastInteractionTimestamp = System.currentTimeMillis()
            }
            currentScreen != Screen.Home -> {
                currentScreen = Screen.Home
                screenBackStack.clear()
                screenBackStack.add(Screen.Home)
                lastInteractionTimestamp = System.currentTimeMillis()
            }
            isSessionActive -> {
                showEndSessionDialog = true
            }
            else -> {
                // Last page on the back stack: ask the user with popup
                showExitConfirmationDialog = true
            }
        }
    }

    // 8-second idle auto-compress for bottom navigation bar
    LaunchedEffect(lastInteractionTimestamp, currentScreen, isSessionActive, isSessionCompleted) {
        if (!isSessionActive && !isSessionCompleted) {
            isBottomBarIdleCompressed = false
            delay(8000L)
            isBottomBarIdleCompressed = true
        }
    }

    val shouldShowBottomBar = isBottomBarVisible && !isBottomBarIdleCompressed && !isSessionActive && !isSessionCompleted

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.background)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    lastInteractionTimestamp = System.currentTimeMillis()
                }
            }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = themeColors.background,
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                FocusDeskBottomNavBar(
                    selectedScreen = currentScreen,
                    onScreenSelected = { screen ->
                        navigateTo(screen)
                    },
                    isVisible = shouldShowBottomBar
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(bottom = if (shouldShowBottomBar) 70.dp else 0.dp)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        SwiftUiMotion.ScreenTransition
                    },
                    label = "tab_switch"
                ) { screen ->
                    when (screen) {
                        is Screen.Home -> {
                            HomeScreen(
                                focusedMinutesToday = analyticsState.stats.totalFocusMinutesToday,
                                completedSessionsToday = analyticsState.stats.recentSessions.size,
                                dayStreak = analyticsState.stats.currentDayStreak,
                                dailyGoalHours = userSettings.dailyGoalHours,
                                recentSessions = analyticsState.stats.recentSessions,
                                onQuickStart = { duration, label ->
                                    val sound = if (timerState.soundscape != Soundscape.None) {
                                        timerState.soundscape
                                    } else if (userSettings.soundscape != Soundscape.None) {
                                        userSettings.soundscape
                                    } else {
                                        Soundscape.Rain
                                    }
                                    timerViewModel.onIntent(
                                        TimerIntent.StartCustomSession(
                                            taskTitle = label,
                                            category = "Work",
                                            mood = "Calm",
                                            durationMinutes = duration,
                                            soundscape = sound
                                        )
                                    )
                                },
                                onStartFocusSessionClick = {
                                    newSessionInitialDuration = 25
                                    showNewSessionSheet = true
                                },
                                onScrollStateChange = { isVisible ->
                                    lastInteractionTimestamp = System.currentTimeMillis()
                                    isBottomBarVisible = isVisible
                                }
                            )
                        }

                        is Screen.Focus -> {
                            FocusScreen(
                                onQuickStart = { duration, label ->
                                    val sound = if (timerState.soundscape != Soundscape.None) {
                                        timerState.soundscape
                                    } else if (userSettings.soundscape != Soundscape.None) {
                                        userSettings.soundscape
                                    } else {
                                        Soundscape.Rain
                                    }
                                    timerViewModel.onIntent(
                                        TimerIntent.StartCustomSession(
                                            taskTitle = label,
                                            category = "Work",
                                            mood = "Calm",
                                            durationMinutes = duration,
                                            soundscape = sound
                                        )
                                    )
                                },
                                onOpenCustomSession = {
                                    newSessionInitialDuration = 25
                                    showNewSessionSheet = true
                                }
                            )
                        }

                        is Screen.Insights -> {
                            AnalyticsScreen(
                                stats = analyticsState.stats,
                                onScrollStateChange = { isVisible ->
                                    lastInteractionTimestamp = System.currentTimeMillis()
                                    isBottomBarVisible = isVisible
                                }
                            )
                        }

                        is Screen.Sounds -> {
                            SoundsScreen(
                                currentSound = timerState.soundscape,
                                isPlaying = timerState.isPlayingAudio,
                                soundVolume = timerState.soundVolume,
                                onPlaySound = { sound ->
                                    timerViewModel.onIntent(TimerIntent.PlaySound(sound))
                                },
                                onStopSound = {
                                    timerViewModel.onIntent(TimerIntent.StopAudio)
                                },
                                onVolumeChange = { vol ->
                                    timerViewModel.onIntent(TimerIntent.SetSoundVolume(vol))
                                },
                                onScrollStateChange = { isVisible ->
                                    lastInteractionTimestamp = System.currentTimeMillis()
                                    isBottomBarVisible = isVisible
                                }
                            )
                        }

                        is Screen.Settings -> {
                            SettingsScreen(
                                userSettings = userSettings,
                                onThemeChange = { newTheme ->
                                    settingsViewModel.onIntent(SettingsIntent.UpdateAppTheme(newTheme))
                                },
                                onDailyGoalChange = { newGoal ->
                                    settingsViewModel.onIntent(SettingsIntent.UpdateDailyGoal(newGoal))
                                },
                                onNotificationsChange = { enabled ->
                                    settingsViewModel.onIntent(SettingsIntent.UpdateNotifications(enabled))
                                },
                                onHapticsChange = { enabled ->
                                    settingsViewModel.onIntent(SettingsIntent.UpdateHaptics(enabled))
                                },
                                onResetAllData = {
                                    timerViewModel.onIntent(TimerIntent.StopAudio)
                                    timerViewModel.onIntent(TimerIntent.ConfirmReset)
                                    audioEngine.stopSoundscape()
                                    currentScreen = Screen.Home
                                    screenBackStack.clear()
                                    screenBackStack.add(Screen.Home)
                                    showNewSessionSheet = false
                                    showExitConfirmationDialog = false
                                    showEndSessionDialog = false
                                    settingsViewModel.onIntent(SettingsIntent.ResetAllData)
                                },
                                onScrollStateChange = { isVisible ->
                                    lastInteractionTimestamp = System.currentTimeMillis()
                                    isBottomBarVisible = isVisible
                                }
                            )
                        }
                    }
                }
            }
        }

        // New Session Modal Bottom Sheet
        AnimatedVisibility(
            visible = showNewSessionSheet,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showNewSessionSheet = false }
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* prevent closing when clicking sheet inside */ }
                    )
                ) {
                    NewSessionBottomSheet(
                        onDismiss = { showNewSessionSheet = false },
                        initialDurationMinutes = newSessionInitialDuration,
                        onStartFocusing = { title, category, mood, durationMinutes, sound ->
                            showNewSessionSheet = false
                            timerViewModel.onIntent(
                                TimerIntent.StartCustomSession(
                                    taskTitle = title,
                                    category = category,
                                    mood = mood,
                                    durationMinutes = durationMinutes,
                                    soundscape = sound
                                )
                            )
                        }
                    )
                }
            }
        }

        // Active Timer Full Screen Overlay
        AnimatedVisibility(
            visible = isSessionActive && !isTimerScreenMinimized,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(350)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeOut()
        ) {
            TimerScreen(
                session = timerState.session,
                isPlayingAudio = timerState.isPlayingAudio,
                soundVolume = timerState.soundVolume,
                onTogglePauseResume = { timerViewModel.onIntent(TimerIntent.ToggleTimer) },
                onAddFiveMinutes = { timerViewModel.onIntent(TimerIntent.AddFiveMinutes) },
                onEndSession = {
                    audioEngine.stopSoundscape()
                    timerViewModel.onIntent(TimerIntent.EndSessionEarly)
                },
                onToggleAudioMute = { timerViewModel.onIntent(TimerIntent.ToggleSoundscapePlayback) },
                onStopAudio = {
                    audioEngine.stopSoundscape()
                    timerViewModel.onIntent(TimerIntent.StopAudio)
                },
                onMinimize = { setTimerScreenMinimized(true) }
            )
        }

        // In-App Active Session Control Card when full-screen timer is minimized
        AnimatedVisibility(
            visible = isSessionActive && isTimerScreenMinimized && !isSessionCompleted,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 12.dp, start = 14.dp, end = 14.dp)
        ) {
            ActiveFocusSessionCard(
                session = timerState.session,
                isPlayingAudio = timerState.isPlayingAudio,
                onTogglePauseResume = { timerViewModel.onIntent(TimerIntent.ToggleTimer) },
                onToggleAudioMute = { timerViewModel.onIntent(TimerIntent.ToggleSoundscapePlayback) },
                onMaximize = { setTimerScreenMinimized(false) },
                onEndSession = {
                    audioEngine.stopSoundscape()
                    timerViewModel.onIntent(TimerIntent.EndSessionEarly)
                }
            )
        }

        // Session Completed Celebration Screen
        AnimatedVisibility(
            visible = isSessionCompleted,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            val elapsedMinutes = ((timerState.session.targetDurationSeconds - timerState.session.remainingSeconds) / 60L)
                .toInt()
                .coerceAtLeast(1)

            SessionCompleteScreen(
                focusedMinutes = elapsedMinutes,
                xpEarned = 6,
                onDone = {
                    audioEngine.stopSoundscape()
                    timerViewModel.onIntent(TimerIntent.DismissCompletedSession)
                }
            )
        }

        // Exit Confirmation Dialog (when back pressed on last page)
        if (showExitConfirmationDialog) {
            ExitConfirmationDialog(
                onDismissRequest = { showExitConfirmationDialog = false },
                onConfirmExit = {
                    showExitConfirmationDialog = false
                    (context as? Activity)?.finish()
                }
            )
        }

        // End Session Confirmation Dialog (when back pressed during active timer)
        if (showEndSessionDialog) {
            EndSessionConfirmationDialog(
                onDismissRequest = { showEndSessionDialog = false },
                onConfirmEnd = {
                    showEndSessionDialog = false
                    audioEngine.stopSoundscape()
                    timerViewModel.onIntent(TimerIntent.EndSessionEarly)
                }
            )
        }
    }
}
