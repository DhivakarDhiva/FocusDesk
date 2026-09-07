package com.focusdesk.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.media.MediaMetadata
import android.media.RingtoneManager
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import com.focusdesk.app.MainActivity
import com.focusdesk.core.platform.AmbientAudioEngine
import com.focusdesk.core.platform.SoundEffect
import com.focusdesk.domain.model.FocusSession
import com.focusdesk.domain.model.SessionStatus
import com.focusdesk.domain.model.Soundscape
import com.focusdesk.domain.repository.FocusRepository
import com.focusdesk.domain.repository.SettingsRepository
import com.focusdesk.domain.usecase.TimerEngineUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

/**
 * Foreground Service hosting a native Android [MediaSession] and [Notification.MediaStyle].
 *
 * This allows modern Android operating systems (e.g. OxygenOS/ColorOS Aqua Dynamics,
 * Xiaomi HyperOS Island, Samsung One UI, and Android 14/15 Live Status Bar Chips)
 * to automatically present the built-in system Dynamic Island around the camera punch-hole,
 * exactly like Spotify and phone call applications.
 */
class DynamicIslandService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var mediaSession: MediaSession? = null

    private lateinit var timerEngine: TimerEngineUseCase
    private lateinit var focusRepository: FocusRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var audioEngine: AmbientAudioEngine

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        val koin = GlobalContext.get()
        timerEngine = koin.get()
        focusRepository = koin.get()
        settingsRepository = koin.get()
        audioEngine = koin.get()

        createNotificationChannel()
        setupMediaSession()

        val initialNotification = buildNotification(initialSession = null)
        startForeground(NOTIFICATION_ID, initialNotification)

        // Observe session updates and update native MediaSession + MediaStyle notification
        serviceScope.launch {
            focusRepository.observeCurrentSession().collectLatest { session ->
                if (session.status == SessionStatus.Completed || session.status == SessionStatus.Idle) {
                    if (session.status == SessionStatus.Completed) {
                        showCompletionNotification(this@DynamicIslandService, session)
                    }
                    cleanupMediaSession()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                } else {
                    updateMediaSessionState(session)
                    updateNotification(session)
                }
            }
        }
    }

    private fun setupMediaSession() {
        try {
            mediaSession = MediaSession(this, "FocusDeskMediaSession").apply {
                setCallback(object : MediaSession.Callback() {
                    override fun onPlay() {
                        serviceScope.launch {
                            timerEngine.startTimer()
                        }
                    }

                    override fun onPause() {
                        serviceScope.launch {
                            timerEngine.pauseTimer()
                        }
                    }

                    override fun onStop() {
                        endSession()
                    }

                    override fun onCustomAction(action: String, extras: android.os.Bundle?) {
                        when (action) {
                            "TOGGLE_MUTE" -> toggleMute()
                            "STOP_SESSION" -> endSession()
                        }
                    }
                })
                isActive = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleMute() {
        serviceScope.launch {
            val newSoundscape = timerEngine.toggleAudioMute()
            val volume = settingsRepository.getSettings().soundscapeVolume
            if (newSoundscape != Soundscape.None) {
                audioEngine.playSoundscape(newSoundscape, volume)
            } else {
                audioEngine.stopSoundscape()
            }
        }
    }

    private fun endSession() {
        serviceScope.launch {
            timerEngine.endSessionEarly()
            audioEngine.stopSoundscape()
            cleanupMediaSession()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun createTimerBadgeBitmap(session: FocusSession, isRunning: Boolean): Bitmap {
        val size = 256
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dark rounded background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#12141C")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(0f, 0f, size.toFloat(), size.toFloat(), 56f, 56f, bgPaint)

        // Circular track
        val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#252836")
            style = Paint.Style.STROKE
            strokeWidth = 18f
        }
        val center = size / 2f
        val radius = center - 32f
        canvas.drawCircle(center, center, radius, trackPaint)

        // Progress arc
        val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (isRunning) android.graphics.Color.parseColor("#4DEEAA") else android.graphics.Color.parseColor("#E5A43B")
            style = Paint.Style.STROKE
            strokeWidth = 18f
            strokeCap = Paint.Cap.ROUND
        }
        val rect = RectF(center - radius, center - radius, center + radius, center + radius)
        canvas.drawArc(rect, -90f, (session.progress * 360f).coerceIn(4f, 360f), false, progressPaint)

        // Center Time Text: MM:SS
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 44f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val yPos = center - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(session.formattedRemainingTime, center, yPos, textPaint)

        return bitmap
    }

    private fun updateMediaSessionState(session: FocusSession) {
        val sessionObj = mediaSession ?: return
        try {
            val isRunning = session.status == SessionStatus.Running
            val isSoundActive = session.soundscape != Soundscape.None
            val state = if (isRunning) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED
            val elapsedMillis = (session.targetDurationSeconds - session.remainingSeconds).coerceAtLeast(0) * 1000L
            val speed = if (isRunning) 1.0f else 0.0f

            val toggleMuteAction = PlaybackState.CustomAction.Builder(
                "TOGGLE_MUTE",
                if (isSoundActive) "Mute" else "Unmute",
                if (isSoundActive) android.R.drawable.ic_lock_silent_mode else android.R.drawable.ic_lock_silent_mode_off
            ).build()

            val stopAction = PlaybackState.CustomAction.Builder(
                "STOP_SESSION",
                "Stop",
                android.R.drawable.ic_menu_close_clear_cancel
            ).build()

            val playbackState = PlaybackState.Builder()
                .setActions(
                    PlaybackState.ACTION_PLAY or
                            PlaybackState.ACTION_PAUSE or
                            PlaybackState.ACTION_PLAY_PAUSE or
                            PlaybackState.ACTION_STOP
                )
                .addCustomAction(toggleMuteAction)
                .addCustomAction(stopAction)
                .setState(state, elapsedMillis, speed, SystemClock.elapsedRealtime())
                .build()

            sessionObj.setPlaybackState(playbackState)

            val timerBitmap = createTimerBadgeBitmap(session, isRunning)

            val metadata = MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, session.taskTitle.ifBlank { "Focus Session" })
                .putString(
                    MediaMetadata.METADATA_KEY_ARTIST,
                    "${session.formattedRemainingTime} remaining • ${if (isSoundActive) "♫ ${session.soundscape.title}" else "Muted"}"
                )
                .putString(MediaMetadata.METADATA_KEY_ALBUM, "FocusDesk Timer")
                .putLong(MediaMetadata.METADATA_KEY_DURATION, session.targetDurationSeconds * 1000L)
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, timerBitmap)
                .putBitmap(MediaMetadata.METADATA_KEY_ART, timerBitmap)
                .putBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON, timerBitmap)
                .build()

            sessionObj.setMetadata(metadata)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cleanupMediaSession() {
        try {
            mediaSession?.isActive = false
            mediaSession?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaSession = null
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_SERVICE) {
            cleanupMediaSession()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        if (action == ACTION_APP_FOREGROUND) {
            isAppInForeground = true
        } else if (action == ACTION_APP_BACKGROUND) {
            isAppInForeground = false
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Focus Session Dynamic Island",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Native Android Media Dynamic Island integration for active focus sessions"
            setShowBadge(false)
            enableVibration(false)
            setSound(null, null)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    private fun buildNotification(initialSession: FocusSession?): Notification {
        val session = initialSession ?: FocusSession(
            id = "default",
            taskTitle = "Focus Session",
            remainingSeconds = 25 * 60L,
            targetDurationSeconds = 25 * 60L
        )

        val isRunning = session.status == SessionStatus.Running
        val titleText = if (session.taskTitle.isNotBlank()) {
            "Focus: ${session.taskTitle}"
        } else {
            "Focus Session (${session.mood})"
        }

        val contentText = "${session.formattedRemainingTime}  •  ${(session.progress * 100).toInt()}%  •  ${if (session.soundscape != Soundscape.None) session.soundscape.title else "Muted"}"

        // Maximize Intent: tapping notification body brings MainActivity to front
        val maximizeIntent = Intent(this, MainActivity::class.java).apply {
            action = MainActivity.ACTION_MAXIMIZE_TIMER
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val maximizePendingIntent = PendingIntent.getActivity(
            this,
            101,
            maximizeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Pause/Resume Action
        val togglePauseIntent = Intent(this, DynamicIslandActionReceiver::class.java).apply {
            action = DynamicIslandActionReceiver.ACTION_TOGGLE_PAUSE
        }
        val togglePausePendingIntent = PendingIntent.getBroadcast(
            this,
            102,
            togglePauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Mute/Unmute Action
        val toggleAudioIntent = Intent(this, DynamicIslandActionReceiver::class.java).apply {
            action = DynamicIslandActionReceiver.ACTION_TOGGLE_MUTE
        }
        val toggleAudioPendingIntent = PendingIntent.getBroadcast(
            this,
            103,
            toggleAudioIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Stop Action
        val stopIntent = Intent(this, DynamicIslandActionReceiver::class.java).apply {
            action = DynamicIslandActionReceiver.ACTION_STOP_SESSION
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            104,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Native Android MediaStyle for the system status-bar Dynamic Island
        val mediaStyle = Notification.MediaStyle().apply {
            mediaSession?.sessionToken?.let { token ->
                setMediaSession(token)
            }
            setShowActionsInCompactView(0, 1, 2)
        }

        val timerBitmap = createTimerBadgeBitmap(session, isRunning)

        val builder = Notification.Builder(this, CHANNEL_ID)
            .setStyle(mediaStyle)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setLargeIcon(timerBitmap)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setSubText(if (isRunning) "FOCUSING" else "PAUSED")
            .setContentIntent(maximizePendingIntent)
            .setOngoing(isRunning)
            .setOnlyAlertOnce(true)
            .setCategory(Notification.CATEGORY_TRANSPORT)
            .setVisibility(Notification.VISIBILITY_PUBLIC)

        // Action 0: Pause or Play
        val playPauseIcon = if (isRunning) {
            Icon.createWithResource(this, android.R.drawable.ic_media_pause)
        } else {
            Icon.createWithResource(this, android.R.drawable.ic_media_play)
        }
        val playPauseAction = Notification.Action.Builder(
            playPauseIcon,
            if (isRunning) "Pause" else "Resume",
            togglePausePendingIntent
        ).build()
        builder.addAction(playPauseAction)

        // Action 1: Sound Toggle (Mute / Sound)
        val isSoundActive = session.soundscape != Soundscape.None
        val soundIcon = Icon.createWithResource(
            this,
            if (isSoundActive) android.R.drawable.ic_lock_silent_mode else android.R.drawable.ic_lock_silent_mode_off
        )
        val soundAction = Notification.Action.Builder(
            soundIcon,
            if (isSoundActive) "Mute" else "Sound",
            toggleAudioPendingIntent
        ).build()
        builder.addAction(soundAction)

        // Action 2: Stop Session
        val stopIcon = Icon.createWithResource(this, android.R.drawable.ic_menu_close_clear_cancel)
        val stopAction = Notification.Action.Builder(
            stopIcon,
            "Stop",
            stopPendingIntent
        ).build()
        builder.addAction(stopAction)

        return builder.build()
    }

    private fun updateNotification(session: FocusSession) {
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, buildNotification(session))
    }

    override fun onDestroy() {
        serviceScope.cancel()
        cleanupMediaSession()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "focus_session_dynamic_island"
        const val COMPLETION_CHANNEL_ID = "focus_session_completion_alerts"
        const val NOTIFICATION_ID = 8001
        const val COMPLETION_NOTIFICATION_ID = 8002

        const val ACTION_STOP_SERVICE = "com.focusdesk.app.action.STOP_SERVICE"
        const val ACTION_APP_FOREGROUND = "com.focusdesk.app.action.APP_FOREGROUND"
        const val ACTION_APP_BACKGROUND = "com.focusdesk.app.action.APP_BACKGROUND"

        @Volatile
        var isAppInForeground: Boolean = true

        @Volatile
        private var lastNotifiedSessionId: String? = null

        @Volatile
        private var lastNotifiedTimestamp: Long = 0L

        fun showCompletionNotification(context: Context, session: FocusSession) {
            val now = System.currentTimeMillis()
            if (session.id == lastNotifiedSessionId && (now - lastNotifiedTimestamp) < 3000L) {
                return
            }
            lastNotifiedSessionId = session.id
            lastNotifiedTimestamp = now

            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            createCompletionNotificationChannel(manager)

            val openIntent = Intent(context, MainActivity::class.java).apply {
                action = MainActivity.ACTION_MAXIMIZE_TIMER
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openPendingIntent = PendingIntent.getActivity(
                context,
                199,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val elapsedMinutes = ((session.targetDurationSeconds - session.remainingSeconds).coerceAtLeast(60L) / 60L).toInt()
            val taskName = session.taskTitle.ifBlank { "Focus Session" }
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notification = Notification.Builder(context, COMPLETION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Focus Session Complete! 🎉")
                .setContentText("Great job! Completed ${elapsedMinutes}m of $taskName. Take a break!")
                .setContentIntent(openPendingIntent)
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_ALARM)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .build()

            manager.notify(COMPLETION_NOTIFICATION_ID, notification)

            // Play native notification chime
            try {
                val ringtone = RingtoneManager.getRingtone(context.applicationContext, soundUri)
                ringtone?.play()
            } catch (_: Exception) {}
        }

        private fun createCompletionNotificationChannel(manager: NotificationManager) {
            val existing = manager.getNotificationChannel(COMPLETION_CHANNEL_ID)
            if (existing == null) {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()

                val channel = NotificationChannel(
                    COMPLETION_CHANNEL_ID,
                    "Focus Session Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts and plays a sound when your focus timer finishes"
                    enableLights(true)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 350, 200, 350)
                    setSound(soundUri, audioAttributes)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                manager.createNotificationChannel(channel)
            }
        }

        fun start(context: Context) {
            val intent = Intent(context, DynamicIslandService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, DynamicIslandService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.startService(intent)
        }

        fun notifyForeground(context: Context) {
            isAppInForeground = true
            val intent = Intent(context, DynamicIslandService::class.java).apply {
                action = ACTION_APP_FOREGROUND
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {}
        }

        fun notifyBackground(context: Context) {
            isAppInForeground = false
            val intent = Intent(context, DynamicIslandService::class.java).apply {
                action = ACTION_APP_BACKGROUND
            }
            try {
                context.startForegroundService(intent)
            } catch (_: Exception) {}
        }
    }
}
