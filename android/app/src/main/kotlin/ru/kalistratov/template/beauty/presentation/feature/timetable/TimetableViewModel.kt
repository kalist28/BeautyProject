package ru.kalistratov.template.beauty.presentation.feature.timetable

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.feature.timetable.TimetableInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.presentation.feature.timetable.view.TimetableIntent
import javax.inject.Inject

data class TimetableState(val a: Int = 0) : BaseState

sealed class TimetableAction : BaseAction

class TimetableViewModel @Inject constructor(
    private val interactor: TimetableInteractor,
) : BaseViewModel<TimetableIntent, TimetableAction, TimetableState>() {

    init {
        viewModelScope.launch {

/*
            merge(
                loadUserMenuAdapterAction
            )
                .flowOn(Dispatchers.IO)
                .scan(TimetableState(), ::reduce)
                .onEach { stateFlow.emit(it) }
                .launchIn(this)
                .addTo(workComposite)*/
        }
    }

    override fun reduce(state: TimetableState, action: TimetableAction) = TimetableState()
}
