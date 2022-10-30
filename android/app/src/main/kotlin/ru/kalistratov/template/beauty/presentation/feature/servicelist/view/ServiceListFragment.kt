package ru.kalistratov.template.beauty.presentation.feature.servicelist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.databinding.FragmentListServiceBinding
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.feature.servicelist.ServiceListState
import ru.kalistratov.template.beauty.presentation.feature.servicelist.ServiceListViewModel
import ru.kalistratov.template.beauty.presentation.feature.servicelist.di.ServiceListModule
import javax.inject.Inject

sealed interface ServiceListIntent : BaseIntent {
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
    private val controller = ServiceListController()

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

        with(binding.recycler) {
            adapter = controller.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.connectInto(this)
    }

    override fun intents(): Flow<ServiceListIntent> = merge(
        controller.serviceClicks.map { ServiceListIntent.InitData },
        binding.upBar.backButton.clicks().map { ServiceListIntent.BackPressed }
    )

    override fun render(state: ServiceListState) {
        controller.let {
            it.services = state.groups
            it.requestModelBuild()
        }
    }
}