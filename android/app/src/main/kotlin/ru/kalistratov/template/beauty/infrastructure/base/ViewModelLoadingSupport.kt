package ru.kalistratov.template.beauty.infrastructure.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow

interface ViewModelLoadingSupport {
    fun loadingUpdates(): Flow<Boolean>

    suspend fun setLoadingProcessor(
        scope: CoroutineScope,
        collector: (Boolean) -> Unit
    )

    suspend fun isLoadingShown(): Boolean

    suspend fun showLoading()
    suspend fun hideLoading()
}

class ViewModelLoadingSupportBaseImpl() : ViewModelLoadingSupport {

    private var requestCount = 0
    private var lastCollectorJob: Job? = null
    private val loadingUpdates = mutableSharedFlow<Boolean>()

    override fun loadingUpdates() = loadingUpdates.asSharedFlow()

    override suspend fun setLoadingProcessor(
        scope: CoroutineScope,
        collector: (Boolean) -> Unit
    ) {
        lastCollectorJob?.cancel()
        lastCollectorJob = loadingUpdates
            .onEach { collector.invoke(it) }
            .launchIn(scope)
    }

    override suspend fun isLoadingShown() = requestCount > 0

    override suspend fun showLoading() = loadingUpdates.emit(true).also { requestCount++ }
    override suspend fun hideLoading() {
        if (requestCount > 0) requestCount--
        if (requestCount < 1) loadingUpdates.emit(false)
    }
}