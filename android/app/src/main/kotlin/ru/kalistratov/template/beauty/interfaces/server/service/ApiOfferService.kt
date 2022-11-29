package ru.kalistratov.template.beauty.interfaces.server.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferCategory
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferType
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType


interface ApiOfferCategoryService {
    suspend fun get(
        id: Id? = null,
        includeType: IncludeType? = null
    ): NetworkResult<List<ServerOfferCategory>>

    suspend fun getType(
        id: Id? = null,
        includeType: IncludeType
    ): NetworkResult<ServerOfferType>
}