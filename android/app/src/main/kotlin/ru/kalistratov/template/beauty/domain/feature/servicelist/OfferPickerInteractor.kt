package ru.kalistratov.template.beauty.domain.feature.servicelist

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.entity.OfferType

interface OfferPickerInteractor {
    suspend fun loadCategories(root: Id? = null): List<OfferCategory>
    suspend fun updateSelectedList(id: Id, fromCrumbs: Boolean, oldList: List<Id>): List<Id>
    suspend fun getNestedCategory(ids: List<Id>): List<OfferCategory>
    suspend fun getCategory(id: Id): OfferCategory?
    suspend fun getType(id: Id): OfferType?
}