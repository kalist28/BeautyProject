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
        0 -> R.string.monday
        1 -> R.string.tuesday
        2 -> R.string.wednesday
        3 -> R.string.thursday
        4 -> R.string.friday
        5 -> R.string.saturday
        else -> R.string.sunday
    }

    @StringRes
    val shortTittleResId = when (index) {
        -1 -> R.string.blank_string
        0 -> R.string.monday_short
        1 -> R.string.tuesday_short
        2 -> R.string.wednesday_short
        3 -> R.string.thursday_short
        4 -> R.string.friday_short
        5 -> R.string.saturday_short
        else -> R.string.sunday_short
    }

    companion object {
        fun fromIndex(index: Int) = when (index) {
            6 -> Sunday
            0 -> Monday
            1 -> Tuesday
            2 -> Wednesday
            3 -> Thursday
            4 -> Friday
            5 -> Saturday
            else -> Nothing
        }
    }

    object Monday : WeekDay(0)
    object Tuesday : WeekDay(1)
    object Wednesday : WeekDay(2)
    object Thursday : WeekDay(3)
    object Friday : WeekDay(4)
    object Saturday : WeekDay(5)
    object Sunday : WeekDay(6)
    object Nothing : WeekDay(-1)
}
