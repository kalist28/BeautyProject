package ru.kalistratov.template.beauty.infrastructure.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow

interface ViewModelNotificationSupport {
    fun notifications(): Flow<ViewNotification>
    suspend fun post(notification: ViewNotification)
}

class ViewModelNotificationSupportBaseImpl : ViewModelNotificationSupport {

    private val loadingUpdates = mutableSharedFlow<ViewNotification>()

    override fun notifications() = loadingUpdates.asSharedFlow()

    override suspend fun post(notification: ViewNotification) {
        loadingUpdates.emit(notification)
    }
}