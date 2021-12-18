package ru.kalistratov.template.beauty.presentation.feature.timetable

import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.presentation.feature.timetable.view.TimetableIntent

data class TimetableState(val a: Int = 0) : BaseState

sealed class TimetableAction : BaseAction

class TimetableViewModel : BaseViewModel<TimetableIntent, TimetableAction, TimetableState>() {
    override fun reduce(state: TimetableState, action: TimetableAction) = TimetableState()
}
