package ru.kalistratov.template.beauty.infrastructure.service.api

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.Identity.encode
import kotlinx.serialization.encodeToString
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.ServerUrl
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.SessionManager
import ru.kalistratov.template.beauty.infrastructure.extensions.jsonParser
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.interfaces.server.dto.RemoveRequest
import ru.kalistratov.template.beauty.interfaces.server.dto.RemoveResponse
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerClient
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerClientDataBundle
import ru.kalistratov.template.beauty.interfaces.server.service.ApiClientsService
import javax.inject.Inject

class ApiClientsServiceImpl @Inject constructor(
    url: ServerUrl,
    sessionManager: SessionManager,
    authSettingsService: AuthSettingsService
) : ApiService(url, sessionManager, authSettingsService), ApiClientsService {

    private val clientUrl = "$url/contacts"

    override suspend fun create(
        bundle: ServerClientDataBundle
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<ServerClient> {
            it.post(clientUrl) {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = bundle
                loge(jsonParser.encodeToString(bundle))
            }
        }
    }.logIfError()

    override suspend fun update(
        id: Id, bundle: ServerClientDataBundle
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<ServerClient> {
            it.patch("$clientUrl/$id") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = bundle
            }
        }
    }.logIfError()

    override suspend fun load(id: Id) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<ServerClient> {
            it.get("$clientUrl/$id") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
            }
        }
    }.logIfError()

    override suspend fun loadAll() = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<List<ServerClient>> {
            it.get(clientUrl) {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
            }
        }
    }.logIfError()

    override suspend fun remove(
        request: RemoveRequest
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<RemoveResponse> {
            it.get(clientUrl) {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = request
            }
        }
    }.logIfError()

}