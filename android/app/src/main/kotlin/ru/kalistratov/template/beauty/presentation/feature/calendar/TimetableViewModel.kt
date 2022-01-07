package ru.kalistratov.template.beauty.presentation.feature.calendar

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.feature.calendar.CalendarInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.presentation.feature.calendar.view.CalendarIntent
import javax.inject.Inject

data class CalendarState(val a: Int = 0) : BaseState

sealed class CalendarAction : BaseAction

class CalendarViewModel @Inject constructor(
    private val interactor: CalendarInteractor,
) : BaseViewModel<CalendarIntent, CalendarAction, CalendarState>() {

    init {
        viewModelScope.launch {

/*
            merge(
                loadUserMenuAdapterAction
            )
                .flowOn(Dispatchers.IO)
                .scan(CalendarState(), ::reduce)
                .onEach { stateFlow.emit(it) }
                .launchIn(this)
                .addTo(workComposite)*/
        }
    }

    override fun reduce(state: CalendarState, action: CalendarAction) = CalendarState()
}
