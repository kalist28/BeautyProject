package ru.kalistratov.template.beauty.presentation.feature.calendar

import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendarview.model.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.feature.calendar.CalendarInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.feature.calendar.view.CalendarIntent
import javax.inject.Inject

data class CalendarState(
    val selectedDay: CalendarDay? = null,
    val showDayDetails: Boolean = false
) : BaseState

sealed class CalendarAction : BaseAction {
    data class UpdateSelectedDay(
        val day: CalendarDay?,
        val show: Boolean = false
    ) : CalendarAction()

    object Clear : CalendarAction()
}

class CalendarViewModel @Inject constructor(
    private val interactor: CalendarInteractor,
) : BaseViewModel<CalendarIntent, CalendarAction, CalendarState>() {

    init {
        viewModelScope.launch {

            val cleanSelectedDayAction = intentFlow
                .filterIsInstance<CalendarIntent.SelectedDayCloses>()
                .map { CalendarAction.UpdateSelectedDay(null) }

            val showSelectedDayAction = intentFlow
                .filterIsInstance<CalendarIntent.DaySelected>()
                .flatMapConcat {
                    flowOf(
                        CalendarAction.UpdateSelectedDay(it.day, true),
                        CalendarAction.Clear
                    )
                }

            merge(
                showSelectedDayAction,
                cleanSelectedDayAction,
            )
                .flowOn(Dispatchers.IO)
                .scan(CalendarState(), ::reduce)
                .onEach { stateFlow.emit(it) }
                .launchIn(this)
                .addTo(workComposite)
        }
    }

    override fun reduce(state: CalendarState, action: CalendarAction) = when(action) {
        is CalendarAction.Clear -> state.copy(
            showDayDetails = false
        )
        is CalendarAction.UpdateSelectedDay -> state.copy(
            selectedDay = action.day,
            showDayDetails = action.show,
        )
    }
}
