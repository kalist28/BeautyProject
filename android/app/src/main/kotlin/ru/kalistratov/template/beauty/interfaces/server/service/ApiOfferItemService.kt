package ru.kalistratov.template.beauty.interfaces.server.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.RemoveOfferItemsRequest
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferItemDataBundle
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferItem
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType

interface ApiOfferItemService {
    suspend fun load(
        id: Id,
        includeType: IncludeType
    ): NetworkResult<ServerOfferItem>

    suspend fun loadAll(
        category: Id? = null,
        includeType: IncludeType
    ): NetworkResult<List<ServerOfferItem>>

    suspend fun post(
        id: Id?,
        offerItem: ServerOfferItemDataBundle,
        includeType: IncludeType
    ): NetworkResult<ServerOfferItem>

    suspend fun remove(
        request: RemoveOfferItemsRequest
    ): NetworkResult<String>
}