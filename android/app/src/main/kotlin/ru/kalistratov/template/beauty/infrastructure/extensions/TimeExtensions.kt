package ru.kalistratov.template.beauty.domain.extension

import com.soywiz.klock.DateTime
import com.soywiz.klock.Time
import com.soywiz.klock.TimeFormat
import ru.kalistratov.template.beauty.infrastructure.entity.TimeRange
import java.util.*

const val clockFormatPattern = "HH:mm"
val clockTimeFormat = TimeFormat(clockFormatPattern)
val noTime = Time(0)

fun timeNow(): Time = DateTime.nowLocal().let {
    Time(
        hour = it.hours,
        minute = it.minutes,
    )
}

fun Time.insideWithoutCorners(timeRange: TimeRange) =
    this > timeRange.start && this < timeRange.end

fun Time?.toClockFormat() = (this ?: noTime).format(clockFormatPattern)

fun Time.toMilliseconds() = getTotalMinute() * 60 * 1000

fun Time.getTotalMinute() = hour * 60 + minute

fun Time.isNoTime() = hour == 0 && minute == 0

fun DateTime.getTotalMinute(): Int = hours * 60 + minutes

fun DateTime.isNoTime() = this == DateTime(0)

fun Date.toTime() = DateTime.fromUnix(this.time).time

fun Date.toDateTime() = DateTime.fromUnix(this.time)

fun Time.toTimeRange(time: Time) = TimeRange(this, time)