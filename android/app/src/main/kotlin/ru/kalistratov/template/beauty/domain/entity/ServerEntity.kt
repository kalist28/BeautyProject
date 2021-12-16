package ru.kalistratov.template.beauty.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class ServerData<T>(val data: T)

@Serializable
data class ServerUser(
    val id: Long? = null,
    val email: String? = null
)

@Serializable
data class ServerAuthResult(
    val user: ServerUser = ServerUser(),
    val token: String? = null
)
