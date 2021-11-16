package ru.kalistratov.template.beauty.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import ru.kalistratov.template.beauty.domain.extension.getJson
import ru.kalistratov.template.beauty.infrastructure.extensions.jsonParser

@Serializable
sealed class AuthResult {
    @Serializable
    data class Success(val authResult: ServerAuthResult) : AuthResult()
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
        val login: Array<String> = emptyArray(),
        val email: Array<String> = emptyArray(),
        val password: Array<String> = emptyArray(),
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Errors

            if (!login.contentEquals(other.login)) return false
            if (!email.contentEquals(other.email)) return false
            if (!password.contentEquals(other.password)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = login.contentHashCode()
            result = 31 * result + email.contentHashCode()
            result = 31 * result + password.contentHashCode()
            return result
        }
    }
}

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RegistrationRequest(
    val login: String,
    val email: String,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String,
)
