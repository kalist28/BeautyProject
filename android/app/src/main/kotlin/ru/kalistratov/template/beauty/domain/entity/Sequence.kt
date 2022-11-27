package ru.kalistratov.template.beauty.domain.entity

import com.soywiz.klock.Time
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.infrastructure.extensions.noTime
import ru.kalistratov.template.beauty.infrastructure.extensions.toClockFormat
import ru.kalistratov.template.beauty.infrastructure.kserialization.serializer.ClockFormatTimeSerializer

typealias SequenceWeek = List<SequenceDay>

@Serializable
data class SequenceDay(
    val id: Id,

    @Contextual
    val day: WeekDay,

    @Serializable(ClockFormatTimeSerializer::class)
    val startAt: Time,

    @Serializable(ClockFormatTimeSerializer::class)
    val finishAt: Time,

    val isHoliday: Boolean,
    val windows: List<SequenceDayWindow>,
) {
    companion object {
        val emptyDay = SequenceDay(
            id = "",
            day = WeekDay.Nothing,
            startAt = noTime,
            finishAt = noTime,
            isHoliday = false,
            windows = emptyList()
        )
    }

    fun toContentTimeRange() = "${startAt.toClockFormat()} - ${finishAt.toClockFormat()}"
}

@Serializable
data class SequenceDayWindow(
    val id: Id = "",
    val sequenceDayId: Id = "",

    @Serializable(ClockFormatTimeSerializer::class)
    val startAt: Time,

    @Serializable(ClockFormatTimeSerializer::class)
    val finishAt: Time,
)