package ru.kalistratov.template.beauty.domain.feature.offer.my.picker

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.entity.OfferItem
import ru.kalistratov.template.beauty.domain.entity.OfferType
import ru.kalistratov.template.beauty.presentation.entity.OfferTypeContainer

interface MyOfferPickerInteractor {
    suspend fun postSelected(itemId: Id)
    suspend fun getAllOfferItems(): Map<OfferCategory, List<OfferTypeContainer>>
}