package ru.kalistratov.template.beauty.interfaces.server.dto.offer

import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.Data
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerPrice

@Serializable
data class ServerOfferItem constructor(
    val id: Id,
    val type: Data<ServerOfferType>,
    val type_property: Data<ServerOfferTypeProperty>?,
    val description: String = "",
    val price_from: ServerPrice,
    val price_to: ServerPrice,
    val published: Boolean,
)

@Serializable
data class ServerOfferItemDataBundle(
    val type_id: Id?,
    val type_property_id: Id?,
    val description: String? = null,
    val price_from: Int,
    val price_to: Int,
    val published: Boolean,
)