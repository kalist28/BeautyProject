package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list

import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.feature.timetable.reservation.list.ReservationListInteractor
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository
import javax.inject.Inject

class ReservationListInteractorImpl @Inject constructor(
    private val sequenceDayRepository: SequenceDayRepository
) : ReservationListInteractor {
    override suspend fun getSequenceDay(index: Int): SequenceDay? =
        sequenceDayRepository.get(index)

    override suspend fun getSequenceWeek() =
        sequenceDayRepository.getAll()
}
