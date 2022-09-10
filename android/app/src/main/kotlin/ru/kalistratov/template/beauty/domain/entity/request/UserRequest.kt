package ru.kalistratov.template.beauty.domain.entity.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    var name: String? = null,
    var surname: String? = null,
    var patronymic: String? = null,
)