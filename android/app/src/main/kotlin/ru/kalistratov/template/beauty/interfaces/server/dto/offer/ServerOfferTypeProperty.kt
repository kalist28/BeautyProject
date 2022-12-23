package ru.kalistratov.template.beauty.interfaces.server.dto.offer

import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.Id

@Serializable
data class ServerOfferTypeProperty(
    val id: Id,
    val type_id: Id = "",
    val name: String = "",
    val description: String = "",
)