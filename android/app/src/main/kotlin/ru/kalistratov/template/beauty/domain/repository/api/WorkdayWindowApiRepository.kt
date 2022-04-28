package ru.kalistratov.template.beauty.domain.repository.api

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow

interface WorkdayWindowApiRepository {
    suspend fun getWindows(): NetworkResult<List<WorkdayWindow>>
    suspend fun createWorkdayWindow(workdayWindow: WorkdayWindow): NetworkResult<WorkdayWindow>
}