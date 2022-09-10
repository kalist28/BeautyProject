package ru.kalistratov.template.beauty.domain.entity

import com.soywiz.klock.Time
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.extension.noTime
import ru.kalistratov.template.beauty.infrastructure.kserialization.serializer.ClockFormatTimeSerializer

@Serializable
data class SequenceWeek(
    @SerialName("data")
    val days: List<SequenceDay> = emptyList()
)

@Serializable
data class SequenceDay(
    val id: String = "",

    @Contextual
    @SerialName("week_day_number")
    val day: WeekDay = WeekDay.Nothing,

    @SerialName("start_at")
    @Serializable(with = ClockFormatTimeSerializer::class)
    val startAt: Time = noTime,

    @SerialName("finish_at")
    @Serializable(with = ClockFormatTimeSerializer::class)
    val finishAt: Time = noTime,

    @SerialName("is_holiday")
    val isHoliday: Boolean = false,
)
