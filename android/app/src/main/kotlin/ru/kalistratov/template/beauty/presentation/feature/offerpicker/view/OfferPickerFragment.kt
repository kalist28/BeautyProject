package ru.kalistratov.template.beauty.presentation.feature.offerpicker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.*
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentListServiceBinding
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.OfferPickerState
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.OfferPickerViewModel
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.di.OfferPickerModule
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.extension.showLoading
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.view.EditSequenceDayWindowsFragmentArgs
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.OfferPickerRouter
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.entity.OfferPickerType
import ru.kalistratov.template.beauty.presentation.view.LoadingAlertDialog
import javax.inject.Inject

sealed interface OfferPickerIntent : BaseIntent {
    data class TypeClick(val id: Id) : OfferPickerIntent
    data class CategoryClick(val id: Id, val fromCrumbs: Boolean) : OfferPickerIntent

    data class InitData(val pickerType: OfferPickerType) : OfferPickerIntent
    object BackPressed : OfferPickerIntent
}

class OfferPickerFragment : BaseFragment(), BaseView<OfferPickerIntent, OfferPickerState> {

    companion object {
        val typeSelectedFlow = mutableSharedFlow<Any>()

        inline fun <reified T> filteredSelections() = typeSelectedFlow
            .filterIsInstance<T>()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var offerPickerRouter: OfferPickerRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[OfferPickerViewModel::class.java]
    }

    private val binding: FragmentListServiceBinding by viewBinding(CreateMethod.INFLATE)
    private val controller = OfferPickerController(::changeRecycleLayoutManager)

    private val gridLayoutManager by lazy { GridLayoutManager(requireContext(), 3) }

    private val args: OfferPickerFragmentArgs by navArgs()

    private val pickerType by lazy { OfferPickerType.valueOf(args.pickerType, args.id) }

    private val loadingAlertDialog by lazy { LoadingAlertDialog(requireContext()) }

    override fun injectUserComponent(userComponent: UserComponent) {
        userComponent.plus(OfferPickerModule(this)).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAppBar(
            getString(
                when (pickerType) {
                    is OfferPickerType.Type -> R.string.slt_offer_type
                    is OfferPickerType.Category -> R.string.slt_offer_category
                    //is OfferPickerType.TypeProperty -> R.string.slt_offer_type_property
                }
            )
        )

        with(binding.recycler) {
            adapter = controller.adapter
            layoutManager = gridLayoutManager
        }

        viewModel.connectInto(this)
        viewModel.router = offerPickerRouter
    }

    override fun onAppBarBackPressed() = offerPickerRouter.back()

    override fun intents(): Flow<OfferPickerIntent> = merge(
        flowOf(OfferPickerIntent.InitData(pickerType)),
        controller.typeClicks.map(OfferPickerIntent::TypeClick),
        controller.categoryClicks.map { OfferPickerIntent.CategoryClick(it, false) },
        binding.breadCrumbs.getClickUpdates().map { OfferPickerIntent.CategoryClick(it, true) }
    )

    override fun render(state: OfferPickerState) {
        state.loading.let {
            showLoading(it)
            if (it) return
        }
        val emptyCategorySelection = state.selected.isEmpty() &&
                state.pickerType is OfferPickerType.Category
        binding.breadCrumbs.update(
            if (emptyCategorySelection) listOf("-1" to "Категории")
            else state.categories.map { it.id to it.title }
        )

        controller.let {
            it.selected = state.selected
            it.categories = state.categories
            it.requestModelBuild()
        }
    }

    private fun changeRecycleLayoutManager(isEmpty: Boolean) {
        val recycler = binding.recycler
        val oldManager = recycler.layoutManager
        val newManager = when (isEmpty) {
            true -> gridLayoutManager
            false -> LinearLayoutManager(requireContext())
        }
        if (oldManager != newManager) {
            recycler.removeAllViewsInLayout()
            recycler.layoutManager = newManager
        }
    }
}