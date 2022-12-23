package ru.kalistratov.template.beauty.infrastructure.base

import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow

abstract class BaseViewModelWithLoadingSupport
<I : BaseIntent, A : BaseAction, S : BaseState> : BaseViewModel<I, A, S>() {

    private val loadingUpdates = mutableSharedFlow<Boolean>()

    fun loadingUpdates() = loadingUpdates.asSharedFlow()

    protected suspend fun showLoading() = loadingUpdates.emit(true)
    protected suspend fun hideLoading() = loadingUpdates.emit(false)
}