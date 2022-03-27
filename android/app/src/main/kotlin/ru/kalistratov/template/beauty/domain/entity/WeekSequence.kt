package ru.kalistratov.template.beauty.domain.entity

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.extension.Time
import ru.kalistratov.template.beauty.domain.extension.noTime

@Serializable
data class WeekSequence(
    @SerialName("data") val days: List<WorkDaySequence> = emptyList()
)

@Serializable
data class WorkDaySequence(
    val id: Int? = null,
    @Contextual val day: WeekDay,
    @SerialName("start_at") val startAt: Time = noTime,
    @SerialName("finish_at") val finishAt: Time = noTime,
    @SerialName("is_holiday") val isHoliday: Boolean = false,
)