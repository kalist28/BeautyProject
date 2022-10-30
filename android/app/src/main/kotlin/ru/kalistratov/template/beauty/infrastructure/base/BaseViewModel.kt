package ru.kalistratov.template.beauty.infrastructure.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.CompositeJob
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow

abstract class BaseViewModel<I : BaseIntent, A : BaseAction, S : BaseState> : ViewModel() {

    private val uiComposite = CompositeJob()
    protected val workComposite = CompositeJob()

    private lateinit var _stateFlow: MutableStateFlow<S>
    protected val stateFlow: MutableSharedFlow<S> = mutableSharedFlow(replay = 1)
    protected val intentFlow: MutableSharedFlow<I> = mutableSharedFlow()

    override fun onCleared() {
        workComposite.cancel()
        uiComposite.cancel()
        super.onCleared()
    }

    abstract fun reduce(state: S, action: A): S

    open fun stateUpdates(): Flow<S> = stateFlow

    fun processIntent(intents: Flow<I>) {
        uiComposite.cancel()
        intents
            .onEach(intentFlow::emit)
            .launchIn(viewModelScope)
            .addTo(uiComposite)
    }

    protected fun getLastState() = _stateFlow.value

    protected fun <T> Flow<T>.launchHere() = this
        .launchIn(viewModelScope)
        .addTo(workComposite)

    protected suspend fun Flow<A>.collectState(initialState: S) {
        _stateFlow = MutableStateFlow(initialState)
        scan(initialState, ::reduce)
            .onEach {
                _stateFlow.value = it
                stateFlow.emit(it)
            }
            .launchIn(viewModelScope)
            .addTo(workComposite)
    }

}
