package ru.kalistratov.template.beauty.common

import kotlinx.serialization.Serializable

@Serializable
sealed class NetworkResult<out T> {
    @Serializable
    data class Success<out T>(val value: T) : NetworkResult<T>()
    data class GenericError(val error: NetworkRequestException) : NetworkResult<Nothing>()
}
