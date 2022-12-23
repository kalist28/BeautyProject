package ru.kalistratov.template.beauty.infrastructure.helper

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferItem
import ru.kalistratov.template.beauty.domain.entity.OfferType
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.OfferTypeContainer
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.OfferTypePropertyContainer

object OfferItemPacker {
    fun packItemsToContainers(
        items: List<OfferItem>
    ) = mutableMapOf<Id, List<OfferTypeContainer>>().apply {
        sortItemsByCategoryId(items).forEach { (categoryId, items) ->
            val containers = mutableListOf<OfferTypeContainer>()
            sortItemsByType(items).forEach { (type, items) ->
                packItemsInContainer(type, items)?.let(containers::add)
            }
            put(categoryId, containers)
        }
    }.toMap()

    private fun sortItemsByCategoryId(
        items: List<OfferItem>
    ) = mutableMapOf<Id, MutableList<OfferItem>>().apply {
        items.forEach { item ->
            val categoryId = item.type.categoryId
            val categoryItems = get(categoryId)
            if (categoryItems != null) categoryItems.add(item)
            else mutableListOf(item).also { put(categoryId, it) }
        }
    }.toMap()

    private fun sortItemsByType(
        items: List<OfferItem>
    ) = mutableMapOf<OfferType, MutableList<OfferItem>>().apply {
        items.forEach { item ->
            val type = item.type
            val containers = get(type)
            if (containers != null) containers.add(item)
            else mutableListOf(item).also { put(type, it) }
        }
    }

    private fun packItemsInContainer(
        type: OfferType, items: List<OfferItem>
    ): OfferTypeContainer? {
        if (items.isEmpty()) return null
        return if (items.size == 1 && items.first().typeProperty == null) items.first()
            .let { OfferTypeContainer.Single(type, it.id, it.price) }
        else items.filter { it.typeProperty != null }
            .map { OfferTypePropertyContainer(it.id, it.typeProperty!!, it.price) }
            .let { OfferTypeContainer.WithProperties(type, it) }
    }
}