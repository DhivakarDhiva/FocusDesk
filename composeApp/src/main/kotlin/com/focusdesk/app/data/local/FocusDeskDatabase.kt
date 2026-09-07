package com.focusdesk.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.focusdesk.app.data.local.dao.FocusSessionDao
import com.focusdesk.app.data.local.dao.TaskDao
import com.focusdesk.app.data.local.dao.UserSettingsDao
import com.focusdesk.app.data.local.entity.FocusSessionEntity
import com.focusdesk.app.data.local.entity.TaskEntity
import com.focusdesk.app.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FocusSessionEntity::class,
        UserSettingsEntity::class,
        TaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FocusDeskDatabase : RoomDatabase() {

    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun taskDao(): TaskDao

    companion object {
        private const val DATABASE_NAME = "focusdesk.db"

        @Volatile
        private var INSTANCE: FocusDeskDatabase? = null

        fun getInstance(context: Context): FocusDeskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocusDeskDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Prepopulate initial settings and starter tasks on database creation
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateDatabase(database)
                }
            }
        }

        private suspend fun prepopulateDatabase(database: FocusDeskDatabase) {
            val userSettingsDao = database.userSettingsDao()
            if (userSettingsDao.getSettings() == null) {
                userSettingsDao.upsertSettings(
                    UserSettingsEntity(
                        id = 1,
                        workDurationMinutes = 25,
                        shortBreakDurationMinutes = 5,
                        longBreakDurationMinutes = 15,
                        sessionsBeforeLongBreak = 4,
                        autoStartBreaks = false,
                        autoStartWork = false,
                        soundscape = "None",
                        soundscapeVolume = 0.65f,
                        tickSoundsEnabled = true,
                        hapticsEnabled = true,
                        notificationsEnabled = true,
                        appTheme = "DefaultFocus",
                        dailyGoalHours = 4,
                        isOnboardingCompleted = false,
                        xp = 0,
                        level = 1,
                        dayStreak = 0,
                        bestStreak = 0,
                        lastActiveEpochDay = 0L
                    )
                )
            }

            val taskDao = database.taskDao()
            if (taskDao.countTasks() == 0) {
                taskDao.insertAll(
                    listOf(
                        TaskEntity(
                            id = "t_1",
                            title = "Architect KMP MVI Store",
                            description = "Implement unidirectional data flow with pure reducer and one-shot effects channel.",
                            isCompleted = false,
                            priority = "Urgent",
                            category = "Coding",
                            estimatedPomodoros = 3,
                            completedPomodoros = 2,
                            createdAtTimestamp = System.currentTimeMillis() - 3600000L
                        ),
                        TaskEntity(
                            id = "t_2",
                            title = "Tune Compose Spring Physics",
                            description = "Ensure dampingRatio and stiffness 1:1 match SwiftUI .bouncy and .snappy curves.",
                            isCompleted = false,
                            priority = "High",
                            category = "Design",
                            estimatedPomodoros = 2,
                            completedPomodoros = 1,
                            createdAtTimestamp = System.currentTimeMillis() - 7200000L
                        ),
                        TaskEntity(
                            id = "t_3",
                            title = "Implement Ambient Audio Engine",
                            description = "Loop rainfall and forest soundscapes with dynamic volume control.",
                            isCompleted = false,
                            priority = "Medium",
                            category = "DeepWork",
                            estimatedPomodoros = 2,
                            completedPomodoros = 0,
                            createdAtTimestamp = System.currentTimeMillis() - 10800000L
                        ),
                        TaskEntity(
                            id = "t_4",
                            title = "Write Architecture Spec",
                            description = "Document component mapping and navigation graph.",
                            isCompleted = true,
                            priority = "Low",
                            category = "Writing",
                            estimatedPomodoros = 1,
                            completedPomodoros = 1,
                            createdAtTimestamp = System.currentTimeMillis() - 14400000L,
                            completedAtTimestamp = System.currentTimeMillis() - 1200000L
                        )
                    )
                )
            }
        }
    }
}
