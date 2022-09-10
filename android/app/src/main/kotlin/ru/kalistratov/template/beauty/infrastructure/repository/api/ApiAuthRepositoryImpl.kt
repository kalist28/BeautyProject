package ru.kalistratov.template.beauty.infrastructure.repository.api

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.common.handlingNetworkSafetyWithoutData
import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.entity.request.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.request.RegistrationRequest
import ru.kalistratov.template.beauty.domain.entity.request.ServerToken
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.repository.api.ApiAuthRepository
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

class ApiAuthRepositoryImpl(
    url: String,
    authSettingsService: AuthSettingsService
) : ApiRepository(url, authSettingsService), ApiAuthRepository {
    override suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<User> =
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
    ): NetworkResult<ServerToken> = getClient()
        .use {
            handlingNetworkSafetyWithoutData<ServerToken> {
                it.post("$url/clients/web/login") {
                    contentType(ContentType.Application.Json)
                    body = request
                }
            }
        }
        .logIfError()
}