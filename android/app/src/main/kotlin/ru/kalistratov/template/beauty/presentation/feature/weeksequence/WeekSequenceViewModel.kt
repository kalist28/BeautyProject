package ru.kalistratov.template.beauty.presentation.feature.weeksequence

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.feature.weeksequence.WeekSequenceInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.view.WeekSequenceIntent
import ru.kalistratov.template.beauty.presentation.view.weeksequence.EditSequenceDayBottomSheet
import javax.inject.Inject

data class WeekSequenceState(
    val weekSequence: SequenceWeek = emptyList(),
    val weekSequenceLoading: Boolean = true,
    val openEditWorkDaySequenceBottomSheet: Boolean = false,
    val editWorkdaySequence: SequenceDay? = null,
) : BaseState

sealed class WeekSequenceAction : BaseAction {
    data class UpdateWeekSequence(val sequenceWeek: SequenceWeek) : WeekSequenceAction()
    data class OpenEditWorkDaySequenceBottomSheet(val day: SequenceDay) : WeekSequenceAction()
    object LoadWeekSequence : WeekSequenceAction()
    object Clear : WeekSequenceAction()
}

class WeekSequenceViewModel @Inject constructor(
    private val router: WeekSequenceRouter,
    private val interactor: WeekSequenceInteractor
) : BaseViewModel<WeekSequenceIntent, WeekSequenceAction, WeekSequenceState>() {

    private val initialState = WeekSequenceState()
    private val _stateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {
            val initFlow = intentFlow
                .filterIsInstance<WeekSequenceIntent.InitData>()
                .share(this)

            val loadWeekSequenceFlow = initFlow
                .map { interactor.getWeekSequence() }
                .share(this)

            val showLoadingWeekSequenceAction = initFlow
                .map { WeekSequenceAction.LoadWeekSequence }

            intentFlow.filterIsInstance<WeekSequenceIntent.WorkDayBottomSheetClick>()
                .onEach {
                    if (it.intent is EditSequenceDayBottomSheet.ClickIntent.EditWindows) {
                        router.openEditWorkdayWindows(it.intent.day.day.index)
                    }
                }
                .launchHere()

            val updateWorkDaySequenceAction = intentFlow
                .filterIsInstance<WeekSequenceIntent.UpdateWorkDaySequence>()
                .flatMapConcat { intent ->
                    val dayToUpdate = intent.day
                    val updatedDay = interactor.updateWorkDaySequence(dayToUpdate)
                    if (updatedDay == null) emptyFlow()
                    else {
                        val state = _stateFlow.value
                        val days = state.weekSequence.toMutableList()
                        val oldItem = days.find {
                            it.day == updatedDay.day
                        } ?: return@flatMapConcat emptyFlow()

                        val lastIndex = days.lastIndexOf(oldItem)
                        days.removeAt(lastIndex)
                        days.add(lastIndex, updatedDay)
                        flowOf(days)
                    }
                }
                .share(this)

            val updateWeekSequenceAction = merge(
                loadWeekSequenceFlow,
                updateWorkDaySequenceAction
            ).flatMapConcat {
                if (it == null) emptyFlow()
                else flowOf(WeekSequenceAction.UpdateWeekSequence(it))
            }

            val openWorkDaySequenceEditBottomSheetAction = intentFlow
                .filterIsInstance<WeekSequenceIntent.WorkDaySequenceClick>()
                .flatMapConcat { intent ->
                    val lastState = _stateFlow.value
                    val day = lastState.weekSequence
                        .find { it.day.index == intent.dayIndex }

                    if (day == null) emptyFlow()
                    else flowOf(
                        WeekSequenceAction.OpenEditWorkDaySequenceBottomSheet(day),
                        WeekSequenceAction.Clear,
                    )
                }

            intentFlow.filterIsInstance<WeekSequenceIntent.BackPressed>()
                .onEach { router.back() }
                .launchIn(this)
                .addTo(workComposite)

            merge(
                updateWeekSequenceAction,
                showLoadingWeekSequenceAction,
                openWorkDaySequenceEditBottomSheetAction
            )
                .flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .onEach { _stateFlow.value = it }
                .collect(stateFlow)
        }.addTo(workComposite)
    }

    override fun reduce(
        state: WeekSequenceState,
        action: WeekSequenceAction
    ): WeekSequenceState = when (action) {
        is WeekSequenceAction.Clear -> state.copy(
            openEditWorkDaySequenceBottomSheet = false,
            editWorkdaySequence = null
        )
        is WeekSequenceAction.UpdateWeekSequence -> state.copy(
            weekSequence = action.sequenceWeek,
            weekSequenceLoading = false,
        )
        is WeekSequenceAction.LoadWeekSequence -> state.copy(
            weekSequenceLoading = true
        )
        is WeekSequenceAction.OpenEditWorkDaySequenceBottomSheet -> state.copy(
            openEditWorkDaySequenceBottomSheet = true,
            editWorkdaySequence = action.day
        )
    }
}
