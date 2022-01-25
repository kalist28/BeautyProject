package ru.kalistratov.template.beauty.presentation.feature.weeksequence

import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence
import ru.kalistratov.template.beauty.domain.feature.weeksequence.WeekSequenceInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.view.WeekSequenceIntent

data class WeekSequenceState(
    val weekSequence: WeekSequence = WeekSequence(),
    val weekSequenceLoading: Boolean = true,
    val openEditWorkDaySequenceBottomSheet: Boolean = false,
    val editWorkDaySequence: WorkDaySequence? = null,
) : BaseState

sealed class WeekSequenceAction : BaseAction {
    data class UpdateWeekSequence(val weekSequence: WeekSequence) : WeekSequenceAction()
    data class OpenEditWorkDaySequenceBottomSheet(val day: WorkDaySequence) : WeekSequenceAction()
    object LoadWeekSequence : WeekSequenceAction()
    object Clear : WeekSequenceAction()
}

class WeekSequenceViewModel @Inject constructor(
    private val router: WeekSequenceRouter,
    private val interactor: WeekSequenceInteractor
) : BaseViewModel<WeekSequenceIntent, WeekSequenceAction, WeekSequenceState>() {

    private val initialState = WeekSequenceState()
    private val stateFlow = MutableStateFlow(initialState)

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

            val updateWorkDaySequenceAction = intentFlow
                .filterIsInstance<WeekSequenceIntent.UpdateWorkDaySequence>()
                .flatMapConcat { intent ->
                    val updatedDay = intent.day
                    val updated = interactor.updateWorkDAySequence(updatedDay)
                    if (!updated) emptyFlow()
                    else {
                        val state = stateFlow.value
                        val days = state.weekSequence.days.toMutableList()
                        val oldItem = days.find {
                            it.day == updatedDay.day
                        } ?: return@flatMapConcat emptyFlow()

                        val lastIndex = days.lastIndexOf(oldItem)
                        days.removeAt(lastIndex)
                        days.add(lastIndex, updatedDay)

                        flowOf(WeekSequence(days))
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
                    val lastState = stateFlow.value
                    val day = lastState.weekSequence.days
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
                .onEach {
                    stateFlow.value = it
                }
                .collect(shareStateFlow)
        }.addTo(workComposite)
    }

    override fun reduce(state: WeekSequenceState, action: WeekSequenceAction) = when (action) {
        is WeekSequenceAction.Clear -> state.copy(
            openEditWorkDaySequenceBottomSheet = false,
            editWorkDaySequence = null
        )
        is WeekSequenceAction.UpdateWeekSequence -> {
            state.copy(
                weekSequence = action.weekSequence,
                weekSequenceLoading = false,
            )
        }
        is WeekSequenceAction.LoadWeekSequence -> state.copy(weekSequenceLoading = true)
        is WeekSequenceAction.OpenEditWorkDaySequenceBottomSheet -> state.copy(
            openEditWorkDaySequenceBottomSheet = true,
            editWorkDaySequence = action.day
        )
    }
}
