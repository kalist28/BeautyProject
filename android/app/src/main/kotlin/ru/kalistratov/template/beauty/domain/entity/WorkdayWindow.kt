package ru.kalistratov.template.beauty.domain.entity

import com.soywiz.klock.Time
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.infrastructure.kserialization.serializer.ClockFormatTimeSerializer

@Serializable
data class WorkdayWindow(
    val id: Id = -1,
    @SerialName("sequence_day") val sequence_day: Int = -1,

    @SerialName("start_at")
    @Serializable(with = ClockFormatTimeSerializer::class)
    val startAt: Time,

    @SerialName("finish_at")
    @Serializable(with = ClockFormatTimeSerializer::class)
    val finishAt: Time,
)
