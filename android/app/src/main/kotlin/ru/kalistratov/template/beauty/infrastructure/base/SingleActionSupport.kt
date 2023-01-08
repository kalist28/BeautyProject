package ru.kalistratov.template.beauty.infrastructure.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow

interface SingleActionSupport<SA : SingleAction> {
    suspend fun setSingleActionProcessor(
        collector: (SA) -> Unit,
        scope: CoroutineScope
    )

    suspend fun post(notification: SA)
}

class SingleActionSupportBaseImpl<SA : SingleAction> : SingleActionSupport<SA> {

    private var lastJob: Job? = null
    private val updates = mutableSharedFlow<SA>()

    override suspend fun post(notification: SA) {
        updates.emit(notification)
    }

    override suspend fun setSingleActionProcessor(
        collector: (SA) -> Unit,
        scope: CoroutineScope
    ) {
        lastJob?.cancel()
        lastJob = updates
            .onEach { collector.invoke(it) }
            .launchIn(scope)
    }
}