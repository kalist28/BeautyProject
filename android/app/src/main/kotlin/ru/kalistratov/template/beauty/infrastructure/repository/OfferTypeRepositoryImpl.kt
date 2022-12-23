package ru.kalistratov.template.beauty.infrastructure.repository

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.repository.OfferTypeRepository
import ru.kalistratov.template.beauty.infrastructure.extensions.process
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType
import ru.kalistratov.template.beauty.interfaces.server.service.ApiOfferTypeService

class OfferTypeRepositoryImpl(
    private val apiOfferItemService: ApiOfferTypeService
) : OfferTypeRepository {

    override suspend fun get(id: Id) =
        apiOfferItemService.load(
            id,
            IncludeType.Properties
        ).process(
            success = { toLocal() },
            error = { null }
        )
}