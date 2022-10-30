package ru.kalistratov.template.beauty.infrastructure.entity.dto

import com.soywiz.klock.Time
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.emptyId
import ru.kalistratov.template.beauty.infrastructure.kserialization.serializer.ClockFormatTimeSerializer

@Serializable
data class ServerSequenceDayWindow(
    val id: Id = emptyId,

    @SerialName("sequence_day_id")
    val sequenceDayId: Id = emptyId,

    @SerialName("start_at")
    @Serializable(with = ClockFormatTimeSerializer::class)
    val startAt: Time,

    @SerialName("finish_at")
    @Serializable(with = ClockFormatTimeSerializer::class)
    val finishAt: Time,
)