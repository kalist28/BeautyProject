package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list

import androidx.lifecycle.viewModelScope
import com.soywiz.klock.DateTime
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.common.DateTimeFormat
import ru.kalistratov.template.beauty.domain.entity.Reservation
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.feature.timetable.reservation.list.ReservationListInteractor
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.clickDebounce
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.view.ReservationListIntent
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class ReservationListState(
    val selectedDay: LocalDate = LocalDate.now(),
    val selectedSequenceDay: SequenceDay? = null,
    val sequenceWeek: SequenceWeek? = null,
    val filledReservations: List<Reservation> = emptyList(),
) : BaseState

sealed interface ReservationListAction : BaseAction {
    data class UpdateSelectedDay(val date: LocalDate) : ReservationListAction
    data class UpdateSelectedSequenceDay(val day: SequenceDay?) : ReservationListAction
    data class UpdateReservations(val reservations: List<Reservation>) : ReservationListAction
    data class UpdateSequenceWeek(val week: SequenceWeek) : ReservationListAction
}

class ReservationListViewModel @Inject constructor(
    private val interactor: ReservationListInteractor,
) : BaseViewModel<ReservationListIntent, ReservationListAction, ReservationListState>(),
    ViewModelLoadingSupport by ViewModelLoadingSupportBaseImpl() {

    var router: ReservationListRouter? = null

    init {
        viewModelScope.launch {

            val initDataFlow = intentFlow
                .filterIsInstance<ReservationListIntent.InitData>()
                .share(this)

            val daySelectedFlow = intentFlow
                .filterIsInstance<ReservationListIntent.DaySelected>()
                .clickDebounce()
                .share(this)

            val updateReservationsAction = daySelectedFlow.map {
                val millis = it.date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val reservations = interactor.getReservations(
                    DateTime(millis).format(DateTimeFormat.DATE_STANDART)
                )
                ReservationListAction.UpdateReservations(reservations)
            }

            val updateSelectedDay = daySelectedFlow
                .map { ReservationListAction.UpdateSelectedDay(it.date) }

            val updateSelectedSequenceDay = daySelectedFlow.map {
                showLoading()
                loge(it.date.dayOfWeek.value)
                ReservationListAction.UpdateSelectedSequenceDay(
                    interactor.getSequenceDay(it.date.dayOfWeek.value - 1)
                )
            }.onEach { hideLoading() }

            val updateSequenceWeek = initDataFlow.map {
                showLoading()
                ReservationListAction.UpdateSequenceWeek(interactor.getSequenceWeek())
            }.onEach { hideLoading() }

            intentFlow.filterIsInstance<ReservationListIntent.CreateReservation>()
                .clickDebounce()
                .onEach {
                    router?.toEdit()
                }
                .launchHere()

            merge(
                updateSelectedDay,
                updateSequenceWeek,
                updateReservationsAction,
                updateSelectedSequenceDay
            ).collectState()
        }
    }

    override fun reduce(
        state: ReservationListState,
        action: ReservationListAction
    ) = when (action) {
        is ReservationListAction.UpdateSelectedDay -> state.copy(
            selectedDay = action.date
        )
        is ReservationListAction.UpdateSequenceWeek -> state.copy(
            sequenceWeek = action.week
        )
        is ReservationListAction.UpdateSelectedSequenceDay -> state.copy(
            selectedSequenceDay = action.day
        )
        is ReservationListAction.UpdateReservations -> state.copy(
            filledReservations = action.reservations
        )
    }

    override fun initialState() = ReservationListState()
}
