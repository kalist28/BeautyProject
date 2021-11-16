package ru.kalistratov.template.beauty.common

sealed class NetworkRequestException(open val exception: Throwable) : Exception() {
    data class RequestException(
        val statusCode: Int?,
        val errorMessage: String?,
        override val exception: Throwable
    ) : NetworkRequestException(exception) {

        fun isNotPaid() = statusCode == 402
        fun isTooLarge() = statusCode == 413
        fun isNotFound() = statusCode == 404
        fun isForbidden() = statusCode == 403
        fun isBadRequest() = statusCode == 400
        fun isUnauthorized() = statusCode == 401
    }

    data class Timeout(override val exception: Throwable) : NetworkRequestException(exception)
    data class IOException(override val exception: Throwable) : NetworkRequestException(exception)
    data class Serialization(override val exception: Throwable) : NetworkRequestException(exception)
    data class IllegalStateException(override val exception: Throwable) :
        NetworkRequestException(exception)
}
