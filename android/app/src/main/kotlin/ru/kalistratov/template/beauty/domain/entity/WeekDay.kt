package ru.kalistratov.template.beauty.domain.entity

import androidx.annotation.StringRes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ru.kalistratov.template.beauty.R

sealed class WeekDay(val index: Int) {

    override fun toString(): String = this.javaClass.simpleName

    @StringRes
    val tittleResId = when (index) {
        -1 -> R.string.blank_string
        1 -> R.string.monday
        2 -> R.string.tuesday
        3 -> R.string.wednesday
        4 -> R.string.thursday
        5 -> R.string.friday
        6 -> R.string.saturday
        else -> R.string.sunday
    }

    @StringRes
    val shortTittleResId = when (index) {
        -1 -> R.string.blank_string
        1 -> R.string.monday_short
        2 -> R.string.tuesday_short
        3 -> R.string.wednesday_short
        4 -> R.string.thursday_short
        5 -> R.string.friday_short
        6 -> R.string.saturday_short
        else -> R.string.sunday_short
    }

    companion object {
        fun fromIndex(index: Int) = when (index) {
            -1 -> Nothing
            0 -> Sunday
            1 -> Monday
            2 -> Tuesday
            3 -> Wednesday
            4 -> Thursday
            5 -> Friday
            6 -> Saturday
            else -> null
        }
    }

    object Monday : WeekDay(1)
    object Tuesday : WeekDay(2)
    object Wednesday : WeekDay(3)
    object Thursday : WeekDay(4)
    object Friday : WeekDay(5)
    object Saturday : WeekDay(6)
    object Sunday : WeekDay(0)
    object Nothing : WeekDay(-1)
}
