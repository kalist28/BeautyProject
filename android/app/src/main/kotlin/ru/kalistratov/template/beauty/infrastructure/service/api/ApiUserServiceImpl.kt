package ru.kalistratov.template.beauty.infrastructure.service.api

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.entity.request.UpdateUserRequest
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.interfaces.server.service.ApiUserService
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

class ApiUserServiceImpl(
    url: String,
    authSettingsService: AuthSettingsService
) : ApiService(url, authSettingsService), ApiUserService {
    override suspend fun getUser(
        id: String?
    ): NetworkResult<User> = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<User> {
            val path = if (id == null) "$url/user/profile" else "$url/users/$id"
            it.get(path) {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
            }
        }
    }.logIfError()

    override suspend fun updateUser(
        request: UpdateUserRequest
    ): NetworkResult<User> = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<User> {
            it.patch("$url/users/${getUserId()}") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = request
            }
        }
    }.logIfError()
}
