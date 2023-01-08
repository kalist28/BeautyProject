package ru.kalistratov.template.beauty.presentation.feature.offer.my.picker

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.entity.OfferItem
import ru.kalistratov.template.beauty.domain.feature.offer.my.picker.MyOfferPickerInteractor
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository
import ru.kalistratov.template.beauty.domain.repository.OfferItemRepository
import ru.kalistratov.template.beauty.domain.service.MyOfferPickerService
import ru.kalistratov.template.beauty.infrastructure.helper.OfferItemPacker
import ru.kalistratov.template.beauty.presentation.entity.OfferTypeContainer
import javax.inject.Inject

class MyOfferPickerInteractorImpl @Inject constructor(
    private val offerItemRepository: OfferItemRepository,
    private val myOfferPickerService: MyOfferPickerService,
    private val offerCategoryRepository: OfferCategoryRepository,
) : MyOfferPickerInteractor {
    override suspend fun postSelected(itemId: Id) =
        myOfferPickerService.postSelected(itemId)

    override suspend fun getAllOfferItems(): Map<OfferCategory, List<OfferTypeContainer>> {
        val containers = OfferItemPacker.packItemsToContainers(offerItemRepository.getAll())
        return mutableMapOf<OfferCategory, List<OfferTypeContainer>>().apply {
            containers.keys.forEach { id ->
                offerCategoryRepository.get(id)?.let { category ->
                    containers[id]?.let { put(category, it) }
                }
            }
        }
    }

}