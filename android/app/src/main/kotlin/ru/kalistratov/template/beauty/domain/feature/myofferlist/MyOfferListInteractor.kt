package ru.kalistratov.template.beauty.domain.feature.myofferlist

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.entity.OfferItem
import ru.kalistratov.template.beauty.domain.entity.OfferType
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.MyOfferListViewTypeState
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.OfferCategoryContainer

interface MyOfferListInteractor {
    suspend fun getOfferItem(id: Id): OfferItem?
    suspend fun saveOfferItem(state: MyOfferListViewTypeState.CreatingItem)
    suspend fun updateOfferItem(state: MyOfferListViewTypeState.EditingItem)

    suspend fun getType(id: Id): OfferType?
    suspend fun getOfferCategoryContainers(): List<OfferCategoryContainer>
    suspend fun getOfferCategory(id: Id): OfferCategory?

    suspend fun filterNotCreatingTypes(categoryId: Id): List<OfferType>

    suspend fun removeOfferItem(id: Id)
}