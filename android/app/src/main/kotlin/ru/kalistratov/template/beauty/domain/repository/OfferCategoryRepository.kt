package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.entity.Id

interface OfferCategoryRepository {
    suspend fun get(id: Id): OfferCategory?
    suspend fun getAll(root: Id? = null): List<OfferCategory>
    suspend fun findNested(ids: List<Id>): List<OfferCategory>
}