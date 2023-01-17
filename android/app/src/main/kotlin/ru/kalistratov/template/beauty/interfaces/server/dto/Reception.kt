package ru.kalistratov.template.beauty.interfaces.server.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.Data
import ru.kalistratov.template.beauty.domain.entity.DataList
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.ServerOfferItem

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
data class MakeReservationRequest(
    @SerialName("date_at") val dateAt: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("workday_window_id") val windowId: String,
    @SerialName("created_by") val createdBy: String,
    @SerialName("created_for") val createdFor: String? = null,
)

@Serializable
data class ServerReservation(
    val id: String,
    @SerialName("date_at") val dateAt: String,
    val price: ServerPrice,
    val item: Data<ServerOfferItem>,
    @SerialName("workday_window") val window: Data<ServerSequenceDayWindow>,
)

@Serializable
data class FreeSequenceDayWindowsResponse(
    val windows: DataList<ServerSequenceDayWindow>
)