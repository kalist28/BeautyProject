package ru.kalistratov.template.beauty.infrastructure.service.api

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.RemoveOfferItemsRequest
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferItemDataBundle
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferItem
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType
import ru.kalistratov.template.beauty.interfaces.server.service.ApiOfferItemService

class ApiOfferItemServiceImpl(
    url: String,
    authSettingsService: AuthSettingsService
) : ApiService(url, authSettingsService), ApiOfferItemService {

    private val itemUrl = "$url/offer/items"

    override suspend fun load(
        id: Id,
        includeType: IncludeType
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<ServerOfferItem> {
            it.get("$itemUrl/$id") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                parameter("include", includeType.value)
            }
        }
    }

    override suspend fun loadAll(
        category: Id?,
        includeType: IncludeType
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<List<ServerOfferItem>> {
            it.get(itemUrl) {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                parameter("include", includeType.value)
                parameter("category", category)
            }
        }
    }.logIfError()

    override suspend fun post(
        id: Id?,
        offerItem: ServerOfferItemDataBundle,
        includeType: IncludeType
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<ServerOfferItem> {
            val request: HttpRequestBuilder.() -> Unit = {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                parameter("include", includeType.value)
                body = offerItem
            }
            if (id == null) it.post(itemUrl, request)
            else it.patch("$itemUrl/$id", request)
        }
    }.logIfError()

    override suspend fun remove(
        request: RemoveOfferItemsRequest
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<String> {
            it.delete(itemUrl) {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = request
            }
        }
    }.logIfError()
}