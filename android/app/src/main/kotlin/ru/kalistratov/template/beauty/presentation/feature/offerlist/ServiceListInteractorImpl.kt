package ru.kalistratov.template.beauty.presentation.feature.offerlist

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.feature.servicelist.ServiceListInteractor
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository

class ServiceListInteractorImpl(
    private val offerCategoryRepository: OfferCategoryRepository
) : ServiceListInteractor {

    override suspend fun loadCategories() =
        offerCategoryRepository.get()

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


}