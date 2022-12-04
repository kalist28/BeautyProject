package ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows

import androidx.lifecycle.viewModelScope
import com.soywiz.klock.Time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.domain.entity.TimeSource
import ru.kalistratov.template.beauty.domain.feature.editworkdaywindows.EditWorkdayWindowsInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.notNullFlow
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.infrastructure.extensions.noTime
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.view.EditSequenceDayWindowsIntent
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.view.State
import javax.inject.Inject

data class EditWorkdayWindowsState(
    val day: SequenceDay = SequenceDay.emptyDay,
    val toTime: Time = noTime,
    val fromTime: Time = noTime,
    val canAddWindow: Boolean = false,
    val selectedWindow: SequenceDayWindow? = null,
    val selectedWindows: List<Id> = emptyList(),
    val displayState: State = State.LIST,
    val timeForShowTimePicker: TimeSource? = null,

    val loading: Boolean = true,
    val needInvalidateOptionMenu: Boolean = false,
) : BaseState

sealed class EditWorkdayWindowsAction : BaseAction {
    data class UpdateToTime(val time: Time) : EditWorkdayWindowsAction()
    data class UpdateFromTime(val time: Time) : EditWorkdayWindowsAction()
    data class CreateWindow(val window: SequenceDayWindow) : EditWorkdayWindowsAction()
    data class UpdateSelectedWindow(val window: SequenceDayWindow) : EditWorkdayWindowsAction()
    data class UpdateCanAddWindow(val can: Boolean) : EditWorkdayWindowsAction()
    data class UpdateDay(val day: SequenceDay) : EditWorkdayWindowsAction()
    data class UpdateSelectedWindows(val list: List<Id>) : EditWorkdayWindowsAction()
    data class UpdateLoading(val loading: Boolean) : EditWorkdayWindowsAction()
    data class ShowTimePicker(val time: TimeSource?) : EditWorkdayWindowsAction()

    data class UpdateDisplayState(val state: State) : EditWorkdayWindowsAction()

    object UpdateOptionMenu : EditWorkdayWindowsAction()
    object Clear : EditWorkdayWindowsAction()
}

