package com.focusdesk.core.platform

enum class HapticFeedbackType {
    Light,
    Medium,
    Heavy,
    Success,
    Warning,
    Error,
    Selection
}

expect class HapticEngine {
    fun perform(type: HapticFeedbackType)
}
