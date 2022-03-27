package ru.kalistratov.template.beauty.presentation.feature.weeksequence

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence
import ru.kalistratov.template.beauty.domain.feature.weeksequence.WeekSequenceInteractor
import ru.kalistratov.template.beauty.domain.service.WorkSequenceService

class WeekSequenceInteractorImpl(
    private val workSequenceService: WorkSequenceService
) : WeekSequenceInteractor {
    override suspend fun getWeekSequence(): WeekSequence? =
        when (val response = workSequenceService.loadWeekSequence()) {
            is NetworkResult.Success -> response.value
            else -> null
        }

    override suspend fun updateWorkDaySequence(
        workDaySequence: WorkDaySequence
    ): WorkDaySequence? = when (
        val result = workSequenceService.updateWorkDaySequence(workDaySequence)
    ) {
        is NetworkResult.Success -> result.value
        else -> null
    }
}
