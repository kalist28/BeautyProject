package ru.kalistratov.template.beauty.presentation.feature.client.picker.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentClientPickerBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.presentation.feature.client.picker.ClientPickerRouter
import ru.kalistratov.template.beauty.presentation.feature.client.picker.ClientPickerState
import ru.kalistratov.template.beauty.presentation.feature.client.picker.ClientPickerViewModel
import ru.kalistratov.template.beauty.presentation.feature.client.picker.di.ClientPickerModule
import javax.inject.Inject

sealed interface ClientPickerIntent : BaseIntent {
    data class ClientClicked(val id: Id) : ClientPickerIntent
    data class SearchTextChanged(val text: String) : ClientPickerIntent

    object InitData : ClientPickerIntent
}

class ClientPickerFragment : BaseFragment(), BaseView<ClientPickerIntent, ClientPickerState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var router: ClientPickerRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ClientPickerViewModel::class.java]
    }

    private val binding: FragmentClientPickerBinding by viewBinding(CreateMethod.INFLATE)

    private val searchTextMutableFlow = mutableSharedFlow<String>()

    private val controller = ClientPickerController()

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(ClientPickerModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar(R.string.client_pick)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = controller.adapter
            holdersAdapter = ClientPickerShimmerAdapter()
        }

        with(viewModel) {
            router = this@ClientPickerFragment.router

            controller.buildFinishedUpdates()
                .onEach { hideLoading() }
                .launchIn(viewModelScope)

            viewModelScope.launch {
                setLoadingProcessor(this, binding.recycler::toggleHoldersAdapter)
            }.addTo(jobComposite)

            connectInto(this@ClientPickerFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        (menu.findItem(R.id.search).actionView as SearchView).setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String) =
                    searchTextMutableFlow.tryEmit(newText)

                override fun onQueryTextSubmit(query: String?) = false
            }
        )
    }

    override fun appBarMenu(): Int = R.menu.client_picker

    override fun intents(): Flow<ClientPickerIntent> = merge(
        flowOf(ClientPickerIntent.InitData),
        controller.clicks().map(ClientPickerIntent::ClientClicked),
        searchTextMutableFlow.map(ClientPickerIntent::SearchTextChanged)
    )

    override fun render(state: ClientPickerState) {
        with(controller) {
            clients = state.clients
            requestModelBuild()
        }
    }
}