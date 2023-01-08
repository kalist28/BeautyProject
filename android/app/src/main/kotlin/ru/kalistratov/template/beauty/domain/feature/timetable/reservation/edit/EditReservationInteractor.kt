package ru.kalistratov.template.beauty.domain.feature.timetable.reservation.edit

import com.soywiz.klock.Date
import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.domain.entity.*

interface EditReservationInteractor {
    suspend fun getSelectedMyOfferFlow(): Flow<Id>
    suspend fun getSelectedClientFlow(): Flow<Id>

    suspend fun getClient(id: Id): Client?
    suspend fun getOfferItem(id: Id): OfferItem?
    suspend fun getCategory(id: Id): OfferCategory?
    suspend fun getSequenceWeek(): SequenceWeek
    suspend fun makeReservation()
    suspend fun getFreeSequenceDayWindows(date: Date): List<SequenceDayWindow>
}
