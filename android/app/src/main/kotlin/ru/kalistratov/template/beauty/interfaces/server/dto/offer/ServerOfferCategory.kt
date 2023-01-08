package ru.kalistratov.template.beauty.interfaces.server.dto.offer

import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.Data
import ru.kalistratov.template.beauty.domain.entity.DataList
import ru.kalistratov.template.beauty.domain.entity.Id

@Serializable
data class ServerOfferCategory(
    val id: Id = "",
    val parent_id: Id? = null,
    val title: String = "",
    val description: String = "",
    val types: DataList<ServerOfferType> = DataList.empty(),
    val children: DataList<ServerOfferCategory> = DataList.empty()
)