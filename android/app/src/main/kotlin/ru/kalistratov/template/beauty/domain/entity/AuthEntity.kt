package ru.kalistratov.template.beauty.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import ru.kalistratov.template.beauty.domain.extension.getJson
import ru.kalistratov.template.beauty.infrastructure.extensions.jsonParser
import ru.kalistratov.template.beauty.presentation.feature.registration.RegistrationAction

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
)

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RefreshRequest(
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class RegistrationRequest(
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val role: String = "specialist"
)

@Serializable
data class ServerToken(
    @SerialName("access_token") val token: String,
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val throwable: Throwable) : AuthResult() {
        var authError: AuthError? = null

        init {
            throwable.getJson()?.let { authError = jsonParser.decodeFromString(it) }
        }
    }
}

@Serializable
data class AuthError(
    val message: String? = null,
    val errors: Errors? = null,
) {
    @Serializable
    data class Errors(
        val email: Array<String> = emptyArray(),
        val password: Array<String> = emptyArray(),
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Errors

            if (!email.contentEquals(other.email)) return false
            if (!password.contentEquals(other.password)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = email.contentHashCode()
            result = 31 * result + password.contentHashCode()
            return result
        }
    }
}
