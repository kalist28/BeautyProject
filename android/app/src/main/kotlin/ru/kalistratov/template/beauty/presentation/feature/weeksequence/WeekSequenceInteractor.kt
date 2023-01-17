package ru.kalistratov.template.beauty.presentation.feature.weeksequence

import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.feature.weeksequence.WeekSequenceInteractor
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class WeekSequenceInteractorImpl(
    private val sequenceDayRepository: SequenceDayRepository
) : WeekSequenceInteractor {
    override suspend fun getWeekSequence(): SequenceWeek =
        sequenceDayRepository.getAll()

    override suspend fun updateWorkDaySequence(day: SequenceDay) =
        sequenceDayRepository.add(day)
}
