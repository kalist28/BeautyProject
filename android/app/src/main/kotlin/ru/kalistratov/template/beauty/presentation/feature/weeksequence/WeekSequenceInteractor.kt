package ru.kalistratov.template.beauty.presentation.feature.weeksequence

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkdaySequence
import ru.kalistratov.template.beauty.domain.feature.weeksequence.WeekSequenceInteractor
import ru.kalistratov.template.beauty.domain.service.WorkSequenceService
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class WeekSequenceInteractorImpl(
    private val workSequenceService: WorkSequenceService
) : WeekSequenceInteractor {
    override suspend fun getWeekSequence(): WeekSequence? =
        when (val response = workSequenceService.loadWeekSequence()) {
            is NetworkResult.Success -> response.value
            else -> {
                loge(response)
                null
            }
        }

    override suspend fun updateWorkDaySequence(
        workdaySequence: WorkdaySequence
    ): WorkdaySequence? = when (
        val result = workSequenceService.updateWorkDaySequence(workdaySequence)
    ) {
        is NetworkResult.Success -> result.value
        else -> null
    }
}
