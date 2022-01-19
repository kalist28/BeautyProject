package ru.kalistratov.template.beauty.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import ru.kalistratov.template.beauty.domain.extension.getJson
import ru.kalistratov.template.beauty.infrastructure.extensions.jsonParser

@Serializable
data class User(
    val id: Long? = null,
    val email: String? = null
)

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RegistrationRequest(
    val email: String,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String,
)

@Serializable
data class ServerAuthResult(
    val user: User = User(),
    val token: String? = null
)

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
