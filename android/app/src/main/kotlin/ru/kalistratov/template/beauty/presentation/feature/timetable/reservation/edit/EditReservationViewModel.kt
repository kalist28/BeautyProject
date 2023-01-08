package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit

import androidx.lifecycle.viewModelScope
import com.soywiz.klock.Date
import com.soywiz.klock.DateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.domain.feature.timetable.reservation.edit.EditReservationInteractor
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.clickDebounce
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.view.EditReservationIntent
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.view.EditReservationSingleAction
import javax.inject.Inject

data class EditReservationState(
    val sequenceWeek: SequenceWeek = emptyList(),
    val freeWindows: List<SequenceDayWindow> = emptyList(),
    val date: Date? = null,
    val client: Client? = null,
    val window: SequenceDayWindow? = null,
    val offerItem: OfferItem? = null,
    val offerItemCategory: OfferCategory? = null
) : BaseState

sealed interface EditReservationAction : BaseAction {
    data class UpdateDate(val date: Date?) : EditReservationAction
    data class UpdateClient(val client: Client?) : EditReservationAction
    data class UpdateOfferItem(val item: OfferItem?) : EditReservationAction
    data class UpdateOfferItemCategory(val category: OfferCategory?) : EditReservationAction
    data class UpdateSelectedFreeWindow(val window: SequenceDayWindow?) : EditReservationAction
    data class UpdateFreeWindows(val windows: List<SequenceDayWindow>) : EditReservationAction
    data class UpdateSequenceWeek(val week: SequenceWeek) : EditReservationAction
}

class EditReservationViewModel @Inject constructor(
    private val interactor: EditReservationInteractor
) : BaseViewModel<EditReservationIntent, EditReservationAction, EditReservationState>(),
    ViewModelLoadingSupport by ViewModelLoadingSupportBaseImpl(),
    SingleActionSupport<EditReservationSingleAction> by SingleActionSupportBaseImpl() {

    var router: EditReservationRouter? = null

    init {
        viewModelScope.launch {
            val initDataFlow = intentFlow
                .filterIsInstance<EditReservationIntent.InitData>()
                .share(this, 1)

            val loadSequenceWeekAction = initDataFlow.map {
                showLoading()
                EditReservationAction.UpdateSequenceWeek(
                    interactor.getSequenceWeek()
                )
            }.onEach { hideLoading() }

            //Show DatePicker
            intentFlow.filterIsInstance<EditReservationIntent.ShowDatePicker>()
                .clickDebounce()
                .onEach { post(EditReservationSingleAction.ShowDatePicker(state.sequenceWeek)) }
                .launchHere()

            val dateSelectedFlow = intentFlow
                .filterIsInstance<EditReservationIntent.DateSelected>()
                .map { DateTime(it.unix) }
                .share(this)

            val loadFreeWindowsFlow = dateSelectedFlow
                .map {
                    showLoading()
                    interactor.getFreeSequenceDayWindows(it.date)
                        .also { hideLoading() }
                }
                .share(this)

            val updateFreeWindows = loadFreeWindowsFlow
                .map(EditReservationAction::UpdateFreeWindows)

            val showSequenceDayWindowPickerFlow = intentFlow
                .filterIsInstance<EditReservationIntent.ShowSequenceDayWindowPicker>()
                .clickDebounce()
                .map { state.freeWindows }

            val updateOfferItemAction = interactor
                .getSelectedMyOfferFlow()
                .flatMapConcat {
                    showLoading()
                    val item = interactor.getOfferItem(it)
                    val category = item?.type?.categoryId
                        ?.run { interactor.getCategory(this) }
                    flowOf(
                        EditReservationAction.UpdateOfferItem(item),
                        EditReservationAction.UpdateOfferItemCategory(category)
                    ).onCompletion { hideLoading() }
                }

            val updateClientAction = interactor
                .getSelectedClientFlow()
                .map {
                    showLoading()
                    EditReservationAction.UpdateClient(interactor.getClient(it))
                }.onEach { hideLoading() }

            //Show WindowPickerDialog
            merge(loadFreeWindowsFlow, showSequenceDayWindowPickerFlow)
                .filter { it.isNotEmpty() }
                .onEach { windows ->
                    post(
                        EditReservationSingleAction.ShowFreeWindowsDialog(
                            windows.map { it.toContentTimeRange() }
                        )
                    )
                }
                .launchHere()

            intentFlow.filterIsInstance<EditReservationIntent.ShowOfferItemPicker>()
                .onEach { router?.toMyOfferPicker() }
                .launchHere()

            intentFlow.filterIsInstance<EditReservationIntent.ShowClientPicker>()
                .onEach { router?.toClientPicker() }
                .launchHere()

            val updateDateAction = dateSelectedFlow.map {
                EditReservationAction.UpdateDate(it.date)
            }

            val updateSelectedFreeWindowAction = intentFlow
                .filterIsInstance<EditReservationIntent.SequenceDayWindowSelected>()
                .map { EditReservationAction.UpdateSelectedFreeWindow(state.freeWindows[it.index]) }

            // saveReservationAction
            intentFlow.filterIsInstance<EditReservationIntent.SaveClick>()
                .clickDebounce()
                .onEach {
                    interactor
                }
                .launchHere()

            merge(
                updateDateAction,
                updateFreeWindows,
                updateClientAction,
                updateOfferItemAction,
                loadSequenceWeekAction,
                updateSelectedFreeWindowAction,
            ).collectState()
        }
    }

    override fun initialState() = EditReservationState()

    override fun reduce(
        state: EditReservationState,
        action: EditReservationAction
    ): EditReservationState = when (action) {
        is EditReservationAction.UpdateSequenceWeek -> state.copy(
            sequenceWeek = action.week
        )
        is EditReservationAction.UpdateDate -> state.copy(
            date = action.date,
            window = null
        )
        is EditReservationAction.UpdateFreeWindows -> state.copy(
            freeWindows = action.windows
        )
        is EditReservationAction.UpdateSelectedFreeWindow -> state.copy(
            window = action.window
        )
        is EditReservationAction.UpdateOfferItem -> state.copy(
            offerItem = action.item
        )
        is EditReservationAction.UpdateOfferItemCategory -> state.copy(
            offerItemCategory = action.category
        )
        is EditReservationAction.UpdateClient -> state.copy(
            client = action.client
        )
    }

}