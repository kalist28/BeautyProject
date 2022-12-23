package ru.kalistratov.template.beauty.interfaces.server.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferType
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType

interface ApiOfferTypeService {
    suspend fun load(
        id: Id,
        includeType: IncludeType
    ): NetworkResult<ServerOfferType>
}