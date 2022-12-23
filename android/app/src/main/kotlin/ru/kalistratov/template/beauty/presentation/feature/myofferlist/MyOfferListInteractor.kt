package ru.kalistratov.template.beauty.presentation.feature.myofferlist

import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.domain.feature.myofferlist.MyOfferListInteractor
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository
import ru.kalistratov.template.beauty.domain.repository.OfferItemRepository
import ru.kalistratov.template.beauty.domain.repository.OfferTypeRepository
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.infrastructure.helper.OfferItemPacker
import ru.kalistratov.template.beauty.presentation.entity.getFrom
import ru.kalistratov.template.beauty.presentation.entity.getTo
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.MyOfferListViewTypeState
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.OfferCategoryContainer

class MyOfferListInteractorImpl(
    private val offerTypeRepository: OfferTypeRepository,
    private val offerItemRepository: OfferItemRepository,
    private val offerCategoryRepository: OfferCategoryRepository
) : MyOfferListInteractor {

    override suspend fun getOfferCategoryContainers() = offerItemRepository.getAll().let {
        mutableListOf<OfferCategoryContainer>().apply {
            OfferItemPacker.packItemsToContainers(it).forEach { (categoryId, containers) ->
                getOfferCategory(categoryId)?.let { category ->
                    add(OfferCategoryContainer(category, containers))
                }
            }
        }.toList()
    }

    override suspend fun getOfferCategory(id: Id) =
        offerCategoryRepository.get(id)

    override suspend fun filterNotCreatingTypes(categoryId: Id): List<OfferType> {
        val category = getOfferCategory(categoryId) ?: return emptyList()
        val categoryTypes = category.types
        val existTypesName = mutableSetOf<String>()
        val existTypesToProperties = mutableMapOf<String, MutableList<String>>()
        offerItemRepository.getAll(category.id).forEach {
            val typeName = it.type.name
            existTypesName.add(typeName)
            val propertyName = it.typeProperty?.name ?: return@forEach
            val properties = existTypesToProperties[typeName]
            if (properties != null) properties.add(propertyName)
            else existTypesToProperties[typeName] = mutableListOf(propertyName)
        }
        return categoryTypes.filter { type ->
            val name = type.name
            if (type.properties.isNotEmpty()) !existTypesName.contains(name)
            else existTypesToProperties[name]
                ?.let { type.properties.map { name }.containsAll(it) }
                ?.not() ?: true
        }
    }

    override suspend fun removeOfferItem(id: Id) =
        offerItemRepository.remove(id)

    override suspend fun getOfferItem(id: Id) =
        offerItemRepository.get(id)

    override suspend fun saveOfferItem(state: MyOfferListViewTypeState.CreatingItem) {
        if (state.type == null || state.priceContainer == null) {
            loge("Error on save OfferItem because state.type(${state.type}), priceSelection(${state.priceContainer}).")
            return
        }
        val offerItemDataBundle = OfferItemDataBundle(
            typeId = state.type.id,
            typePropertyId = state.typeProperty?.id,
            description = state.description,
            priceFrom = state.priceContainer.getFrom(),
            priceTo = state.priceContainer.getTo(),
            published = true,
        )
        offerItemRepository.add(offerItemDataBundle)
    }

    override suspend fun updateOfferItem(state: MyOfferListViewTypeState.EditingItem) {
        val offerItemDataBundle = OfferItemDataBundle(
            priceFrom = state.priceContainer.getFrom(),
            priceTo = state.priceContainer.getTo(),
            description = state.description
        )
        offerItemRepository.add(state.offerItem.id, offerItemDataBundle)
    }

    override suspend fun getType(id: Id): OfferType? =
        offerTypeRepository.get(id)
}