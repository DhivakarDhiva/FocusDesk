package com.focusdesk.core.mvi

/**
 * Marker interface for one-shot UI side effects (e.g. Navigation, SnackBar, Haptics).
 * Handled via Channel / SharedFlow to guarantee single-time consumption.
 */
interface MviEffect
