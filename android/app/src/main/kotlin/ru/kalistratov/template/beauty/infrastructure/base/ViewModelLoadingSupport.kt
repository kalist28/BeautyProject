package ru.kalistratov.template.beauty.infrastructure.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow

interface ViewModelLoadingSupport {
    fun loadingUpdates(): Flow<Boolean>

    suspend fun showLoading()
    suspend fun hideLoading()
}

class ViewModelLoadingSupportBaseImpl : ViewModelLoadingSupport {
    private val loadingUpdates = mutableSharedFlow<Boolean>()

    override fun loadingUpdates() = loadingUpdates.asSharedFlow()

    override suspend fun showLoading() = loadingUpdates.emit(true)
    override suspend fun hideLoading() = loadingUpdates.emit(false)
}