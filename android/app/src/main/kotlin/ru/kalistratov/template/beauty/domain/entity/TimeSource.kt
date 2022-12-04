package ru.kalistratov.template.beauty.domain.entity

import com.soywiz.klock.Time

data class TimeSource(
    val time: Time,
    val type: TimeSourceType
)

enum class TimeSourceType(val key: String) {
    START_KEY("start"),
    FINISH_KEY("finish")
}