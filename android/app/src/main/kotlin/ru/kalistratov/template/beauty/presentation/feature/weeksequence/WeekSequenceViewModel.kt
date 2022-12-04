package ru.kalistratov.template.beauty.presentation.feature.weeksequence

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.entity.TimeSource
import ru.kalistratov.template.beauty.domain.feature.weeksequence.WeekSequenceInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.view.WeekSequenceIntent
import ru.kalistratov.template.beauty.presentation.view.weeksequence.EditSequenceDayBottomSheet
import java.util.Optional
import javax.inject.Inject

data class WeekSequenceState(
    val weekSequence: SequenceWeek = emptyList(),
    val selectedDay: SequenceDay? = null,
    val loading: Boolean = false,
    val timeForShowTimePicker: TimeSource? = null
) : BaseState

sealed class WeekSequenceAction : BaseAction {
    data class UpdateWeekSequence(val sequenceWeek: SequenceWeek) : WeekSequenceAction()
    data class UpdateSelectedDay(val day: SequenceDay?) : WeekSequenceAction()
    data class ShowTimePicker(val time: TimeSource?) : WeekSequenceAction()
    object ShowLoading : WeekSequenceAction()
    object Clear : WeekSequenceAction()
}

class WeekSequenceViewModel @Inject constructor(
    private val interactor: WeekSequenceInteractor
) : BaseViewModel<WeekSequenceIntent, WeekSequenceAction, WeekSequenceState>() {

    var router: WeekSequenceRouter? = null

    private val showLoadingFlow = mutableSharedFlow<Unit>()

    init {
        viewModelScope.launch {
            val initFlow = intentFlow
                .filterIsInstance<WeekSequenceIntent.InitData>()
                .take(1)
                .onEach { showLoadingFlow.emit(Unit) }
                .share(this)

            val loadWeekSequenceFlow = initFlow
                .map { interactor.getWeekSequence() }
                .share(this)

            val firstCreateDayForEditWindowsFlow = intentFlow
                .filterIsInstance<WeekSequenceIntent.EditWindows>()
                .flatMapConcat {
                    loge(1)
                    val selectedDay = getLastState().selectedDay
                        ?: return@flatMapConcat emptyFlow()

                    loge(2)
                    val sequence = getLastState().weekSequence
                    val oldDayVersion = sequence
                        .find { it.day == selectedDay.day }
                        ?: return@flatMapConcat emptyFlow()

                    loge(3)
                    if (
                        oldDayVersion.startAt != selectedDay.startAt ||
                        oldDayVersion.finishAt != selectedDay.finishAt
                    ) flowOf(Unit).updateDay(false).onEach {
                        loge(4) }
                    else flowOf(selectedDay).onEach {
                        loge(5) }
                }
                .share(this)

            firstCreateDayForEditWindowsFlow
                .onEach { router?.openEditWorkdayWindows(it.day.index) }
                .launchHere()

            val updateAfterFirstCreatingAction = firstCreateDayForEditWindowsFlow
                .map { WeekSequenceAction.UpdateSelectedDay(it) }

            val updateWorkDaySequenceAction = intentFlow
                .filterIsInstance<WeekSequenceIntent.UpdateDay>()
                .updateDay().map { interactor.getWeekSequence() }
                .share(this)

            val updateWeekSequenceAction = merge(
                loadWeekSequenceFlow,
                updateWorkDaySequenceAction,
            ).flatMapConcat {
                if (it == null) emptyFlow()
                else flowOf(
                    WeekSequenceAction.UpdateWeekSequence(it),
                    WeekSequenceAction.UpdateSelectedDay(null)
                )
            }

            val openWorkDaySequenceEditBottomSheetAction = intentFlow
                .filterIsInstance<WeekSequenceIntent.DayClicked>()
                .flatMapConcat { intent ->
                    val day = getLastState().weekSequence
                        .find { it.day.index == intent.dayIndex }

                    if (day == null) emptyFlow()
                    else flowOf(
                        WeekSequenceAction.UpdateSelectedDay(day)
                    )
                }

            val showTimePickerAction = intentFlow
                .filterIsInstance<WeekSequenceIntent.DayTimeClicked>()
                .flatMapConcat {
                    val day = getLastState().selectedDay ?: return@flatMapConcat emptyFlow()
                    val timeSource = day.timeSource(it.type)
                    flowOf(
                        WeekSequenceAction.ShowTimePicker(timeSource),
                        WeekSequenceAction.Clear,
                    )
                }

            val updateSelectedDayTimeByPickedTimeAction = intentFlow
                .filterIsInstance<WeekSequenceIntent.TimePicked>()
                .flatMapConcat {
                    val day = getLastState().selectedDay ?: return@flatMapConcat emptyFlow()
                    val updatedDay = day.updateByTimeSource(it.source)
                    flowOf(WeekSequenceAction.UpdateSelectedDay(updatedDay))
                }

            val updateHolidayDayAction = intentFlow
                .filterIsInstance<WeekSequenceIntent.DayHolidayChanged>()
                .debounce(300)
                .flatMapConcat {
                    val day = getLastState().selectedDay ?: return@flatMapConcat emptyFlow()
                    val updatedDay = day.copy(isHoliday = it.isHoliday)
                    flowOf(WeekSequenceAction.UpdateSelectedDay(updatedDay))
                }

            val slowLoadingAction = showLoadingFlow
                .map { WeekSequenceAction.ShowLoading }

            merge(
                slowLoadingAction,
                showTimePickerAction,
                updateHolidayDayAction,
                updateWeekSequenceAction,
                updateAfterFirstCreatingAction,
                updateSelectedDayTimeByPickedTimeAction,
                openWorkDaySequenceEditBottomSheetAction
            ).collectState()

        }.addTo(workComposite)
    }

    override fun initialState() = WeekSequenceState()

    override fun reduce(
        state: WeekSequenceState,
        action: WeekSequenceAction
    ): WeekSequenceState = when (action) {
        is WeekSequenceAction.Clear -> state.copy(
            timeForShowTimePicker = null
        )
        is WeekSequenceAction.UpdateWeekSequence -> state.copy(
            weekSequence = action.sequenceWeek,
            loading = false,
        )
        is WeekSequenceAction.UpdateSelectedDay -> state.copy(
            selectedDay = action.day
        )
        is WeekSequenceAction.ShowTimePicker -> state.copy(
            timeForShowTimePicker = action.time
        )
        WeekSequenceAction.ShowLoading -> state.copy(
            loading = true
        )
    }

    private fun <T> Flow<T>.updateDay(showLoading: Boolean = true) = this
        .flatMapConcat { getLastState().selectedDay?.let(::flowOf) ?: emptyFlow() }
        .flatMapConcat { dayToUpdate ->
            if (showLoading) showLoadingFlow.emit(Unit)
            interactor.updateWorkDaySequence(dayToUpdate)
                ?.let { flowOf(it).onEach { loge("day updated") } }
                ?: emptyFlow()
        }
}
