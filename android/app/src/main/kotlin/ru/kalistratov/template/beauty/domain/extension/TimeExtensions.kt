package ru.kalistratov.template.beauty.domain.extension

import com.cesarferreira.tempo.toDate
import com.cesarferreira.tempo.toString
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

typealias Time = String

const val clockFormat = "HH:mm"
const val noTime = "00:00"

fun Time.toCalendar(): Calendar = GregorianCalendar
    .getInstance()
    .apply { this.time = toDate(clockFormat) }

fun Calendar.getHour() = this.get(HOUR_OF_DAY)

fun Calendar.getMinute() = this.get(MINUTE)

fun Calendar.getTotalMinute(): Int {
    val hour = getHour()
    val minute = getMinute()
    return hour * 60 + minute
}

fun Time.getClockDate() = this.toDate(clockFormat)

fun Time.isNoTime() = this == noTime

fun Date.toClockDate() = this.toString(clockFormat)
