package com.focusdesk.domain.model

data class DailyFocusMetric(
    val dayLabel: String,
    val dayOfWeekIndex: Int,
    val focusMinutes: Int,
    val completedSessionsCount: Int
)

data class ProductivityStats(
    val totalFocusMinutesToday: Int = 0,
    val totalFocusMinutesWeek: Int = 0,
    val currentDayStreak: Int = 0,
    val completedTasksToday: Int = 0,
    val weeklyDistribution: List<DailyFocusMetric> = emptyList(),
    val recentSessions: List<FocusSession> = emptyList()
) {
    val formattedTotalTimeToday: String
        get() {
            val hours = totalFocusMinutesToday / 60
            val minutes = totalFocusMinutesToday % 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }

    val formattedTotalTimeWeek: String
        get() {
            val hours = totalFocusMinutesWeek / 60
            val minutes = totalFocusMinutesWeek % 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }
}
