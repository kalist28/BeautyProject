package ru.kalistratov.template.beauty.presentation.feature.personalarea

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence
import ru.kalistratov.template.beauty.domain.feature.personalarea.PersonalAreaInteractor
import ru.kalistratov.template.beauty.domain.service.WorkSequenceService
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class PersonalAreaInteractorImpl(
    private val workSequenceService: WorkSequenceService
) : PersonalAreaInteractor {
    override suspend fun getWeekSequence(): WeekSequence? =
        when (val week = workSequenceService.loadWeekSequence()) {
            is NetworkResult.Success -> week.value
            is NetworkResult.GenericError -> null
            else -> null
        }

    override suspend fun updateWorkDAySequence(workDaySequence: WorkDaySequence): Boolean {
        val result = workSequenceService.updateWorkDAySequence(workDaySequence)
        loge(result)
        return when (result) {
            is NetworkResult.Success -> true
            else -> false
        }
    }
}
