package ru.kalistratov.template.beauty.interfaces.server.dto.offer

import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.Id

@Serializable
data class RemoveOfferItemsRequest(
    val ids: List<Id>
)
