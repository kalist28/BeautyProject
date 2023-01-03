package ru.kalistratov.template.beauty.infrastructure.extensions

import android.telephony.PhoneNumberUtils
import ru.kalistratov.template.beauty.common.RegexPattern.ONLY_NUMBERS
import ru.kalistratov.template.beauty.domain.entity.PhoneNumber
import java.util.*

fun PhoneNumber.toStringNumbers() = replace(Regex(ONLY_NUMBERS), "")
fun PhoneNumber.toNumbers() = toStringNumbers().toLong()

fun PhoneNumber.toFormat() = PhoneNumberUtils
    .formatNumber(this, Locale.getDefault().country)
