package ru.kalistratov.template.beauty.interfaces.server.dto

import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.Id

@Serializable
data class ServerClient(
    val id: Id,
    val name: String,
    val surname: String?,
    val patronymic: String?,
    val phone_number: String,
    val note: String?
)

@Serializable
data class ServerClientDataBundle(
    val name: String,
    val surname: String?,
    val patronymic: String?,
    val phone_number: Long,
    val note: String?
)