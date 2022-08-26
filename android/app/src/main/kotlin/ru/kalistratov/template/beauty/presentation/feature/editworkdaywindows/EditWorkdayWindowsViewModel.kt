package ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows

import androidx.lifecycle.viewModelScope
import com.soywiz.klock.Time
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.WorkdaySequence
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow
import ru.kalistratov.template.beauty.domain.extension.noTime
import ru.kalistratov.template.beauty.domain.feature.editworkdaywindows.EditWorkdayWindowsInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.notNullFlow
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.view.EditWorkdayWindowsIntent

data class EditWorkdayWindowsState(
    val workdaySequence: WorkdaySequence = WorkdaySequence(),
    val windows: List<WorkdayWindow> = emptyList(),
    val toTime: Time = noTime,
    val fromTime: Time = noTime,
    val canAddWindow: Boolean = false,
    val selectedWindow: WorkdayWindow? = null,

    val showAddWindowDialog: Boolean = false
) : BaseState

sealed class EditWorkdayWindowsAction : BaseAction {
    data class UpdateToTime(val time: Time) : EditWorkdayWindowsAction()
    data class UpdateFromTime(val time: Time) : EditWorkdayWindowsAction()
    data class UpdateSelectedWindow(val window: WorkdayWindow) : EditWorkdayWindowsAction()
    data class UpdateCanAddWindow(val can: Boolean) : EditWorkdayWindowsAction()
    data class UpdateWorkdayWindows(val windows: List<WorkdayWindow>) : EditWorkdayWindowsAction()
    data class UpdateWorkdaySequence(val sequence: WorkdaySequence) : EditWorkdayWindowsAction()

    object ShowAddWindowDialog : EditWorkdayWindowsAction()
    object Clear : EditWorkdayWindowsAction()
}

class EditWorkdayWindowsViewModel @Inject constructor(
    private val router: EditWorkdayWindowsRouter,
    private val interactor: EditWorkdayWindowsInteractor,
) : BaseViewModel<EditWorkdayWindowsIntent, EditWorkdayWindowsAction, EditWorkdayWindowsState>() {

    private val initialState = EditWorkdayWindowsState()
    private val _stateFlow = MutableStateFlow(initialState)

    private val sortWindowsComparator = Comparator<WorkdayWindow> { day1, day2 ->
        day1.startAt.compareTo(day2.startAt)
    }

    init {
        viewModelScope.launch {

            val initDataFlow = intentFlow
                .filterIsInstance<EditWorkdayWindowsIntent.InitData>()
                .take(1)
                .share(this)

            val loadUserAction = initDataFlow
                .map {
                    EditWorkdayWindowsAction.UpdateWorkdayWindows(
                        interactor.getWindows().also { loge(it) }
                    )
                }
                .flowOn(Dispatchers.IO)

            val updateDaySequenceIdAction = initDataFlow
                .map {
                    EditWorkdayWindowsAction.UpdateWorkdaySequence(
                        interactor.getWorkdaySequence(it.daySequenceId.toLong())
                    )
                }
                .flowOn(Dispatchers.IO)

            val saveWindowAction = intentFlow
                .filterIsInstance<EditWorkdayWindowsIntent.AddWindow>()
                .flatMapConcat {
                    val state = _stateFlow.value
                    val request = it.window.copy(sequence_day = state.workdaySequence.day.index)
                    val result = interactor.createWindow(request)
                    if (result is NetworkResult.Success) flowOf(result.value)
                    else emptyFlow()
                }
                .flowOn(Dispatchers.IO)
                .map { window ->
                    val windows = _stateFlow.value.windows
                        .toMutableList()
                        .also {
                            it.add(window)
                            it.sortedWith(sortWindowsComparator)
                        }
                    EditWorkdayWindowsAction.UpdateWorkdayWindows(windows)
                }

            val updateWindowAction = intentFlow
                .filterIsInstance<EditWorkdayWindowsIntent.UpdateWindow>()
                .flatMapConcat {
                    loge(it.window)
                    val result = interactor.updateWindow(it.window)
                    loge(result)
                    if (result is NetworkResult.Success) flowOf(result.value)
                    else emptyFlow()
                }
                .flowOn(Dispatchers.IO)
                .map { window ->
                    val windows = _stateFlow.value.windows
                        .toMutableList()
                        .also { list ->
                            list.indexOfFirst { it.id == window.id }.let {
                                list.removeAt(it)
                                list.add(it, window)
                            }
                        }
                    EditWorkdayWindowsAction.UpdateWorkdayWindows(windows)
                }

            val updateSelectedWindowAction = intentFlow
                .filterIsInstance<EditWorkdayWindowsIntent.WindowClick>()
                .flatMapConcat { intent ->
                    notNullFlow(_stateFlow.value.windows.find { it.id == intent.id })
                }
                .flatMapConcat {
                    flowOf(
                        EditWorkdayWindowsAction.UpdateSelectedWindow(it),
                        EditWorkdayWindowsAction.ShowAddWindowDialog,
                        EditWorkdayWindowsAction.Clear
                    )
                }

            val showAddWindowDialogAction = intentFlow
                .filterIsInstance<EditWorkdayWindowsIntent.AddWindowDialogClick>()
                .flatMapConcat {
                    flowOf(
                        EditWorkdayWindowsAction.ShowAddWindowDialog,
                        EditWorkdayWindowsAction.Clear
                    )
                }

            intentFlow.filterIsInstance<EditWorkdayWindowsIntent.BackPressed>()
                .onEach { router.back() }
                .launchIn(this)
                .addTo(workComposite)

            merge(
                loadUserAction,
                saveWindowAction,
                updateWindowAction,
                showAddWindowDialogAction,
                updateDaySequenceIdAction,
                updateSelectedWindowAction
            )
                .flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .onEach {
                    _stateFlow.value = it
                    _stateFlow.emit(it)
                }
                .launchIn(this)
                .addTo(workComposite)
        }
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
        is EditWorkdayWindowsAction.UpdateWorkdaySequence -> state.copy(
            workdaySequence = action.sequence
        )
        is EditWorkdayWindowsAction.UpdateWorkdayWindows -> state.copy(
            windows = action.windows
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
        )
    }
}
