package ru.kalistratov.template.beauty.domain.feature.weeksequence

import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.entity.SequenceDay

interface WeekSequenceInteractor {
    suspend fun getWeekSequence(): SequenceWeek?
    suspend fun updateWorkDaySequence(day: SequenceDay): SequenceDay?
}
