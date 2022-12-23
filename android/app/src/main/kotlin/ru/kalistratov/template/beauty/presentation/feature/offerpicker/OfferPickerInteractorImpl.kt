package ru.kalistratov.template.beauty.presentation.feature.offerpicker

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.entity.OfferType
import ru.kalistratov.template.beauty.domain.feature.servicelist.OfferPickerInteractor
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository

class OfferPickerInteractorImpl(
    private val offerCategoryRepository: OfferCategoryRepository
) : OfferPickerInteractor {

    override suspend fun loadCategories(root: Id?) =
        offerCategoryRepository.getAll(root)

    override suspend fun updateSelectedList(
        id: Id,
        fromCrumbs: Boolean,
        oldList: List<Id>
    ) = oldList.toMutableList().apply {
        if (id == "-1") clear()
        else when (contains(id)) {
            false -> add(id)
            true -> {
                val indexTo = indexOf(id).let { if (fromCrumbs) it + 1 else it }
                (size - 1 downTo indexTo).forEach(::removeAt)
            }
        }
    }

    override suspend fun getNestedCategory(ids: List<Id>): List<OfferCategory> =
        offerCategoryRepository.findNested(ids)

    override suspend fun getCategory(id: Id) =
        offerCategoryRepository.getAll(id)
            .find { it.id == id }

    override suspend fun getType(id: Id): OfferType? {
        offerCategoryRepository.getAll(id).forEach { category ->
            category.types.find { it.id == id }?.let { return it }
        }
        return null
    }

}