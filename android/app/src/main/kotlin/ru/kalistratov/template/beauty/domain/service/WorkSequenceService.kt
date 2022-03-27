package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Data
import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence

interface WorkSequenceService {
    suspend fun loadWeekSequence(): NetworkResult<WeekSequence>
    suspend fun updateWorkDaySequence(
        workDaySequence: WorkDaySequence
    ): NetworkResult<WorkDaySequence>
}
