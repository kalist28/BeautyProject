package ru.kalistratov.template.beauty.presentation.feature.myofferlist

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.entity.OfferItem
import ru.kalistratov.template.beauty.domain.entity.OfferType
import ru.kalistratov.template.beauty.domain.feature.myofferlist.MyOfferListInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModelWithLoadingSupport
import ru.kalistratov.template.beauty.infrastructure.coroutines.*
import ru.kalistratov.template.beauty.presentation.entity.toContainer
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.CreatingClickType
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.MyOfferListViewTypeState
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.view.MyOfferListIntent
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.entity.OfferPickerType
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.view.OfferPickerFragment
import javax.inject.Inject

data class MyOfferListState(
    val offerItems: List<OfferItem> = emptyList(),
    val viewTypeState: MyOfferListViewTypeState = MyOfferListViewTypeState.ListItems(emptyList()),
    val creatingClickType: CreatingClickType = CreatingClickType.SelectCategory,
    val invalidateOptionMenu: Boolean = false,
    val typesForSelecting: List<OfferType> = emptyList(),
    val showSelectTypeDialog: Boolean = false,
    val showSelectTypePropertyDialog: Boolean = false,
    val itemIdForDelete: Id? = null
) : BaseState

sealed interface MyOfferListAction : BaseAction {
    data class UpdateViewTypeState(val state: MyOfferListViewTypeState) : MyOfferListAction
    data class UpdateCreatingClickType(val type: CreatingClickType) : MyOfferListAction
    data class UpdateTypesForSelecting(val types: List<OfferType>) : MyOfferListAction
    data class UpdateShowingSelectTypeDialog(val show: Boolean) : MyOfferListAction
    data class UpdateShowingSelectTypePropertyDialog(val show: Boolean) : MyOfferListAction

    data class UpdateItemIdForDelete(val id: Id?) : MyOfferListAction

    object InvalidateOptionMenu : MyOfferListAction
    object Clear : MyOfferListAction
}

