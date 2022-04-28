package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkdaySequence

interface WorkSequenceService {
    suspend fun loadWeekSequence(): NetworkResult<WeekSequence>
    suspend fun updateWorkDaySequence(
        workdaySequence: WorkdaySequence
    ): NetworkResult<WorkdaySequence>
}