class EditWorkdayWindowsViewModel @Inject constructor(
    private val interactor: EditWorkdayWindowsInteractor,
) : BaseViewModel<EditSequenceDayWindowsIntent, EditWorkdayWindowsAction, EditWorkdayWindowsState>() {

    init {
        viewModelScope.launch {

            intentFlow.onEach { loge(it) }.launchHere()

            val initDataFlow = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.InitData>()
                .take(1)
                .share(this, replay = 1)

            val updateDaySequenceIdAction = initDataFlow
                .flatMapConcat {
                    val result = interactor.getSequenceDay(it.dayNumber)
                    val loadingAction = EditWorkdayWindowsAction.UpdateLoading(false)
                    if (result == null) flowOf(loadingAction)
                    else flowOf(
                        EditWorkdayWindowsAction.UpdateDay(result),
                        loadingAction
                    )
                }
                .flowOn(Dispatchers.IO)

            val pushWindowFlow = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.PushWindow>()
                .debounce(300)
                .flatMapConcat { notNullFlow(getLastState().selectedWindow) }
                .share(this)

            val updatedWindowsAction = pushWindowFlow.flatMapConcat {
                val window = interactor.pushWindow(it) ?: return@flatMapConcat emptyFlow()
                val windows = interactor.updateWindowList(window, getDayWindows())
                val updatedDay = getDay().copy(windows = windows)
                flowOf(
                    EditWorkdayWindowsAction.UpdateDay(updatedDay),
                    EditWorkdayWindowsAction.UpdateDisplayState(State.LIST),
                    EditWorkdayWindowsAction.UpdateLoading(false),
                )
            }

            val exitEditorAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.ExitEditor>()
                .map { EditWorkdayWindowsAction.UpdateDisplayState(State.LIST) }

            val updateSelectedWindowAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.WindowClick>()
                .flatMapConcat { intent ->
                    notNullFlow(getDayWindows().find { it.id == intent.id })
                }
                .flatMapConcat {
                    flowOf(
                        EditWorkdayWindowsAction.UpdateSelectedWindow(it),
                        EditWorkdayWindowsAction.UpdateDisplayState(State.EDIT),
                    )
                }

            val showAddWindowDialogAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.AddWindowDialogClick>()
                .flatMapConcat {
                    flowOf(
                        EditWorkdayWindowsAction.Clear
                    )
                }

            val updateDisplayStateAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.WindowListStateChanged>()
                .flatMapConcat {
                    flowOf(
                        EditWorkdayWindowsAction.UpdateDisplayState(it.state),
                        EditWorkdayWindowsAction.UpdateOptionMenu,
                        EditWorkdayWindowsAction.Clear
                    )
                }

            val updateSelectedWindowsAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.SelectedWindowsUpdated>()
                .map { EditWorkdayWindowsAction.UpdateSelectedWindows(it.list) }

            val removeWindowsFlow = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.RemoveWindows>()
                .map { getLastState().selectedWindows }
                .share(this, replay = 1)

            val removeWindowFlow = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.RemoveWindow>()
                .map { getLastState().selectedWindow?.let { listOf(it.id) } ?: emptyList() }
                .share(this, replay = 1)

            val afterWindowsRemoveActions = merge(
                removeWindowFlow,
                removeWindowsFlow
            ).flatMapConcat { ids ->
                val updatedDay = interactor.removeWindows(ids, getDay())
                flowOf(
                    EditWorkdayWindowsAction.UpdateDay(updatedDay),
                    EditWorkdayWindowsAction.UpdateSelectedWindows(emptyList()),
                    EditWorkdayWindowsAction.UpdateDisplayState(State.LIST),
                    EditWorkdayWindowsAction.UpdateLoading(false),
                )
            }

            val showLoadingAction = merge(
                pushWindowFlow,
                removeWindowFlow,
                removeWindowsFlow,
            ).map {
                loge("5695789689")
                EditWorkdayWindowsAction.UpdateLoading(true) }

            val createWindowAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.CreateWindow>()
                .flatMapConcat {
                    flowOf(
                        EditWorkdayWindowsAction.CreateWindow(interactor.createNewWindow(getDay())),
                        EditWorkdayWindowsAction.UpdateDisplayState(State.CREATE),
                        EditWorkdayWindowsAction.Clear
                    )
                }

            val updateWindowByPickedTimeAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.TimePicked>()
                .flatMapConcat {
                    val window = getLastState().selectedWindow ?: return@flatMapConcat emptyFlow()
                    flowOf(
                        EditWorkdayWindowsAction.UpdateSelectedWindow(
                            window.updateByTimeSource(it.source)
                        )
                    )
                }

            val showTimePickerAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.TimeClicked>()
                .flatMapConcat {
                    val timeSource = getLastState().selectedWindow?.timeSource(it.type)
                    flowOf(
                        EditWorkdayWindowsAction.ShowTimePicker(timeSource),
                        EditWorkdayWindowsAction.Clear,
                    )
                }

            merge(
                exitEditorAction,
                showLoadingAction,
                createWindowAction,
                showTimePickerAction,
                updatedWindowsAction,
                showAddWindowDialogAction,
                updateDaySequenceIdAction,
                afterWindowsRemoveActions,
                updateSelectedWindowAction,
                updateDisplayStateAction,
                updateSelectedWindowsAction,
                updateWindowByPickedTimeAction,
            ).collectState()
        }.addTo(workComposite)
    }

    override fun initialState() = EditWorkdayWindowsState()

    override fun reduce(
        state: EditWorkdayWindowsState,
        action: EditWorkdayWindowsAction
    ): EditWorkdayWindowsState = when (action) {
        is EditWorkdayWindowsAction.UpdateToTime -> state.copy(
            toTime = action.time
        )
        is EditWorkdayWindowsAction.UpdateFromTime -> state.copy(
            fromTime = action.time
        )
        is EditWorkdayWindowsAction.UpdateDay -> state.copy(
            day = action.day
        )
        is EditWorkdayWindowsAction.UpdateCanAddWindow -> state.copy(
            canAddWindow = action.can
        )
        is EditWorkdayWindowsAction.UpdateSelectedWindow -> state.copy(
            selectedWindow = action.window
        )
        is EditWorkdayWindowsAction.Clear -> state.copy(
            timeForShowTimePicker = null,
            needInvalidateOptionMenu = false,
        )
        is EditWorkdayWindowsAction.UpdateDisplayState -> state.copy(
            displayState = action.state,
            needInvalidateOptionMenu = true,
        )
        is EditWorkdayWindowsAction.UpdateSelectedWindows -> state.copy(
            selectedWindows = action.list
        )
        EditWorkdayWindowsAction.UpdateOptionMenu -> state.copy(
            needInvalidateOptionMenu = true
        )
        is EditWorkdayWindowsAction.UpdateLoading -> state.copy(
            loading = action.loading
        )
        is EditWorkdayWindowsAction.CreateWindow -> state.copy(
            selectedWindow = action.window
        )
        is EditWorkdayWindowsAction.ShowTimePicker -> state.copy(
            timeForShowTimePicker = action.time
        )
    }.also { loge("${action.javaClass.simpleName} -- ${state.loading}") }

    private fun getDay() = getLastState().day
    private fun getDayWindows() = getDay().windows
}
