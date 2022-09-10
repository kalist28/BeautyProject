package ru.kalistratov.template.beauty.domain.repository.api

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow

interface ApiWorkdayWindowRepository {
    suspend fun get(dayNumber: Int): NetworkResult<SequenceDay>
    suspend fun getAll(): NetworkResult<List<WorkdayWindow>>
    suspend fun create(window: WorkdayWindow): NetworkResult<WorkdayWindow>
    suspend fun update(window: WorkdayWindow): NetworkResult<WorkdayWindow>
}