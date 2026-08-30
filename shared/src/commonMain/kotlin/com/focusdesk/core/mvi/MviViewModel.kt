package com.focusdesk.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Production-grade base ViewModel enforcing strict Unidirectional Data Flow (MVI).
 * 
 * View sends [MviIntent] -> Store calls [handleIntent] -> Mutates State via pure [setState] -> View re-renders.
 * One-shot side-effects (navigation, haptics, alerts) are dispatched via [setEffect] using a buffered Channel.
 */
abstract class MviViewModel<I : MviIntent, S : MviState, E : MviEffect>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    protected val currentState: S
        get() = _state.value

    fun onIntent(intent: I) {
        handleIntent(intent)
    }

    protected abstract fun handleIntent(intent: I)

    /**
     * Pure reducer state transition.
     */
    protected fun setState(reducer: S.() -> S) {
        _state.update(reducer)
    }

    /**
     * Dispatches a one-shot side effect to be consumed once by the UI.
     */
    protected fun setEffect(builder: () -> E) {
        val effect = builder()
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}
