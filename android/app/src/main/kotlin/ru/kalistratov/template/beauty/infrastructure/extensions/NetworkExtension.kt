package ru.kalistratov.template.beauty.infrastructure.extensions

import ru.kalistratov.template.beauty.common.NetworkRequestException
import ru.kalistratov.template.beauty.common.NetworkResult

fun <T, R> NetworkResult<T>.process(
    success: (T.() -> R),
    error: ((NetworkRequestException) -> R),
) = when (this) {
    is NetworkResult.Success -> success.invoke(this.value)
    is NetworkResult.GenericError -> error.invoke(this.error)
}

fun <T, R> NetworkResult<T>.processWithNull(
    success: ((T) -> R)? = null,
    error: ((NetworkRequestException) -> R)? = null,
) = when (this) {
    is NetworkResult.Success -> success?.invoke(this.value)
    is NetworkResult.GenericError -> error?.invoke(this.error)
}
