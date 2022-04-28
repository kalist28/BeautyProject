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
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.view.EditWorkdayWindowsIntent
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.TimePickerSpinnerBottomSheet.Companion.FROM_TIME_TAG
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.TimePickerSpinnerBottomSheet.Companion.TO_TIME_TAG

data class EditWorkdayWindowsState(
    val workdaySequence: WorkdaySequence = WorkdaySequence(),
    val windows: List<WorkdayWindow> = emptyList(),
    val toTime: Time = noTime,
    val fromTime: Time = noTime,
    val canAddWindow: Boolean = false,
) : BaseState

sealed class EditWorkdayWindowsAction : BaseAction {
    data class UpdateToTime(val time: Time) : EditWorkdayWindowsAction()
    data class UpdateFromTime(val time: Time) : EditWorkdayWindowsAction()
    data class UpdateCanAddWindow(val can: Boolean) : EditWorkdayWindowsAction()
    data class UpdateWorkdayWindows(val windows: List<WorkdayWindow>) : EditWorkdayWindowsAction()
    data class UpdateWorkdaySequence(val sequence: WorkdaySequence) : EditWorkdayWindowsAction()
}

class EditWorkdayWindowsViewModel @Inject constructor(
    private val interactor: EditWorkdayWindowsInteractor,
) : BaseViewModel<EditWorkdayWindowsIntent, EditWorkdayWindowsAction, EditWorkdayWindowsState>() {

    private val initialState = EditWorkdayWindowsState()
    private val stateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            val initDataFlow = intentFlow
                .filterIsInstance<EditWorkdayWindowsIntent.InitData>()
                .take(1)
                .share(this)

            val loadUserAction = initDataFlow.map {
                EditWorkdayWindowsAction.UpdateWorkdayWindows(
                    interactor.getWindows()
                )
            }

            val updateDaySequenceIdAction = initDataFlow.map {
                EditWorkdayWindowsAction.UpdateWorkdaySequence(
                    interactor.getWorkdaySequence(it.daySequenceId.toLong())
                )
            }

            val timePickerResultsFlow = intentFlow
                .filterIsInstance<EditWorkdayWindowsIntent.TimeSelected>()
                .map { it.result }
                .share(this)

            val updateToTimeAction = timePickerResultsFlow
                .filter { it.tag == TO_TIME_TAG }
                .map { EditWorkdayWindowsAction.UpdateToTime(it.time) }

            val updateFromTimeAction = timePickerResultsFlow
                .filter { it.tag == FROM_TIME_TAG }
                .map { EditWorkdayWindowsAction.UpdateFromTime(it.time) }

            val saveWindowResultAction = intentFlow
                .filterIsInstance<EditWorkdayWindowsIntent.SaveWindowClick>()
                .flatMapConcat {
                    val state = stateFlow.value
                    val request = WorkdayWindow(
                        sequence_day = state.workdaySequence.day.index,
                        finishAt = state.toTime,
                        startAt = state.fromTime
                    )
                    val result = interactor.createWindow(request)
                    loge(result)
                    if (result is NetworkResult.Success) flowOf(result.value)
                    else emptyFlow()
                }
                .map { window ->
                    val windows = stateFlow.value.windows.toMutableList()
                        .also { it.add(window) }
                    EditWorkdayWindowsAction.UpdateWorkdayWindows(windows)
                }

            val updateCanAddWindowAction = timePickerResultsFlow
                .map {
                    val state = stateFlow.value
                    val day = state.workdaySequence

                    val startAt = day.startAt
                    val finishAt = day.finishAt

                    val fromTime = state.fromTime
                    val toTime = state.toTime

                    val time = it.time
                    val checkRange = time > startAt && time < finishAt
                    val checkBorders = when (it.tag) {
                        TO_TIME_TAG -> time > fromTime
                        FROM_TIME_TAG -> time < toTime
                        else -> checkRange
                    }
                    EditWorkdayWindowsAction.UpdateCanAddWindow(checkRange and checkBorders)
                }

            merge(
                loadUserAction,
                updateToTimeAction,
                updateFromTimeAction,
                saveWindowResultAction,
                updateCanAddWindowAction,
                updateDaySequenceIdAction
            )
                .flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .onEach {
                    stateFlow.value = it
                    shareStateFlow.emit(it)
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
    }
}
