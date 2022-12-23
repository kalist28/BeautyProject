package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferType

interface OfferTypeRepository {
    suspend fun get(id: Id): OfferType?
}