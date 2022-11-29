package ru.kalistratov.template.beauty.infrastructure.helper.mapper

import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.entity.OfferType
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferCategory
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferType

fun ServerOfferCategory.toLocal(): OfferCategory {
    val children = children.data
        .map(ServerOfferCategory::toLocal)

    val types = types.data
        .map(ServerOfferType::toLocal)

    return OfferCategory(
        id = id,
        title = title,
        types = types,
        description = description,
        children = children
    )
}

fun ServerOfferType.toLocal() = OfferType(
    id = id,
    name = name,
    description = description
)