package ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity

import ru.kalistratov.template.beauty.domain.entity.*

data class OfferCategoryContainer(
    val category: OfferCategory,
    val types: List<OfferTypeContainer>
)

sealed interface OfferTypeContainer {
    val type: OfferType

    data class Single(
        override val type: OfferType,
        val itemId: Id,
        val price: Price
    ) : OfferTypeContainer

    data class WithProperties(
        override val type: OfferType,
        val properties: List<OfferTypePropertyContainer>
    ) : OfferTypeContainer
}

data class OfferTypePropertyContainer(
    val itemId: Id,
    val property: OfferTypeProperty,
    val price: Price
)