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

fun <T, R> Flow<T>.alternative(
    transform: suspend (value: Boolean) -> R,
    startWith: Boolean = true
) = flatMapConcat { createAlternative(startWith) }
    .map { transform.invoke(it) }

fun createAlternative(startWith: Boolean = true) = flowOf(startWith, !startWith)
fun createAlternativeList(startWith: Boolean = true) = listOf(startWith, !startWith)

fun <T> Flow<T>.clickDebounce(millis: Long = 100) = this.debounce(millis)

fun <T> Flow<T>.textDebounce() = this.debounce(200)

fun <T> Flow<T>.share(scope: CoroutineScope, replay: Int = 0) =
    this.shareIn(scope, Eagerly, replay)

fun <T> notNullFlow(value: T?) =
    if (value != null) flowOf(value)
    else emptyFlow()
