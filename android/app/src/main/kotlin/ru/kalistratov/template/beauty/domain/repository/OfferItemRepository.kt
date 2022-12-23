package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.*

interface OfferItemRepository {
    suspend fun add(bundle: OfferItemDataBundle): OfferItem?
    suspend fun add(id: Id?, bundle: OfferItemDataBundle): OfferItem?
    suspend fun get(id: Id): OfferItem?
    suspend fun getAll(category: Id? = null): List<OfferItem>
    suspend fun remove(id: Id)
    suspend fun removeAll(ids: List<Id>)
}