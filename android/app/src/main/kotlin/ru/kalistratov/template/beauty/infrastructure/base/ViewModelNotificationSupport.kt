package ru.kalistratov.template.beauty.infrastructure.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow

interface ViewModelNotificationSupport {
    fun notifications(): Flow<ViewNotification>

    suspend fun setNotificationProcessor(
        collector: (ViewNotification) -> Unit,
        scope: CoroutineScope
    )

    suspend fun post(notification: ViewNotification)
}

class ViewModelNotificationSupportBaseImpl : ViewModelNotificationSupport {

    private var lastCollectorJob: Job? = null
    private val loadingUpdates = mutableSharedFlow<ViewNotification>()

    override fun notifications() = loadingUpdates.asSharedFlow()

    override suspend fun setNotificationProcessor(
        collector: (ViewNotification) -> Unit,
        scope: CoroutineScope
    ) {
        lastCollectorJob?.cancel()
        lastCollectorJob = loadingUpdates
            .onEach { collector.invoke(it) }
            .launchIn(scope)
    }

    override suspend fun post(notification: ViewNotification) {
        loadingUpdates.emit(notification)
    }
}