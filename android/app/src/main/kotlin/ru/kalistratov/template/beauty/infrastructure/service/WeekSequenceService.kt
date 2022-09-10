package ru.kalistratov.template.beauty.infrastructure.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.repository.api.ApiSequenceRepository
import ru.kalistratov.template.beauty.domain.service.WorkSequenceService

class WorkSequenceServiceImpl(
    private val apiSequenceRepository: ApiSequenceRepository
) : WorkSequenceService {

    override suspend fun loadWeekSequence(): NetworkResult<SequenceWeek> =
        apiSequenceRepository.getWeek()

    override suspend fun updateWorkDaySequence(
        workdaySequence: SequenceDay
    ): NetworkResult<SequenceDay> = when (workdaySequence.id.isNotBlank()) {
        true -> apiSequenceRepository.updateDay(workdaySequence)
        else -> apiSequenceRepository.createDay(workdaySequence)
    }
}
