package ru.kalistratov.template.beauty.presentation.feature.offer.my.picker

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.feature.offer.my.picker.MyOfferPickerInteractor
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.presentation.entity.OfferTypeContainer
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.view.MyOfferPickerIntent
import javax.inject.Inject

data class MyOfferPickerState(
    val containers: Map<OfferCategory, List<OfferTypeContainer>> = emptyMap(),
    val selectedCategory: Id? = null
) : BaseState

sealed interface MyOfferPickerAction : BaseAction {
    data class UpdateSelectedCategory(val id: Id?) : MyOfferPickerAction

    data class UpdateContainers(
        val containers: Map<OfferCategory, List<OfferTypeContainer>>
    ) : MyOfferPickerAction
}

class MyOfferPickerViewModel @Inject constructor(
    private val interactor: MyOfferPickerInteractor
) : BaseViewModel<MyOfferPickerIntent, MyOfferPickerAction, MyOfferPickerState>(),
    ViewModelLoadingSupport by ViewModelLoadingSupportBaseImpl() {

    var router: MyOfferPickerRouter? = null

    init {
        viewModelScope.launch {

            val initDataFlow = intentFlow.filterIsInstance<MyOfferPickerIntent.InitData>()
                .share(this, 1)

            val loadContainersAction = initDataFlow.map {
                showLoading()
                MyOfferPickerAction.UpdateContainers(interactor.getAllOfferItems())
                    .also { hideLoading() }
            }

            val clearCategoryIdFlow = intentFlow
                .filterIsInstance<MyOfferPickerIntent.BackPressed>()
                .map { null }

            val categorySlickedFlow = intentFlow
                .filterIsInstance<MyOfferPickerIntent.CategoryClick>()
                .map { it.id }

            val updateSelectedCategoryIdAction = merge(clearCategoryIdFlow, categorySlickedFlow)
                .map(MyOfferPickerAction::UpdateSelectedCategory)

            intentFlow.filterIsInstance<MyOfferPickerIntent.ItemClick>()
                .onEach {
                    showLoading()
                    interactor.postSelected(it.id)
                    router?.back()
                }
                .launchHere()

            merge(
                loadContainersAction,
                updateSelectedCategoryIdAction
            ).collectState()

        }.addTo(workComposite)
    }

    override fun initialState() = MyOfferPickerState()

    override fun reduce(
        state: MyOfferPickerState,
        action: MyOfferPickerAction
    ) = when (action) {
        is MyOfferPickerAction.UpdateContainers -> state.copy(
            containers = action.containers
        )
        is MyOfferPickerAction.UpdateSelectedCategory -> state.copy(
            selectedCategory = action.id
        )
    }
}