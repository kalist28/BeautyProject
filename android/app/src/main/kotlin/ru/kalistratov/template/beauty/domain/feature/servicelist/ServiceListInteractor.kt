package ru.kalistratov.template.beauty.domain.feature.servicelist

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory

interface ServiceListInteractor {
    suspend fun loadCategories(): List<OfferCategory>
    suspend fun updateSelectedList(id: Id, fromCrumbs: Boolean, oldList:List<Id>): List<Id>
    suspend fun getNestedCategory(ids: List<Id>): List<OfferCategory>
}