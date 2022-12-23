package ru.kalistratov.template.beauty.infrastructure.service.api

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferType
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType
import ru.kalistratov.template.beauty.interfaces.server.service.ApiOfferTypeService

class ApiOfferTypeServiceImpl(
    url: String,
    authSettingsService: AuthSettingsService
) : ApiService(url, authSettingsService), ApiOfferTypeService {

    private val itemUrl = "$url/offer/types"

    override suspend fun load(
        id: Id,
        includeType: IncludeType
    ): NetworkResult<ServerOfferType> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafety<ServerOfferType> {
                it.get("$itemUrl/$id") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                    parameter("include", includeType.value)
                }
            }
        }.logIfError()
}