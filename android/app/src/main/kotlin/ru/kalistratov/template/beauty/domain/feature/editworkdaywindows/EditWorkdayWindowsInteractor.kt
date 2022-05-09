package ru.kalistratov.template.beauty.domain.feature.editworkdaywindows

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.WorkdaySequence
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow

interface EditWorkdayWindowsInteractor {
    suspend fun getWindows(): List<WorkdayWindow>
    suspend fun createWindow(workdayWindow: WorkdayWindow): NetworkResult<WorkdayWindow>
    suspend fun updateWindow(workdayWindow: WorkdayWindow): NetworkResult<WorkdayWindow>
    suspend fun getWorkdaySequence(daySequenceId: Id): WorkdaySequence
}