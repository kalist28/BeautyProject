package ru.kalistratov.template.beauty.presentation.feature.offerlist

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.feature.servicelist.ServiceListInteractor
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.feature.offerlist.view.ServiceListIntent
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import javax.inject.Inject

data class ServiceListState(
    val categories: List<OfferCategory> = emptyList(),
    val selected: List<Id> = emptyList()
) : BaseState

sealed interface ServiceListAction : BaseAction {
    data class UpdateSelected(val selected: List<Id>) : ServiceListAction
    data class UpdateServices(val categories: List<OfferCategory>) : ServiceListAction
}

class ServiceListViewModel @Inject constructor(
    private val router: ServiceListRouter,
    private val interactor: ServiceListInteractor
) : BaseViewModel<ServiceListIntent, ServiceListAction, ServiceListState>() {

    init {
        viewModelScope.launch {
            val initialCategories = flowOf(
                ServiceListAction.UpdateServices(interactor.loadCategories())
            ).share(this, replay = 1)

            val updateSelectedAction = intentFlow
                .filterIsInstance<ServiceListIntent.CategoryClick>()
                .flatMapConcat { intent ->
                    val selected = interactor.updateSelectedList(
                        intent.id,
                        intent.fromCrumbs,
                        getLastState().selected
                    )
                    val nesting = interactor.getNestedCategory(selected)
                    flowOf(
                        ServiceListAction.UpdateSelected(selected),
                        ServiceListAction.UpdateServices(nesting)
                    )
                }

            intentFlow.filterIsInstance<ServiceListIntent.BackPressed>()
                .onEach { router.back() }
                .launchHere()

            merge(
                initialCategories,
                updateSelectedAction
            ).collectState()
        }.addTo(workComposite)
    }

    override fun initialState() = ServiceListState()

    override fun reduce(
        state: ServiceListState,
        action: ServiceListAction
    ): ServiceListState = when (action) {
        is ServiceListAction.UpdateServices -> state.copy(
            categories = action.categories
        )
        is ServiceListAction.UpdateSelected -> state.copy(
            selected = action.selected
        )
    }


}