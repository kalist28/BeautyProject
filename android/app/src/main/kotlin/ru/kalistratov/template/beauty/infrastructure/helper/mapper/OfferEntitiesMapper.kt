package ru.kalistratov.template.beauty.infrastructure.helper.mapper

import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.*

fun ServerOfferCategory.toLocal(): OfferCategory {
    val children = children.data
        .map(ServerOfferCategory::toLocal)

    val types = types.data
        .map(ServerOfferType::toLocal)

    return OfferCategory(
        id = id,
        parentId = parent_id,
        title = title,
        types = types,
        description = description,
        children = children
    )
}

fun ServerOfferType.toLocal() = OfferType(
    id = id,
    categoryId = category_id,
    name = name,
    description = description,
    properties = properties.data.map { it.toLocal() }
)

fun ServerOfferTypeProperty.toLocal() = OfferTypeProperty(
    id = id,
    typeId = type_id,
    name = name,
    description = description
)

fun ServerOfferItem.toLocal() = OfferItem(
    id = id,
    type = type.data.toLocal(),
    typeProperty = type_property?.data?.toLocal(),
    description = description,
    price = createPrice(price_from.toLocal(), price_to.toLocal()),
    published = published
)

fun OfferItemDataBundle.toServer() = ServerOfferItemDataBundle(
    type_id = typeId,
    type_property_id = typePropertyId,
    description = description,
    price_from = priceFrom,
    price_to = priceTo ?: 0,
    published = published
)