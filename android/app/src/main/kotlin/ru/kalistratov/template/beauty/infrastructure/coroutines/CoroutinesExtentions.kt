package ru.kalistratov.template.beauty.infrastructure.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly

fun <T> mutableSharedFlow(replay: Int = 0) = MutableSharedFlow<T>(
    replay = replay,
    extraBufferCapacity = 5,
    onBufferOverflow = BufferOverflow.DROP_LATEST
)

fun <T> Flow<T>.textDebounce() = this.debounce(200)

fun <T> Flow<T>.share(scope: CoroutineScope, replay: Int = 0) =
    this.shareIn(scope, Eagerly, replay)

fun <T> notNullFlow(value: T?) =
    if (value != null) flowOf(value)
    else emptyFlow()
