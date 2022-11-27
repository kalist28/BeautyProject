package ru.kalistratov.template.beauty.infrastructure.entity.dto

import com.soywiz.klock.Time
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.entity.Data
import ru.kalistratov.template.beauty.domain.entity.WeekDay
import ru.kalistratov.template.beauty.infrastructure.extensions.noTime
import ru.kalistratov.template.beauty.infrastructure.kserialization.serializer.ClockFormatTimeSerializer

@Serializable
data class ServerSequenceDay(
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

    @SerialName("windows")
    val windowsData: Data<List<ServerSequenceDayWindow>>
)