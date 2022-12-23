package ru.kalistratov.template.beauty.interfaces.server.dto.offer

import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.DataList
import ru.kalistratov.template.beauty.domain.entity.Id

@Serializable
data class ServerOfferType(
    val id: Id,
    val category_id: Id = "",
    val name: String = "",
    val description: String = "",
    val properties: DataList<ServerOfferTypeProperty> = DataList.empty()
)