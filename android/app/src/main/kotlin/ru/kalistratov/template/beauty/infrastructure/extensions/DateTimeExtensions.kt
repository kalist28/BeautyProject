package ru.kalistratov.template.beauty.infrastructure.extensions

import android.content.Context
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.yearMonth
import com.soywiz.klock.*
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WeekDay as ProjectWeekDay
import ru.kalistratov.template.beauty.infrastructure.entity.TimeRange
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import java.util.Date as JDate

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

fun Time.insideWithoutEnd(timeRange: TimeRange) =
    this >= timeRange.start && this < timeRange.end

fun Time.inside(timeRange: TimeRange) =
    this >= timeRange.start && this <= timeRange.end

fun Time?.toClockFormat() = (this ?: noTime).format(clockFormatPattern)

fun Time.toMilliseconds() = getTotalMinute() * 60 * 1000

fun Time.getTotalMinute() = hour * 60 + minute

fun Time.isNoTime() = hour == 0 && minute == 0

fun DateTime.getTotalMinute(): Int = hours * 60 + minutes

fun DateTime.isNoTime() = this == DateTime(0)

fun Date.monthTitle(
    context: Context,
    declination: Boolean = false,
    short: Boolean = false
) = context.getString(month1.toMonthResId(declination, short))

fun DateTime.monthTitle(
    context: Context,
    declination: Boolean = false,
    short: Boolean = false
) = context.getString(month1.toMonthResId(declination, short))

fun JDate.toTime() = DateTime.fromUnix(this.time).time

fun JDate.toDateTime() = DateTime.fromUnix(this.time)

fun Time.toTimeRange(time: Time) = TimeRange(this, time)

fun Time.plus(
    hour: Int = 0,
    minute: Int = 0,
    second: Int = 0,
    millisecond: Int = 0,
): Time {
    val timeSpan = TimeSpan(
        Time(hour, minute, second, millisecond)
            .toMilliseconds()
            .toDouble()
    )
    return Time(this.encoded + timeSpan)
}

fun Int.toMonthResId(
    declination: Boolean = false,
    short: Boolean = false
) = if (short) when (this) {
    1 -> R.string.january_short
    2 -> R.string.february_short
    3 -> if (declination) R.string.march_short else R.string.march_declination
    4 -> R.string.april_short
    5 -> if (declination) R.string.may_short else R.string.may_declination
    6 -> if (declination) R.string.june_short else R.string.june_declination
    7 -> if (declination) R.string.july_short else R.string.july_declination
    8 -> R.string.august_short
    9 -> R.string.september_short
    10 -> R.string.october_short
    11 -> R.string.november_short
    else -> R.string.december_short
} else if (declination) when (this) {
    1 -> R.string.january_declination
    2 -> R.string.february_declination
    3 -> R.string.march_declination
    4 -> R.string.april_declination
    5 -> R.string.may_declination
    6 -> R.string.june_declination
    7 -> R.string.july_declination
    8 -> R.string.august_declination
    9 -> R.string.september_declination
    10 -> R.string.october_declination
    11 -> R.string.november_declination
    else -> R.string.december_declination
} else when (this) {
    1 -> R.string.january
    2 -> R.string.february
    3 -> R.string.march
    4 -> R.string.april
    5 -> R.string.may
    6 -> R.string.june
    7 -> R.string.july
    8 -> R.string.august
    9 -> R.string.september
    10 -> R.string.october
    11 -> R.string.november
    else -> R.string.december
}

fun LocalDate.dayOfWeekIndex() = dayOfWeek.value - 1

fun LocalDate.toWeekDay() = ProjectWeekDay.fromIndex(dayOfWeekIndex())

fun YearMonth.displayText(short: Boolean = false) =
    "${this.month.displayText(short = short)} ${this.year}"

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.getDefault()).also { loge("$it -- ${this.value}") }
}

fun YearMonth.displayText(context: Context, short: Boolean = false) =
    "${this.month.displayText(context, short = short)} ${this.year}"

fun Month.displayText(context: Context, short: Boolean = true) =
    context.getString(value.toMonthResId())

fun DayOfWeek.displayText(uppercase: Boolean = false) =
    getDisplayName(TextStyle.SHORT, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    }

fun getWeekPageTitle(week: Week): String {
    val firstDate = week.days.first().date
    val lastDate = week.days.last().date
    return when {
        firstDate.yearMonth == lastDate.yearMonth -> {
            firstDate.yearMonth.displayText()
        }
        firstDate.year == lastDate.year -> {
            "${firstDate.month.displayText(short = false)} - ${lastDate.yearMonth.displayText()}"
        }
        else -> {
            "${firstDate.yearMonth.displayText()} - ${lastDate.yearMonth.displayText()}"
        }
    }
}

fun getWeekPageTitle(context: Context, week: Week): String {
    val firstDate = week.days.first().date
    val lastDate = week.days.last().date
    return when {
        firstDate.yearMonth == lastDate.yearMonth -> firstDate.yearMonth.displayText(context)
        firstDate.year == lastDate.year -> {
            val first = firstDate.month.displayText(context, false)
            val last = lastDate.yearMonth.displayText(context)
            "$first - $last"
        }
        else -> "${firstDate.yearMonth.displayText(context)} - ${lastDate.yearMonth.displayText()}"
    }
}

val DateTime.weekDay: ProjectWeekDay get() = when(dayOfWeek) {
    com.soywiz.klock.DayOfWeek.Monday -> ProjectWeekDay.Monday
    com.soywiz.klock.DayOfWeek.Tuesday -> ProjectWeekDay.Tuesday
    com.soywiz.klock.DayOfWeek.Wednesday -> ProjectWeekDay.Wednesday
    com.soywiz.klock.DayOfWeek.Thursday -> ProjectWeekDay.Thursday
    com.soywiz.klock.DayOfWeek.Friday -> ProjectWeekDay.Friday
    com.soywiz.klock.DayOfWeek.Saturday -> ProjectWeekDay.Saturday
    com.soywiz.klock.DayOfWeek.Sunday -> ProjectWeekDay.Sunday
}