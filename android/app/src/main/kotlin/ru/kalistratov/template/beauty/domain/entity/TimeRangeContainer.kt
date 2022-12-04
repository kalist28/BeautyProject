package ru.kalistratov.template.beauty.domain.entity

import com.soywiz.klock.Time
import ru.kalistratov.template.beauty.infrastructure.extensions.toClockFormat

abstract class TimeRangeContainer {
    abstract val startAt: Time
    abstract val finishAt: Time

    fun timeSource(type: TimeSourceType) = when (type) {
        TimeSourceType.START_KEY -> startAt
        TimeSourceType.FINISH_KEY -> finishAt
    }.let { TimeSource(it, type) }

    fun toContentTimeRange() = "${startAt.toClockFormat()} - ${finishAt.toClockFormat()}"
}