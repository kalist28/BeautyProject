package ru.kalistratov.template.beauty.domain.feature.timetable.reservation.list

import ru.kalistratov.template.beauty.domain.entity.Reservation
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek

interface ReservationListInteractor {
    suspend fun getSequenceDay(index: Int): SequenceDay?
    suspend fun getSequenceWeek(): SequenceWeek

    suspend fun getReservations(date: String): List<Reservation>
}
