package ru.kalistratov.template.beauty.interfaces.server.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.DataList

//TODO replace to Serializer
enum class SequenceDayWindowsMode {
    all,
    free,
    booked
}

@Serializable
data class FreeSequenceDayWindowsRequest(
    @SerialName("date_at") val date: String,
    @SerialName("mode") val mode: SequenceDayWindowsMode = SequenceDayWindowsMode.free
)

@Serializable
data class FreeSequenceDayWindowsResponse(
    val windows: DataList<ServerSequenceDayWindow>
)