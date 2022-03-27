package ru.kalistratov.template.beauty.domain.extension

import ru.kalistratov.template.beauty.common.NetworkResult

inline fun <T, R> NetworkResult<T>.doIfSuccess(block: (T) -> R): R? =
    if (this is NetworkResult.Success) block.invoke(this.value) else null

