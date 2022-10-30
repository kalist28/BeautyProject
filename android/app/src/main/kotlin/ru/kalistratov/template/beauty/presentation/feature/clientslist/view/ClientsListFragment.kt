package ru.kalistratov.template.beauty.presentation.feature.clientslist.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentListClientsBinding
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.presentation.feature.clientslist.ClientsListState
import ru.kalistratov.template.beauty.presentation.feature.clientslist.ClientsListViewModel
import ru.kalistratov.template.beauty.presentation.feature.clientslist.di.ClientsListModule
import javax.inject.Inject

sealed interface ClientsListIntent : BaseIntent {
    object InitData : ClientsListIntent
}

class ClientsListFragment : BaseFragment(), BaseView<ClientsListIntent, ClientsListState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ClientsListViewModel::class.java]
    }

    private lateinit var binding: FragmentListClientsBinding

    private val clientsController = ClientsController()

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(ClientsListModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentListClientsBinding
        .inflate(inflater)
        .let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getString(R.string.clients))

        with(binding.recycler) {
            adapter = clientsController.adapter
            val isTablet = resources.getBoolean(R.bool.isTablet)
            val orientationPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            layoutManager = if (isTablet)
                GridLayoutManager(
                    requireContext(),
                    if (orientationPortrait) 2 else 3,
                )
                else LinearLayoutManager(requireContext())
        }

        viewModel.connectInto(this)
    }

    override fun intents(): Flow<ClientsListIntent> = merge(
        flowOf(ClientsListIntent.InitData)
    )

    override fun render(state: ClientsListState) {
        with(clientsController) {
            clients = state.clients
            requestModelBuild()
        }
    }
}