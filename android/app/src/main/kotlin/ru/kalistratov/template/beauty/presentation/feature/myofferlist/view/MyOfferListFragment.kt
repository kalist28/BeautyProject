package ru.kalistratov.template.beauty.presentation.feature.myofferlist.view

import android.os.Bundle
import android.view.*
import androidx.core.view.iterator
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.*
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentMyOfferListBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.presentation.entity.PriceContainer
import ru.kalistratov.template.beauty.presentation.extension.showLoading
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.MyOfferListRouter
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.MyOfferListState
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.MyOfferListViewModel
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.di.MyOfferListModule
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.CreatingClickType
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.MyOfferListViewTypeState
import javax.inject.Inject

sealed interface MyOfferListIntent : BaseIntent {
    data class CreatingClicks(val type: CreatingClickType) : MyOfferListIntent
    data class PriceSelected(val selection: PriceContainer) : MyOfferListIntent
    data class SelectType(val index: Int) : MyOfferListIntent
    data class SelectTypeProperty(val index: Int) : MyOfferListIntent
    data class EditItem(val id: Id) : MyOfferListIntent
    data class DescriptionChanged(val text: String) : MyOfferListIntent

    object NeedDeleteItem : MyOfferListIntent
    data class DeleteItemClick(val delete: Boolean) : MyOfferListIntent

    object CreateOfferItem : MyOfferListIntent
    object SaveOfferItem : MyOfferListIntent
    object ShowList : MyOfferListIntent
}

class MyOfferListFragment : BaseFragment(), BaseView<MyOfferListIntent, MyOfferListState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var myOfferListRouter: MyOfferListRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MyOfferListViewModel::class.java]
    }

    private val binding: FragmentMyOfferListBinding by viewBinding(CreateMethod.INFLATE)

    private val controller by lazy { MyOfferListController(requireContext()) }

    private val intentsFlow = mutableSharedFlow<MyOfferListIntent>()

    private val createOfferItemClicks = mutableSharedFlow<Unit>()

    private var viewState: MyOfferListViewTypeState =
        MyOfferListViewTypeState.ListItems(emptyList())

    private var selectDialogShown: Boolean = false
    private var deleteItemDialogShown: Boolean = false

    override fun injectUserComponent(userComponent: UserComponent) {
        userComponent.plus(MyOfferListModule(this)).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root
        .also { setHasOptionsMenu(true) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.requireActivity()
        setAppBar(R.string.my_offers)
        binding.recycler.apply {
            adapter = controller.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        with(viewModel) {
            connectInto(this@MyOfferListFragment)
            router = myOfferListRouter

            loadingUpdates()
                .onEach(::showLoading)
                .launchIn(viewModelScope)
        }
    }

    override fun onAppBarBackPressed() {
        if (viewState !is MyOfferListViewTypeState.ListItems)
            intentsFlow.tryEmit(MyOfferListIntent.ShowList)
        else myOfferListRouter.back()
    }

    override fun appBarMenu() = R.menu.menu_offer_item_list

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.iterator().forEach { it.isVisible = false }
        val addOfferItem = menu.findItem(R.id.create)
        val remove = menu.findItem(R.id.remove)

        when (viewState) {
            is MyOfferListViewTypeState.ListItems -> {
                updateAppBarTitle(R.string.my_offers)
                addOfferItem.isVisible = true
            }
            is MyOfferListViewTypeState.CreatingItem -> {
                updateAppBarTitle(R.string.offer_create)
            }
            is MyOfferListViewTypeState.EditingItem -> {
                updateAppBarTitle(R.string.offer_edit)
                remove.isVisible = true
            }
        }
    }

    override fun onAppBarMenuItemClick(item: MenuItem): Boolean = when (item.itemId) {
        R.id.create -> createOfferItemClicks.tryEmit(Unit)
        R.id.remove -> intentsFlow.tryEmit(MyOfferListIntent.NeedDeleteItem)
        else -> super.onOptionsItemSelected(item)
    }

    override fun intents(): Flow<MyOfferListIntent> = merge(
        intentsFlow,
        controller.itemClicks().map(MyOfferListIntent::EditItem),
        controller.saveClicks().map { MyOfferListIntent.SaveOfferItem },
        controller.creatingClicks().map(MyOfferListIntent::CreatingClicks),
        controller.priceSelections().map(MyOfferListIntent::PriceSelected),
        controller.descriptionUpdates().map(MyOfferListIntent::DescriptionChanged),
        createOfferItemClicks.map { MyOfferListIntent.CreateOfferItem }
    )

    override fun render(state: MyOfferListState) {
        viewState = state.viewTypeState
        if (state.invalidateOptionMenu) requireActivity().invalidateOptionsMenu()

        if (state.showSelectTypePropertyDialog) {
            val viewState = state.viewTypeState
            if (viewState is MyOfferListViewTypeState.CreatingItem) {
                val names = viewState.type?.properties
                    ?.map { it.name } ?: emptyList()
                showSelectDialog(names, MyOfferListIntent::SelectTypeProperty)
            }
        }

        if (state.showSelectTypeDialog) {
            val viewState = state.viewTypeState
            if (viewState is MyOfferListViewTypeState.CreatingItem) {
                val names = state.typesForSelecting.map { it.name }
                showSelectDialog(names, MyOfferListIntent::SelectType)
            }
        }

        controller.apply {
            this.state = state.viewTypeState
            requestModelBuild()
        }

        if (state.itemIdForDelete != null && !deleteItemDialogShown)
            showConfirmItemDeleteSelectDialog()
    }

    private fun showSelectDialog(
        names: List<String>,
        clickAction: (Int) -> MyOfferListIntent
    ) {
        if (names.isEmpty() || selectDialogShown) return
        MaterialAlertDialogBuilder(requireContext())
            .setCancelable(true)
            .setOnCancelListener { selectDialogShown = false }
            .setItems(names.toTypedArray()) { dialog, index ->
                intentsFlow.tryEmit(clickAction(index))
                dialog.dismiss()
                selectDialogShown = false
            }
            .show()
        selectDialogShown = true
    }

    private fun showConfirmItemDeleteSelectDialog() =
        MaterialAlertDialogBuilder(requireContext())
            .setCancelable(false)
            .setTitle(R.string.offer_description)
            .setPositiveButton(R.string.confirm) { _, _ -> pushDeleteClick(true) }
            .setNegativeButton(R.string.cancel) { _, _ -> pushDeleteClick(false) }
            .show()
            .also { deleteItemDialogShown = true }

    private fun pushDeleteClick(delete: Boolean) {
        intentsFlow.tryEmit(MyOfferListIntent.DeleteItemClick(delete))
        deleteItemDialogShown = false
    }
}