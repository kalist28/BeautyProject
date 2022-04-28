package ru.kalistratov.template.beauty.domain.extension

import com.soywiz.klock.DateTime
import com.soywiz.klock.Time
import com.soywiz.klock.TimeFormat
import java.util.*

const val clockFormatPattern = "HH:mm"
val clockTimeFormat = TimeFormat(clockFormatPattern)
val noTime = Time(0)

fun Time.toClockFormat() = this.format(clockFormatPattern)

fun Time.getTotalMinute() = hour * 60 + minute

fun Time.isNoTime() = hour == 0 && minute == 0

fun DateTime.getTotalMinute(): Int = hours * 60 + minutes

fun DateTime.isNoTime() = this == DateTime(0)

fun Date.toTime() = DateTime.fromUnix(this.time).time

fun Date.toDateTime() = DateTime.fromUnix(this.time)
