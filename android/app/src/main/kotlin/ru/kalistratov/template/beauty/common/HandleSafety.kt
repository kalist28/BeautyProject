package ru.kalistratov.template.beauty.common

import io.ktor.client.features.*
import io.ktor.network.sockets.SocketTimeoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.SerializationException
import ru.kalistratov.template.beauty.domain.entity.Data

typealias NetworkException<E> = suspend (Throwable) -> E

val DEFAULT_NETWORK_EXCEPTION: NetworkException<Nothing> = { throw it }

suspend inline fun <T> safety(
    noinline block: suspend CoroutineScope.() -> T,
    noinline onException: NetworkException<T> = DEFAULT_NETWORK_EXCEPTION
): T {
    return try {
        supervisorScope(block)
    } catch (e: Exception) {
        onException(e)
    }
}

suspend inline fun <T> handlingNetworkSafetyWithoutData(
    noinline block: suspend CoroutineScope.() -> T,
) = safety(
    { NetworkResult.Success(supervisorScope(block)) },
    { throwable ->
        when (throwable) {
            is SocketTimeoutException -> NetworkResult.GenericError(
                NetworkRequestException.Timeout(throwable)
            )
            is SerializationException -> NetworkResult.GenericError(
                NetworkRequestException.Serialization(throwable)
            )
            else -> NetworkResult.GenericError(
                NetworkRequestException.IllegalStateException(throwable)
            )
        }
    }
)

suspend inline fun <T> handlingNetworkSafety(
    noinline block: suspend CoroutineScope.() -> Data<T>,
) = safety(
    { NetworkResult.Success(supervisorScope(block).data) },
    { throwable ->
        when (throwable) {
            is ClientRequestException -> {
                val response = throwable.response
                val statusCode = response.status.value
                val errorMessage = throwable.message
                NetworkResult.GenericError(
                    NetworkRequestException.RequestException(
                        statusCode,
                        errorMessage,
                        throwable
                    )
                )
            }
            is SocketTimeoutException -> NetworkResult.GenericError(
                NetworkRequestException.Timeout(throwable)
            )
            is SerializationException -> NetworkResult.GenericError(
                NetworkRequestException.Serialization(throwable)
            )
            else -> NetworkResult.GenericError(
                NetworkRequestException.IllegalStateException(throwable)
            )
        }
    }
)