class MyOfferListViewModel @Inject constructor(
    private val interactor: MyOfferListInteractor
) : BaseViewModelWithLoadingSupport<MyOfferListIntent, MyOfferListAction, MyOfferListState>() {

    var router: MyOfferListRouter? = null

    private val showListFlow = mutableSharedFlow<Unit>()

    init {
        viewModelScope.launch {
            val initFlow = flowOf(Unit).share(this, replay = 1)

            val loadOfferItemsAction = merge(
                initFlow, intentFlow.filterIsInstance<MyOfferListIntent.ShowList>()
            ).flatMapConcat {
                showLoading()
                flow {
                    emit(
                        MyOfferListAction.UpdateViewTypeState(
                            MyOfferListViewTypeState.ListItems(
                                interactor.getOfferCategoryContainers()
                            )
                        )
                    )
                    emit(MyOfferListAction.InvalidateOptionMenu)
                    emit(MyOfferListAction.Clear)
                    hideLoading()
                }
            }

            val updatePriceSelectionAction = intentFlow
                .filterIsInstance<MyOfferListIntent.PriceSelected>()
                .clickDebounce()
                .flatMapConcat {
                    flow {
                        when (val state = state.viewTypeState) {
                            is MyOfferListViewTypeState.CreatingItem -> emit(
                                MyOfferListAction.UpdateViewTypeState(
                                    state.copy(priceContainer = it.selection)
                                )
                            )
                            is MyOfferListViewTypeState.EditingItem -> emit(
                                MyOfferListAction.UpdateViewTypeState(
                                    state.copy(priceContainer = it.selection)
                                )
                            )
                            else -> Unit
                        }
                    }
                }

            val updateDescriptionAction = intentFlow
                .filterIsInstance<MyOfferListIntent.DescriptionChanged>()
                .clickDebounce()
                .flatMapConcat {
                    flow {
                        when (val state = state.viewTypeState) {
                            is MyOfferListViewTypeState.CreatingItem -> emit(
                                MyOfferListAction.UpdateViewTypeState(
                                    state.copy(description = it.text)
                                )
                            )
                            is MyOfferListViewTypeState.EditingItem -> emit(
                                MyOfferListAction.UpdateViewTypeState(
                                    state.copy(description = it.text)
                                )
                            )
                            else -> Unit
                        }
                    }
                }

            val updateSelectedCategoryAction = merge(
                OfferPickerFragment.typeSelectedFlow,
                intentFlow.filterIsInstance<MyOfferListIntent.SelectType>(),
                intentFlow.filterIsInstance<MyOfferListIntent.SelectTypeProperty>(),
            ).clickDebounce().flatMapConcat { intent ->
                flow {
                    showLoading()
                    val typeState = state.viewTypeState
                    if (typeState is MyOfferListViewTypeState.CreatingItem) {
                        val updatedState = updateCreatingState(typeState, intent)
                        emit(MyOfferListAction.UpdateViewTypeState(updatedState))
                    }
                    hideLoading()
                }
            }

            val editItemAction = intentFlow
                .filterIsInstance<MyOfferListIntent.EditItem>()
                .clickDebounce()
                .flatMapConcat { intent ->
                    flow {
                        showLoading()
                        val item = interactor.getOfferItem(intent.id)
                            ?: hideLoading().let { return@flow }
                        val category = interactor.getOfferCategory(item.type.categoryId)
                            ?: hideLoading().let { return@flow }

                        emit(
                            MyOfferListAction.UpdateViewTypeState(
                                MyOfferListViewTypeState.EditingItem(
                                    category.title,
                                    item,
                                    item.price.toContainer(),
                                    item.description
                                )
                            )
                        )
                        emit(MyOfferListAction.InvalidateOptionMenu)
                        emit(MyOfferListAction.Clear)
                        hideLoading()
                    }
                }

            val creatingClicksFlow =
                intentFlow.filterIsInstance<MyOfferListIntent.CreatingClicks>().map { it.type }
                    .share(this)

            val showSelectTypePropertyDialogAction =
                creatingClicksFlow.filterIsInstance<CreatingClickType.SelectTypeProperty>()
                    .alternative(MyOfferListAction::UpdateShowingSelectTypePropertyDialog)

            val showingSelectTypeDialogAction =
                creatingClicksFlow.filterIsInstance<CreatingClickType.SelectType>()
                    .flatMapConcat {
                        flow {
                            val viewState = state.viewTypeState
                            if (viewState !is MyOfferListViewTypeState.CreatingItem) return@flow
                            val categoryId = viewState.category?.id ?: return@flow
                            emit(
                                MyOfferListAction.UpdateTypesForSelecting(
                                    interactor.filterNotCreatingTypes(categoryId)
                                )
                            )
                            createAlternativeList(true).forEach {
                                emit(MyOfferListAction.UpdateShowingSelectTypeDialog(it))
                            }
                        }
                    }

            val updateLastCreatingClickTypeAction =
                creatingClicksFlow.map(MyOfferListAction::UpdateCreatingClickType)

            creatingClicksFlow.onEach {
                val viewState = state.viewTypeState
                if (viewState !is MyOfferListViewTypeState.CreatingItem) return@onEach
                router?.openOfferPicker(
                    when (it) {
                        CreatingClickType.SelectCategory -> OfferPickerType.Category
                        else -> return@onEach
                    }
                )
            }.launchHere()

            val createOfferItemAction = intentFlow
                .filterIsInstance<MyOfferListIntent.CreateOfferItem>()
                .flatMapConcat {
                    flow {
                        emit(
                            MyOfferListAction.UpdateViewTypeState(
                                MyOfferListViewTypeState.CreatingItem()
                            )
                        )
                        emit(MyOfferListAction.InvalidateOptionMenu)
                        emit(MyOfferListAction.Clear)

                        viewModelScope.launch(Dispatchers.Main) {
                            router?.openOfferPicker(OfferPickerType.Category)
                        }
                    }
                }

            intentFlow.filterIsInstance<MyOfferListIntent.SaveOfferItem>().onEach {
                showLoading()
                when (val state = state.viewTypeState) {
                    is MyOfferListViewTypeState.CreatingItem -> interactor.saveOfferItem(state)
                    is MyOfferListViewTypeState.EditingItem -> interactor.updateOfferItem(state)
                    else -> hideLoading().let { return@onEach }
                }
                showListFlow.emit(Unit)
            }.launchHere()

            val showListAction = showListFlow.flatMapConcat {
                flow {
                    emit(
                        MyOfferListAction.UpdateViewTypeState(
                            MyOfferListViewTypeState.ListItems(
                                interactor.getOfferCategoryContainers()
                            )
                        )
                    )
                    emit(MyOfferListAction.InvalidateOptionMenu)
                    emit(MyOfferListAction.Clear)
                    hideLoading()
                }
            }

            val setItemIdForDeletingAction = intentFlow
                .filterIsInstance<MyOfferListIntent.NeedDeleteItem>()
                .flatMapConcat {
                    val viewState = state.viewTypeState
                    if (viewState !is MyOfferListViewTypeState.EditingItem)
                        return@flatMapConcat emptyFlow()
                    flowOf(MyOfferListAction.UpdateItemIdForDelete(viewState.offerItem.id))
                }

            val clearItemIdForDeletingAction = intentFlow
                .filterIsInstance<MyOfferListIntent.DeleteItemClick>()
                .flatMapConcat {
                    flow {
                        emit(MyOfferListAction.UpdateItemIdForDelete(null))
                        showLoading()

                        if (it.delete) state.itemIdForDelete?.let { id ->
                            interactor.removeOfferItem(id)
                            showListFlow.emit(Unit)
                        }
                        hideLoading()
                    }
                }

            merge(
                editItemAction,
                showListAction,
                loadOfferItemsAction,
                createOfferItemAction,
                updateDescriptionAction,
                updatePriceSelectionAction,
                updateSelectedCategoryAction,
                showingSelectTypeDialogAction,
                setItemIdForDeletingAction,
                clearItemIdForDeletingAction,
                updateLastCreatingClickTypeAction,
                showSelectTypePropertyDialogAction
            ).collectState()
        }.addTo(workComposite)
    }

    override fun initialState() = MyOfferListState()

    override fun reduce(
        state: MyOfferListState, action: MyOfferListAction
    ) = when (action) {
        is MyOfferListAction.UpdateViewTypeState -> state.copy(
            viewTypeState = action.state
        )
        is MyOfferListAction.UpdateCreatingClickType -> state.copy(
            creatingClickType = action.type
        )
        is MyOfferListAction.UpdateShowingSelectTypePropertyDialog -> state.copy(
            showSelectTypePropertyDialog = action.show
        )
        is MyOfferListAction.UpdateShowingSelectTypeDialog -> state.copy(
            showSelectTypeDialog = action.show
        )
        MyOfferListAction.Clear -> state.copy(
            invalidateOptionMenu = false
        )
        MyOfferListAction.InvalidateOptionMenu -> state.copy(
            invalidateOptionMenu = true
        )
        is MyOfferListAction.UpdateTypesForSelecting -> state.copy(
            typesForSelecting = action.types
        )
        is MyOfferListAction.UpdateItemIdForDelete -> state.copy(
            itemIdForDelete = action.id
        )
    }

    private suspend fun updateCreatingState(
        creatingState: MyOfferListViewTypeState.CreatingItem, intent: Any
    ) = when (intent) {
        is OfferCategory -> MyOfferListViewTypeState.CreatingItem(category = intent)
        is MyOfferListIntent.SelectType -> creatingState.let { state ->
            val id = creatingState.category?.types?.get(intent.index)?.id
                ?: return creatingState
            state.copy(type = interactor.getType(id), typeProperty = null)
        }
        is MyOfferListIntent.SelectTypeProperty -> creatingState.copy(
            typeProperty = creatingState.type?.properties?.get(intent.index)
        )
        is MyOfferListIntent.DescriptionChanged -> creatingState.copy(
            description = intent.text
        )
        is MyOfferListIntent.PriceSelected -> creatingState.copy(
            priceContainer = intent.selection
        )
        else -> creatingState
    }
}
