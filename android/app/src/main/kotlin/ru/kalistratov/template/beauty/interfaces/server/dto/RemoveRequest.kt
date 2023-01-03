package ru.kalistratov.template.beauty.interfaces.server.dto

import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.Id

@Serializable
data class RemoveRequest(
    val ids: List<Id>
)

@Serializable
data class RemoveResponse(
    val ids: List<Id>,
    val message: String
)
