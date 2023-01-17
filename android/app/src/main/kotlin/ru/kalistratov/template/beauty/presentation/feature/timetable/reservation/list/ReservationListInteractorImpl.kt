package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list

import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.feature.timetable.reservation.list.ReservationListInteractor
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository
import ru.kalistratov.template.beauty.infrastructure.extensions.process
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.interfaces.server.service.ApiReceptionService
import javax.inject.Inject

class ReservationListInteractorImpl @Inject constructor(
    private val receptionService: ApiReceptionService,
    private val sequenceDayRepository: SequenceDayRepository
) : ReservationListInteractor {
    override suspend fun getSequenceDay(index: Int): SequenceDay? =
        sequenceDayRepository.get(index)

    override suspend fun getSequenceWeek() =
        sequenceDayRepository.getAll()

    override suspend fun getReservations(date: String) = receptionService
        .loadReservations(date).process(
            success = { map { it.toLocal() } },
            error = { emptyList() }
        )
}
