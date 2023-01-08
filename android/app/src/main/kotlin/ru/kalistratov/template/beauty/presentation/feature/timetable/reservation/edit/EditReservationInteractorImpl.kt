package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit

import com.soywiz.klock.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import ru.kalistratov.template.beauty.common.DateTimeFormat
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.entity.OfferItem
import ru.kalistratov.template.beauty.domain.feature.timetable.reservation.edit.EditReservationInteractor
import ru.kalistratov.template.beauty.domain.repository.ClientsRepository
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository
import ru.kalistratov.template.beauty.domain.repository.OfferItemRepository
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository
import ru.kalistratov.template.beauty.domain.service.ClientPickerService
import ru.kalistratov.template.beauty.domain.service.MyOfferPickerService
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.infrastructure.extensions.process
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.interfaces.server.dto.FreeSequenceDayWindowsRequest
import ru.kalistratov.template.beauty.interfaces.server.service.ApiReceptionService
import javax.inject.Inject

class EditReservationInteractorImpl @Inject constructor(
    private val receptionService: ApiReceptionService,
    private val clientPickerService: ClientPickerService,
    private val myOfferPickerService: MyOfferPickerService,
    private val sequenceDayRepository: SequenceDayRepository,
    private val offerItemRepository: OfferItemRepository,
    private val categoryRepository: OfferCategoryRepository,
    private val clientRepository: ClientsRepository,
) : EditReservationInteractor {
    override suspend fun getSelectedMyOfferFlow() = myOfferPickerService.selections()

    override suspend fun getSelectedClientFlow() = clientPickerService.selections()
    override suspend fun getClient(id: Id) = clientRepository.get(id)

    override suspend fun getOfferItem(id: Id): OfferItem? = offerItemRepository.get(id)

    override suspend fun getCategory(id: Id): OfferCategory? = categoryRepository.get(id)

    override suspend fun getSequenceWeek() = sequenceDayRepository.getAll()
        .sortedBy { it.day.index }

    override suspend fun makeReservation() {
        receptionService
    }

    override suspend fun getFreeSequenceDayWindows(date: Date) = receptionService
        .loadFreeWindowsForDay(
            FreeSequenceDayWindowsRequest(date.format(DateTimeFormat.DATE_STANDART))
        ).also { loge(it) }.process(
            success = { windows.data.map { it.toLocal() } },
            error = { emptyList() }
        )
}