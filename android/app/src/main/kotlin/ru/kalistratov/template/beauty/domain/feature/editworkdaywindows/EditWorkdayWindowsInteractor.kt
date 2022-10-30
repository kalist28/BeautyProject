package ru.kalistratov.template.beauty.domain.feature.editworkdaywindows

import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow

interface EditWorkdayWindowsInteractor {
    suspend fun getSequenceDay(dayNumber: Int): SequenceDay?
    suspend fun pushWindow(window: SequenceDayWindow): SequenceDayWindow?
}