package ru.kalistratov.template.beauty.presentation.feature.personalarea

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence
import ru.kalistratov.template.beauty.domain.feature.personalarea.PersonalAreaInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.personalarea.view.PersonalAreaIntent
import javax.inject.Inject

data class PersonalAreaState(
    val weekSequence: WeekSequence = WeekSequence(),
    val weekSequenceLoading: Boolean = true,
    val openEditWorkDaySequenceBottomSheet: Boolean = false,
    val editWorkDaySequence: WorkDaySequence? = null,
) : BaseState

sealed class PersonalAreaAction : BaseAction {
    data class UpdateWeekSequence(val weekSequence: WeekSequence) : PersonalAreaAction()
    data class OpenEditWorkDaySequenceBottomSheet(val day: WorkDaySequence) : PersonalAreaAction()
    object LoadWeekSequence : PersonalAreaAction()
    object Clear : PersonalAreaAction()
}

class PersonalAreaViewModel @Inject constructor(
    private val interactor: PersonalAreaInteractor
) : BaseViewModel<PersonalAreaIntent, PersonalAreaAction, PersonalAreaState>() {

    private val initialState = PersonalAreaState()
    private val stateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            val initFlow = intentFlow
                .filterIsInstance<PersonalAreaIntent.InitData>()
                .share(this)

            val loadWeekSequenceFlow = initFlow
                .map { interactor.getWeekSequence() }
                .share(this)

            val showLoadingWeekSequenceAction = initFlow
                .map { PersonalAreaAction.LoadWeekSequence }

            val updateWorkDaySequenceAction = intentFlow
                .filterIsInstance<PersonalAreaIntent.UpdateWorkDaySequence>()
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
                else flowOf(PersonalAreaAction.UpdateWeekSequence(it))
            }

            val openWorkDaySequenceEditBottomSheetAction = intentFlow
                .filterIsInstance<PersonalAreaIntent.WorkDaySequenceClick>()
                .flatMapConcat { intent ->
                    val lastState = stateFlow.value
                    val day = lastState.weekSequence.days
                        .find { it.day.index == intent.dayIndex }

                    if (day == null) emptyFlow()
                    else flowOf(
                        PersonalAreaAction.OpenEditWorkDaySequenceBottomSheet(day),
                        PersonalAreaAction.Clear,
                    )
                }

            merge(
                updateWeekSequenceAction,
                showLoadingWeekSequenceAction,
                openWorkDaySequenceEditBottomSheetAction
            )
                .flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .onEach {
                    stateFlow.value = it
                    shareStateFlow.emit(it)
                }
                .launchIn(this)
                .addTo(workComposite)
        }.addTo(workComposite)
    }

    override fun reduce(state: PersonalAreaState, action: PersonalAreaAction): PersonalAreaState {
        loge(action.javaClass.simpleName)
        return when (action) {
            is PersonalAreaAction.Clear -> state.copy(
                openEditWorkDaySequenceBottomSheet = false,
                editWorkDaySequence = null
            )
            is PersonalAreaAction.UpdateWeekSequence -> {
                state.copy(
                    weekSequence = action.weekSequence,
                    weekSequenceLoading = false,
                )
            }
            is PersonalAreaAction.LoadWeekSequence -> state.copy(weekSequenceLoading = true)
            is PersonalAreaAction.OpenEditWorkDaySequenceBottomSheet -> state.copy(
                openEditWorkDaySequenceBottomSheet = true,
                editWorkDaySequence = action.day
            )
        }
    }
}
