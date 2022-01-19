package ru.kalistratov.template.beauty.domain.feature.personalarea

import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence

interface PersonalAreaInteractor {
    suspend fun getWeekSequence(): WeekSequence?
    suspend fun updateWorkDAySequence(
        workDaySequence: WorkDaySequence
    ): Boolean
}
