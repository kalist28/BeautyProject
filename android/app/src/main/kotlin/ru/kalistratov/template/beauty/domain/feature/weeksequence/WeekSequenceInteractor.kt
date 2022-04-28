package ru.kalistratov.template.beauty.domain.feature.weeksequence

import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkdaySequence

interface WeekSequenceInteractor {
    suspend fun getWeekSequence(): WeekSequence?
    suspend fun updateWorkDaySequence(
        workdaySequence: WorkdaySequence
    ): WorkdaySequence?
}
