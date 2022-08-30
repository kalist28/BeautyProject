package ru.kalistratov.template.beauty.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val email: String,
)