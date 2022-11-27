package ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows

import androidx.lifecycle.viewModelScope
import com.soywiz.klock.Time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
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
import ru.kalistratov.template.beauty.presentation.view.workdaywindows.IdSelector
import javax.inject.Inject

data class EditWorkdayWindowsState(
    val day: SequenceDay = SequenceDay.emptyDay,
    val toTime: Time = noTime,
    val fromTime: Time = noTime,
    val canAddWindow: Boolean = false,
    val selectedWindow: SequenceDayWindow? = null,
    val selectedWindows: List<Id> = emptyList(),
    val windowsListState: IdSelector.State = IdSelector.State.LIST,

    val loading: Boolean = false,
    val showAddWindowDialog: Boolean = false,
    val needInvalidateOptionMenu: Boolean = false,
) : BaseState

sealed class EditWorkdayWindowsAction : BaseAction {
    data class UpdateToTime(val time: Time) : EditWorkdayWindowsAction()
    data class UpdateFromTime(val time: Time) : EditWorkdayWindowsAction()
    data class UpdateSelectedWindow(val window: SequenceDayWindow) : EditWorkdayWindowsAction()
    data class UpdateCanAddWindow(val can: Boolean) : EditWorkdayWindowsAction()
    data class UpdateDay(val day: SequenceDay) : EditWorkdayWindowsAction()
    data class UpdateSelectedWindows(val list: List<Id>) : EditWorkdayWindowsAction()
    data class UpdateLoading(val loading: Boolean) : EditWorkdayWindowsAction()

    data class UpdateWindowListState(
        val state: IdSelector.State
    ) : EditWorkdayWindowsAction()

    object ShowAddWindowDialog : EditWorkdayWindowsAction()
    object UpdateOptionMenu : EditWorkdayWindowsAction()
    object Clear : EditWorkdayWindowsAction()
}

class EditWorkdayWindowsViewModel @Inject constructor(
    private val interactor: EditWorkdayWindowsInteractor,
) : BaseViewModel<EditSequenceDayWindowsIntent, EditWorkdayWindowsAction, EditWorkdayWindowsState>() {

    private val initialState = EditWorkdayWindowsState()
    private val _stateFlow = MutableStateFlow(initialState)

    private val sortWindowsComparator = Comparator<SequenceDayWindow> { day1, day2 ->
        day1.startAt.compareTo(day2.startAt)
    }

    init {
        viewModelScope.launch {

            val initDataFlow = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.InitData>()
                .take(1)
                .share(this, replay = 1)

            val updateDaySequenceIdAction = initDataFlow
                .flatMapConcat {
                    val result = interactor.getSequenceDay(it.dayNumber)
                    if (result == null) emptyFlow()
                    else flowOf(EditWorkdayWindowsAction.UpdateDay(result))
                }
                .flowOn(Dispatchers.IO)

            val saveWindowFlow = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.AddWindow>()
                .map {
                    val state = _stateFlow.value
                    val request = it.window.copy(sequenceDayId = state.day.id)
                    interactor.pushWindow(request)
                }
                .flowOn(Dispatchers.IO)
                .filterNotNull()
                .map { window ->
                    getDayWindows().toMutableList()
                        .also {
                            it.add(window)
                            it.sortedWith(sortWindowsComparator)
                        }
                }

            val updateWindowFlow = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.UpdateWindow>()
                .map { interactor.pushWindow(it.window) }
                .flowOn(Dispatchers.IO)
                .filterNotNull()
                .map { window ->
                    getDayWindows().toMutableList()
                        .also { list ->
                            list.indexOfFirst { it.id == window.id }.let {
                                list.removeAt(it)
                                list.add(it, window)
                            }
                        }
                }

            val updatedDayAfterWindowsUpdatedAction = merge(
                saveWindowFlow,
                updateWindowFlow
            ).map { windows ->
                val updatedDay = _stateFlow.value.day.copy(windows = windows)
                EditWorkdayWindowsAction.UpdateDay(updatedDay)
            }

            val updateSelectedWindowAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.WindowClick>()
                .flatMapConcat { intent ->
                    notNullFlow(getDayWindows().find { it.id == intent.id })
                }
                .flatMapConcat {
                    flowOf(
                        EditWorkdayWindowsAction.UpdateSelectedWindow(it),
                        EditWorkdayWindowsAction.ShowAddWindowDialog,
                        EditWorkdayWindowsAction.Clear
                    )
                }

            val showAddWindowDialogAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.AddWindowDialogClick>()
                .flatMapConcat {
                    flowOf(
                        EditWorkdayWindowsAction.ShowAddWindowDialog,
                        EditWorkdayWindowsAction.Clear
                    )
                }

            val updateWindowListStateAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.WindowListStateChanged>()
                .flatMapConcat {
                    flowOf(
                        EditWorkdayWindowsAction.UpdateWindowListState(it.state),
                        EditWorkdayWindowsAction.UpdateOptionMenu,
                        EditWorkdayWindowsAction.Clear
                    )
                }

            val updateSelectedWindowsAction = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.SelectedWindowsUpdated>()
                .map { EditWorkdayWindowsAction.UpdateSelectedWindows(it.list) }

            val removeWindowsFlow = intentFlow
                .filterIsInstance<EditSequenceDayWindowsIntent.RemoveWindows>()
                .share(this, replay = 1)

            val afterWindowsRemoveActions = removeWindowsFlow
                .flatMapConcat {
                    val ids = state().selectedWindows
                    loge("BEFORE - ${getDay().windows.map { it.id }}")
                    val updatedDay = interactor.removeWindows(ids, getDay())
                    loge("Updated -  ${updatedDay.windows.map { it.id }}")
                    flowOf(
                        EditWorkdayWindowsAction.UpdateDay(updatedDay),
                        EditWorkdayWindowsAction.UpdateSelectedWindows(emptyList()),
                        EditWorkdayWindowsAction.UpdateLoading(false),
                    )
                }

            val showLoadingAction = merge(
                removeWindowsFlow
            ).map { EditWorkdayWindowsAction.UpdateLoading(true) }

            merge(
                showLoadingAction,
                showAddWindowDialogAction,
                updateDaySequenceIdAction,
                afterWindowsRemoveActions,
                updateSelectedWindowAction,
                updateWindowListStateAction,
                updateSelectedWindowsAction,
                updatedDayAfterWindowsUpdatedAction
            )
                .flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .onEach {
                    _stateFlow.value = it
                }
                .collect(stateFlow)
        }.addTo(workComposite)
    }

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
        is EditWorkdayWindowsAction.ShowAddWindowDialog -> state.copy(
            showAddWindowDialog = true
        )
        is EditWorkdayWindowsAction.UpdateSelectedWindow -> state.copy(
            selectedWindow = action.window
        )
        is EditWorkdayWindowsAction.Clear -> state.copy(
            selectedWindow = null,
            showAddWindowDialog = false,
            needInvalidateOptionMenu = false,
        )
        is EditWorkdayWindowsAction.UpdateWindowListState -> state.copy(
            windowsListState = action.state
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
    }

    private fun state() = _stateFlow.value
    private fun getDay() = _stateFlow.value.day
    private fun getDayWindows() = _stateFlow.value.day.windows
}
