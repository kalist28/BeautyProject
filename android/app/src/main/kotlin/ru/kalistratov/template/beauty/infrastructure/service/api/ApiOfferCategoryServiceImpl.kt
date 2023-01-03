package ru.kalistratov.template.beauty.infrastructure.service.api

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.ServerUrl
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.SessionManager
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferCategory
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferType
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType
import ru.kalistratov.template.beauty.interfaces.server.service.ApiOfferCategoryService
import javax.inject.Inject

class ApiOfferCategoryServiceImpl @Inject constructor(
    url: ServerUrl,
    sessionManager: SessionManager,
    authSettingsService: AuthSettingsService
) : ApiService(url, sessionManager, authSettingsService), ApiOfferCategoryService {

    private val categoryUrl = "$url/offer/categories"
    override suspend fun get(
        id: Id,
        includeType: IncludeType?
    ): NetworkResult<ServerOfferCategory> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafety<ServerOfferCategory> {
                it.get("$categoryUrl/$id") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                    parameter("include", includeType?.value)
                }
            }
        }.logIfError()

    override suspend fun getTree(
        id: Id?,
        includeType: IncludeType?
    ): NetworkResult<List<ServerOfferCategory>> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafety<List<ServerOfferCategory>> {
                it.get("$categoryUrl-tree") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                    parameter("node_id", id)
                    parameter("include", includeType?.value)
                }
            }
        }.logIfError()

    override suspend fun getType(
        id: Id?,
        includeType: IncludeType
    ): NetworkResult<ServerOfferType> {
        TODO("Not yet implemented")
    }

}