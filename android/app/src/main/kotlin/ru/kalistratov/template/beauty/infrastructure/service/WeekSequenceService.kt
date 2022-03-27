package ru.kalistratov.template.beauty.infrastructure.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence
import ru.kalistratov.template.beauty.domain.repository.api.ApiRepository
import ru.kalistratov.template.beauty.domain.service.WorkSequenceService

class WorkSequenceServiceImpl(
    private val apiRepository: ApiRepository
) : WorkSequenceService {

    override suspend fun loadWeekSequence(): NetworkResult<WeekSequence> =
        apiRepository.loadWeekSequence()

    override suspend fun updateWorkDaySequence(
        workDaySequence: WorkDaySequence
    ): NetworkResult<WorkDaySequence> = when (workDaySequence.id == null) {
        true -> apiRepository.createWorkDaySequence(workDaySequence)
        else -> apiRepository.updateWorkDaySequence(workDaySequence)
    }
}
