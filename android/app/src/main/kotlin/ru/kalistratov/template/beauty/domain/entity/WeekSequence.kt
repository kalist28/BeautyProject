package ru.kalistratov.template.beauty.domain.entity

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.domain.extension.Time
import ru.kalistratov.template.beauty.domain.extension.noTime

@Serializable
data class WeekSequence(
    val days: List<WorkDaySequence> = emptyList()
)

@Serializable()
data class WorkDaySequence(
    @Contextual val day: WeekDay,
    val from: Time = noTime,
    val to: Time = noTime,
    @SerialName("is_holiday") val isHoliday: Boolean = false,
)
