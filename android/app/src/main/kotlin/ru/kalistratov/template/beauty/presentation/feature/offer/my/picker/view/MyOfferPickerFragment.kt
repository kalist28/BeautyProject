package ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentListBaseBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.MyOfferPickerRouter
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.MyOfferPickerState
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.MyOfferPickerViewModel
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.di.MyOfferPickerModule
import javax.inject.Inject

sealed interface MyOfferPickerIntent : BaseIntent {
    data class CategoryClick(val id: Id) : MyOfferPickerIntent
    data class ItemClick(val id: Id) : MyOfferPickerIntent

    object BackPressed : MyOfferPickerIntent
    object InitData : MyOfferPickerIntent
}

class MyOfferPickerFragment : BaseFragment(), BaseView<MyOfferPickerIntent, MyOfferPickerState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var offerPickerRouter: MyOfferPickerRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MyOfferPickerViewModel::class.java]
    }

    private val binding: FragmentListBaseBinding by viewBinding(CreateMethod.INFLATE)

    private val controller by lazy {
        MyOfferPickerController(requireContext(), ::changeRecycleLayoutManager)
    }

    private val gridLayoutManager by lazy {
        GridLayoutManager(requireContext(), 2)
    }

    private var selectedCategory: Id? = null

    override fun injectUserComponent(userComponent: UserComponent) {
        userComponent.plus(MyOfferPickerModule(this)).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar(R.string.slt_offer_type)

        with(binding.recycler) {
            adapter = controller.adapter
            layoutManager = gridLayoutManager
        }

        viewModel.apply {
            router = offerPickerRouter
            connectDialogLoadingDisplay()
            connectInto(this@MyOfferPickerFragment)
        }
    }

    override fun onAppBarBackPressed() {
        if (selectedCategory == null) offerPickerRouter.back()
    }

    override fun intents(): Flow<MyOfferPickerIntent> = merge(
        flowOf(MyOfferPickerIntent.InitData),
        backPressedFlow.map { MyOfferPickerIntent.BackPressed },
        controller.itemsClicks().map(MyOfferPickerIntent::ItemClick),
        controller.categoryClicks().map(MyOfferPickerIntent::CategoryClick),
    )

    override fun render(state: MyOfferPickerState) {
        selectedCategory = state.selectedCategory
        controller.apply {
            containers = state.containers
            selectedCategory = state.selectedCategory
            requestModelBuild()
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