package ru.kalistratov.template.beauty.interfaces.server.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerSequenceWeek(
    @SerialName("data")
    val days: List<ServerSequenceDay> = emptyList()
)