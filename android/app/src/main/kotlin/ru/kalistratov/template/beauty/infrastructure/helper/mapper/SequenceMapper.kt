package ru.kalistratov.template.beauty.infrastructure.helper.mapper

import ru.kalistratov.template.beauty.domain.entity.Data
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.infrastructure.entity.dto.ServerSequenceDay
import ru.kalistratov.template.beauty.infrastructure.entity.dto.ServerSequenceDayWindow


fun ServerSequenceDay.toLocal() =
    SequenceDay(
        id = this.id,
        day = this.day,
        startAt = this.startAt,
        finishAt = this.finishAt,
        isHoliday = this.isHoliday,
        windows = this.windowsData.data.map { it.toLocal() }
    )

fun ServerSequenceDayWindow.toLocal() =
    SequenceDayWindow(
        id = this.id,
        sequenceDayId = this.sequenceDayId,
        startAt = this.startAt,
        finishAt = this.finishAt,
    )

fun SequenceDayWindow.toServer() =
    ServerSequenceDayWindow(
        id = this.id,
        sequenceDayId = this.sequenceDayId,
        startAt = this.startAt,
        finishAt = this.finishAt,
    )

fun SequenceDay.toServer() =
    ServerSequenceDay(
        id = this.id,
        day = this.day,
        startAt = this.startAt,
        finishAt = this.finishAt,
        isHoliday = this.isHoliday,
        windowsData = Data(this.windows.map { it.toServer() })
    )