package ru.kalistratov.template.beauty.interfaces.server.dto

import com.soywiz.klock.Time
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.emptyId

@Serializable
data class ServerSequenceDayWindow(
    val id: Id = emptyId,

    @SerialName("sequence_day_id")
    val sequenceDayId: Id = emptyId,

    @SerialName("start_at")
    @Contextual
    val startAt: Time,

    @SerialName("finish_at")
    @Contextual
    val finishAt: Time,
)