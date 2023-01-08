package ru.kalistratov.template.beauty.presentation.feature.weeksequence

import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.feature.weeksequence.WeekSequenceInteractor
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository

class WeekSequenceInteractorImpl(
    private val sequenceDayRepository: SequenceDayRepository
) : WeekSequenceInteractor {
    override suspend fun getWeekSequence(): SequenceWeek =
        sequenceDayRepository.getAll()
            .sortedBy { it.day.index.run { if (this == 0) 7 else this } }

    override suspend fun updateWorkDaySequence(day: SequenceDay) =
        sequenceDayRepository.add(day)
}
