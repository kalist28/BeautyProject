package ru.kalistratov.template.beauty.domain.feature.editworkdaywindows

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow

interface EditWorkdayWindowsInteractor {
    suspend fun getWindows(): List<WorkdayWindow>
    suspend fun createWindow(window: WorkdayWindow): NetworkResult<WorkdayWindow>
    suspend fun updateWindow(window: WorkdayWindow): NetworkResult<WorkdayWindow>
    suspend fun getSequenceDay(dayNumber: Int): SequenceDay
}