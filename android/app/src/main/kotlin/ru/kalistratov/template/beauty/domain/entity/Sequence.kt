package ru.kalistratov.template.beauty.domain.entity

import com.soywiz.klock.Time
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import ru.kalistratov.template.beauty.infrastructure.entity.TimeRange
import ru.kalistratov.template.beauty.infrastructure.extensions.isNoTime
import ru.kalistratov.template.beauty.infrastructure.extensions.noTime

typealias SequenceWeek = List<SequenceDay>

@Serializable
data class SequenceDay(
    val id: Id,

    @Contextual
    val day: WeekDay,

    @Contextual
    override val startAt: Time,

    @Contextual
    override val finishAt: Time,

    val isHoliday: Boolean,
    val windows: List<SequenceDayWindow>,
) : TimeRangeContainer() {
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

    fun updateByTimeSource(
        source: TimeSource
    ) = when (source.type) {
        TimeSourceType.START_KEY -> this.copy(startAt = source.time)
        TimeSourceType.FINISH_KEY -> this.copy(finishAt = source.time)
    }

    fun isNotExist() = !id.exist() || (startAt.isNoTime() && finishAt.isNoTime())
}

@Serializable
data class SequenceDayWindow(
    val id: Id = "",
    val sequenceDayId: Id = "",

    @Contextual
    override val startAt: Time,

    @Contextual
    override val finishAt: Time,
) : TimeRangeContainer() {
    fun updateByTimeSource(
        source: TimeSource
    ) = when (source.type) {
        TimeSourceType.START_KEY -> this.copy(startAt = source.time)
        TimeSourceType.FINISH_KEY -> this.copy(finishAt = source.time)
    }
}