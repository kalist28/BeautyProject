package ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows

import androidx.lifecycle.viewModelScope
import com.soywiz.klock.Time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.domain.extension.noTime
import ru.kalistratov.template.beauty.domain.feature.editworkdaywindows.EditWorkdayWindowsInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.notNullFlow
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.view.EditSequenceDayWindowsIntent
import javax.inject.Inject

data class EditWorkdayWindowsState(
    val day: SequenceDay = SequenceDay.emptyDay,
    val toTime: Time = noTime,
    val fromTime: Time = noTime,
    val canAddWindow: Boolean = false,
    val selectedWindow: SequenceDayWindow? = null,

    val showAddWindowDialog: Boolean = false
) : BaseState

sealed class EditWorkdayWindowsAction : BaseAction {
    data class UpdateToTime(val time: Time) : EditWorkdayWindowsAction()
    data class UpdateFromTime(val time: Time) : EditWorkdayWindowsAction()
    data class UpdateSelectedWindow(val window: SequenceDayWindow) : EditWorkdayWindowsAction()
    data class UpdateCanAddWindow(val can: Boolean) : EditWorkdayWindowsAction()
    data class UpdateDay(val sequence: SequenceDay) : EditWorkdayWindowsAction()

    object ShowAddWindowDialog : EditWorkdayWindowsAction()
    object Clear : EditWorkdayWindowsAction()
}

class EditWorkdayWindowsViewModel @Inject constructor(
    private val router: EditWorkdayWindowsRouter,
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
                .share(this)

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

            intentFlow.filterIsInstance<EditSequenceDayWindowsIntent.BackPressed>()
                .onEach { router.back() }
                .launchIn(this)
                .addTo(workComposite)

            merge(
                showAddWindowDialogAction,
                updateDaySequenceIdAction,
                updateSelectedWindowAction,
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
            day = action.sequence
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

    private fun getDayWindows() = _stateFlow.value.day.windows
}
