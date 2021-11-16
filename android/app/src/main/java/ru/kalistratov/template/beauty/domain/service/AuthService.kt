package ru.kalistratov.template.beauty.domain.service

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.domain.entity.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.RegistrationRequest
import ru.kalistratov.template.beauty.domain.entity.ServerAuthResult
import ru.kalistratov.template.beauty.domain.extension.getClient

interface AuthService {
    suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<ServerAuthResult>

    suspend fun auth(
        request: AuthRequest
    ): NetworkResult<ServerAuthResult>
}

class AuthServiceImpl(private val url: String) : AuthService {

    override suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<ServerAuthResult> =
        getClient().use {
            handlingNetworkSafety {
                it.post("$url/register") {
                    contentType(ContentType.Application.Json)
                    body = request
                }
            }
        }

    override suspend fun auth(
        request: AuthRequest
    ): NetworkResult<ServerAuthResult> =
        getClient().use {
            handlingNetworkSafety {
                it.post("$url/login") {
                    contentType(ContentType.Application.Json)
                    body = request
                }
            }
        }
}
