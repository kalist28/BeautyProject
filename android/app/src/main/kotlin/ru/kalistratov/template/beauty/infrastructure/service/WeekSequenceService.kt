package ru.kalistratov.template.beauty.infrastructure.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkdaySequence
import ru.kalistratov.template.beauty.domain.entity.exist
import ru.kalistratov.template.beauty.domain.repository.api.ApiRepository
import ru.kalistratov.template.beauty.domain.service.WorkSequenceService

class WorkSequenceServiceImpl(
    private val apiRepository: ApiRepository
) : WorkSequenceService {

    override suspend fun loadWeekSequence(): NetworkResult<WeekSequence> =
        apiRepository.loadWeekSequence()

    override suspend fun updateWorkDaySequence(
        workdaySequence: WorkdaySequence
    ): NetworkResult<WorkdaySequence> = when (workdaySequence.id.exist()) {
        true -> apiRepository.updateWorkDaySequence(workdaySequence)
        else -> apiRepository.createWorkDaySequence(workdaySequence)
    }
}
