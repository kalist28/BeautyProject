package ru.kalistratov.template.beauty.common

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import ru.kalistratov.template.beauty.domain.entity.WeekDay
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence
import ru.kalistratov.template.beauty.infrastructure.extensions.jsonParser

fun main() {
    /*val w = WorkDaySequence(
        WeekDay.Monday,
        "10:00",
        "11:00"
    )

    jsonParser.encodeToString(w).let { println(it) }

    jsonParser.decodeFromString<WorkDaySequence>("""
        {
            "day": 1,
            "start_at": "10:00",
            "finish_at": "11:00",
            "is_holiday": false
        }
    """.trimIndent()).let { print(it) }*/
}
