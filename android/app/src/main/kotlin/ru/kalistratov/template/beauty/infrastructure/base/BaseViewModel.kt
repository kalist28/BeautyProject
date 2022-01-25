package ru.kalistratov.template.beauty.infrastructure.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.kalistratov.template.beauty.infrastructure.coroutines.CompositeJob
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow

abstract class BaseViewModel<I : BaseIntent, A : BaseAction, S : BaseState> : ViewModel() {

    private val uiComposite = CompositeJob()
    protected val workComposite = CompositeJob()

    protected val shareStateFlow: MutableSharedFlow<S> = mutableSharedFlow()
    protected val intentFlow: MutableSharedFlow<I> = mutableSharedFlow()

    override fun onCleared() {
        workComposite.cancel()
        uiComposite.cancel()
        super.onCleared()
    }

    abstract fun reduce(state: S, action: A): S

    open fun stateUpdates(): Flow<S> = shareStateFlow

    fun processIntent(intents: Flow<I>) {
        uiComposite.cancel()
        intents
            .onEach(intentFlow::emit)
            .launchIn(viewModelScope)
            .addTo(uiComposite)
    }
}
