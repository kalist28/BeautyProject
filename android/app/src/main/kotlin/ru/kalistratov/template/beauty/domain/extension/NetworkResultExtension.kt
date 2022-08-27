package ru.kalistratov.template.beauty.domain.extension

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

inline fun <T, R> NetworkResult<T>.doIfSuccess(block: (T) -> R): R? =
    if (this is NetworkResult.Success) block.invoke(this.value) else null

fun <T> NetworkResult<T>.logIfError(): NetworkResult<T> =
    this.also { if (this is NetworkResult.GenericError) loge("Network error: $this") }

