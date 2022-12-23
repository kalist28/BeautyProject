package ru.kalistratov.template.beauty.presentation.feature.offerpicker

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.feature.servicelist.OfferPickerInteractor
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.view.OfferPickerIntent
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.infrastructure.coroutines.clickDebounce
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.entity.OfferPickerType
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.view.OfferPickerFragment
import javax.inject.Inject

data class OfferPickerState(
    val pickerType: OfferPickerType = OfferPickerType.Category,
    val categories: List<OfferCategory> = emptyList(),
    val selected: List<Id> = emptyList(),
    val loading: Boolean = true,
) : BaseState

sealed interface OfferPickerAction : BaseAction {
    data class UpdateSelected(val selected: List<Id>) : OfferPickerAction
    data class UpdateServices(val categories: List<OfferCategory>) : OfferPickerAction
    data class UpdatePickerType(val type: OfferPickerType) : OfferPickerAction
    data class UpdateLoading(val show: Boolean) : OfferPickerAction
}

class OfferPickerViewModel @Inject constructor(
    private val interactor: OfferPickerInteractor
) : BaseViewModel<OfferPickerIntent, OfferPickerAction, OfferPickerState>() {

    var router: OfferPickerRouter? = null

    private val selectedClicks = mutableSharedFlow<Any>()
    private val loadingUpdates = mutableSharedFlow<Boolean>()

    private val turnOffLoadingAction = OfferPickerAction.UpdateLoading(false)

    init {
        viewModelScope.launch {

            val initialFlow = intentFlow
                .filterIsInstance<OfferPickerIntent.InitData>()
                .clickDebounce(500)
                .share(this, 1)

            val initSelectedAction = initialFlow
                .flatMapConcat {
                    val type = it.pickerType
                    if (type is OfferPickerType.Type) {
                        flowOf(
                            OfferPickerAction.UpdateSelected(
                                interactor.updateSelectedList(
                                    type.id,
                                    false,
                                    state.selected
                                )
                            ),
                            turnOffLoadingAction
                        )
                    } else emptyFlow()
                }

            val updatePickerTypeAction = initialFlow
                .map { OfferPickerAction.UpdatePickerType(it.pickerType) }

            val initialCategories = initialFlow
                .flatMapConcat {
                    flowOf(
                        OfferPickerAction.UpdateServices(interactor.loadCategories()),
                        turnOffLoadingAction
                    )
                }

            val updateSelectedAction = intentFlow
                .filterIsInstance<OfferPickerIntent.CategoryClick>()
                .flatMapConcat { intent ->
                    if (state.pickerType == OfferPickerType.Category) {
                        interactor.getCategory(intent.id)
                            ?.let { selectedClicks.emit(it) }
                        return@flatMapConcat emptyFlow()
                    }

                    val selected = interactor.updateSelectedList(
                        intent.id,
                        intent.fromCrumbs,
                        state.selected
                    )
                    val nesting = interactor.getNestedCategory(selected)
                    flowOf(
                        OfferPickerAction.UpdateSelected(selected),
                        OfferPickerAction.UpdateServices(nesting)
                    )
                }

            intentFlow.filterIsInstance<OfferPickerIntent.BackPressed>()
                .clickDebounce()
                .onEach { router?.back() }
                .launchHere()

            val typeClicksFlow = intentFlow
                .filterIsInstance<OfferPickerIntent.TypeClick>()
                .mapNotNull { interactor.getType(it.id) }

            merge(selectedClicks, typeClicksFlow)
                .onEach { loadingUpdates.emit(true) }
                .debounce(300)
                .onEach {
                    OfferPickerFragment.typeSelectedFlow.emit(it)
                    router?.back()
                }
                .launchHere()

            val updateLoadingAction = loadingUpdates
                .map(OfferPickerAction::UpdateLoading)

            merge(
                initialCategories,
                initSelectedAction,
                updateLoadingAction,
                updateSelectedAction,
                updatePickerTypeAction
            ).collectState()
        }.addTo(workComposite)
    }

    override fun initialState() = OfferPickerState()

    override fun reduce(
        state: OfferPickerState,
        action: OfferPickerAction
    ): OfferPickerState = when (action) {
        is OfferPickerAction.UpdateServices -> state.copy(
            categories = action.categories
        )
        is OfferPickerAction.UpdateSelected -> state.copy(
            selected = action.selected
        )
        is OfferPickerAction.UpdatePickerType -> state.copy(
            pickerType = action.type
        )
        is OfferPickerAction.UpdateLoading -> state.copy(
            loading = action.show
        )
    }


}