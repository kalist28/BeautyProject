package ru.kalistratov.template.beauty.domain.entity

import com.soywiz.klock.Time
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.extension.noTime
import ru.kalistratov.template.beauty.infrastructure.kserialization.serializer.ClockFormatTimeSerializer

@Serializable
data class WeekSequence(
    @SerialName("data") val days: List<WorkdaySequence> = emptyList()
)

@Serializable
data class WorkdaySequence(
    val id: Id = -1,
    @Contextual val day: WeekDay = WeekDay.Nothing,

    @SerialName("start_at")
    @Serializable(with = ClockFormatTimeSerializer::class)
    val startAt: Time = noTime,

    @SerialName("finish_at")
    @Serializable(with = ClockFormatTimeSerializer::class)
    val finishAt: Time = noTime,

    @SerialName("is_holiday") val isHoliday: Boolean = false,
)
