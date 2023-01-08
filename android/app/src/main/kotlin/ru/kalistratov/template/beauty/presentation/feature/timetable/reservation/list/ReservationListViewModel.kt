package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.feature.timetable.reservation.list.ReservationListInteractor
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.clickDebounce
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.view.ReservationListIntent
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.round

data class ReservationListState(
    val selectedDay: LocalDate = LocalDate.now(),
    val selectedSequenceDay: SequenceDay? = null,
    val sequenceWeek: SequenceWeek? = null,
) : BaseState

sealed interface ReservationListAction : BaseAction {
    data class UpdateSelectedDay(val date: LocalDate) : ReservationListAction
    data class UpdateSelectedSequenceDay(val day: SequenceDay?) : ReservationListAction
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
                .share(this, replay = 1)

            val daySelectedFlow = intentFlow
                .filterIsInstance<ReservationListIntent.DaySelected>()
                .clickDebounce()
                .share(this)

            val updateSelectedDay = daySelectedFlow
                .map { ReservationListAction.UpdateSelectedDay(it.date) }

            val updateSelectedSequenceDay = daySelectedFlow.map {
                showLoading()
                ReservationListAction.UpdateSelectedSequenceDay(
                    interactor.getSequenceDay(it.date.dayOfWeek.value)
                )
            }.onEach { hideLoading() }

            val updateSequenceWeek = initDataFlow.map {
                showLoading()
                ReservationListAction.UpdateSequenceWeek(interactor.getSequenceWeek())
            }.onEach { hideLoading() }

            intentFlow.filterIsInstance<ReservationListIntent.CreateReservation>()
                .clickDebounce()
                .onEach {
                    loge("11111111")
                    router?.toEdit() }
                .launchHere()

            merge(
                updateSelectedDay,
                updateSelectedSequenceDay,
                updateSequenceWeek
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
    }

    override fun initialState() = ReservationListState()
}
