package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.entity.SequenceDay

interface WorkSequenceService {
    suspend fun loadWeekSequence(): NetworkResult<SequenceWeek>
    suspend fun updateWorkDaySequence(
        workdaySequence: SequenceDay
    ): NetworkResult<SequenceDay>
}
