package ru.kalistratov.template.beauty.infrastructure.entity

import com.soywiz.klock.Time
import ru.kalistratov.template.beauty.domain.extension.toClockFormat

data class TimeRange(
    val start: Time,
    val end: Time
) {
    operator fun contains(time: Time) = time in start..end

    override fun toString() = "${start.toClockFormat()} - ${end.toClockFormat()}"

    fun insideOf(timeRange: TimeRange) = start >= timeRange.start && end <= timeRange.end
}
