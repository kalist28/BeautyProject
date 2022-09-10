package ru.kalistratov.template.beauty.presentation.feature.weeksequence

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.feature.weeksequence.WeekSequenceInteractor
import ru.kalistratov.template.beauty.domain.service.WorkSequenceService

class WeekSequenceInteractorImpl(
    private val workSequenceService: WorkSequenceService
) : WeekSequenceInteractor {
    override suspend fun getWeekSequence(): SequenceWeek? =
        when (val response = workSequenceService.loadWeekSequence()) {
            is NetworkResult.Success -> response.value
            else -> null
        }

    override suspend fun updateWorkDaySequence(
        workdaySequence: SequenceDay
    ): SequenceDay? = when (
        val result = workSequenceService.updateWorkDaySequence(workdaySequence)
    ) {
        is NetworkResult.Success -> result.value
        else -> null
    }
}
