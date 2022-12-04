package ru.kalistratov.template.beauty.domain.feature.editworkdaywindows

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow

interface EditWorkdayWindowsInteractor {
    suspend fun getSequenceDay(dayNumber: Int): SequenceDay?
    suspend fun pushWindow(window: SequenceDayWindow): SequenceDayWindow?
    suspend fun removeWindows(ids: List<Id>, day: SequenceDay): SequenceDay
    suspend fun createNewWindow(day: SequenceDay): SequenceDayWindow
    suspend fun updateWindowList(
        window: SequenceDayWindow,
        windows: List<SequenceDayWindow>,
    ): List<SequenceDayWindow>
}