package ru.kalistratov.template.beauty.presentation.feature.client.list.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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
import ru.kalistratov.template.beauty.presentation.feature.client.list.ClientsListRouter
import ru.kalistratov.template.beauty.presentation.feature.client.list.ClientsListState
import ru.kalistratov.template.beauty.presentation.feature.client.list.ClientsListViewModel
import ru.kalistratov.template.beauty.presentation.feature.client.list.di.ClientsListModule
import javax.inject.Inject

sealed interface ClientsListIntent : BaseIntent {
    data class ClientClick(val id: Id) : ClientsListIntent
    object InitData : ClientsListIntent
}

class ClientsListFragment : BaseFragment(), BaseView<ClientsListIntent, ClientsListState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var clientsListRouter: ClientsListRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ClientsListViewModel::class.java]
    }

    private val binding: FragmentListBaseBinding by viewBinding(CreateMethod.INFLATE)

    private val clientsController = ClientsController()

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(ClientsListModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar(getString(R.string.clients))

        with(binding.recycler) {
            adapter = clientsController.adapter
            val isTablet = resources.getBoolean(R.bool.isTablet)
            val orientationPortrait =
                resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            layoutManager = if (isTablet)
                GridLayoutManager(
                    requireContext(),
                    if (orientationPortrait) 2 else 3,
                )
            else LinearLayoutManager(requireContext())
        }

        viewModel.apply {
            connectInto(this@ClientsListFragment)
            router = clientsListRouter
        }
    }

    override fun onAppBarBackPressed() = clientsListRouter.back()

    override fun onAppBarMenuItemClick(item: MenuItem) = when (item.itemId) {
        R.id.create -> clientsListRouter.toCreate().let { true }
        else -> super.onAppBarMenuItemClick(item)
    }

    override fun appBarMenu() = R.menu.client_list

    override fun intents(): Flow<ClientsListIntent> = merge(
        flowOf(ClientsListIntent.InitData),
        clientsController.clicks().map(ClientsListIntent::ClientClick)
    )

    override fun render(state: ClientsListState) {
        with(clientsController) {
            clients = state.clients
            requestModelBuild()
        }
    }
}