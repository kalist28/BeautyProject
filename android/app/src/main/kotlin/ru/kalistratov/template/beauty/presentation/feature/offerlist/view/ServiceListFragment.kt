package ru.kalistratov.template.beauty.presentation.feature.offerlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.*
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentListServiceBinding
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.presentation.feature.offerlist.ServiceListState
import ru.kalistratov.template.beauty.presentation.feature.offerlist.ServiceListViewModel
import ru.kalistratov.template.beauty.presentation.feature.offerlist.di.ServiceListModule
import ru.kalistratov.template.beauty.domain.entity.Id
import javax.inject.Inject

sealed interface ServiceListIntent : BaseIntent {
    data class CategoryClick(val id: Id, val fromCrumbs: Boolean): ServiceListIntent

    object InitData: ServiceListIntent
    object BackPressed: ServiceListIntent
}

class ServiceListFragment : BaseFragment(), BaseView<ServiceListIntent, ServiceListState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ServiceListViewModel::class.java]
    }

    private lateinit var binding: FragmentListServiceBinding
    private val controller = ServiceListController(::changeRecycleLayoutManager)

    override fun injectUserComponent(userComponent: UserComponent) {
        userComponent.plus(ServiceListModule(this)).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentListServiceBinding
        .inflate(inflater, container, false)
        .let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAppBar(getString(R.string.app_name))

        with(binding.recycler) {
            adapter = controller.adapter
        }

        viewModel.connectInto(this)
    }

    override fun intents(): Flow<ServiceListIntent> = merge(
        controller.categoryClicks.map { ServiceListIntent.CategoryClick(it, false) },
        binding.breadCrumbs.getClickUpdates().map { ServiceListIntent.CategoryClick(it, true) }
    )

    override fun render(state: ServiceListState) {
        binding.breadCrumbs.update(
            if (state.selected.isEmpty()) emptyList()
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
        val newManager = when(isEmpty) {
            true -> GridLayoutManager(requireContext(), 3)
            false -> LinearLayoutManager(requireContext())
        }
        if (oldManager != newManager) {
            recycler.removeAllViewsInLayout()
            recycler.layoutManager = newManager
        }
    }
}