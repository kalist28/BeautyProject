package ru.kalistratov.template.beauty.common

sealed class NetworkResult<out T> {
    data class Success<out T>(val value: T) : NetworkResult<T>()
    data class GenericError<out T>(val value: NetworkRequestException) : NetworkResult<T>()
}
