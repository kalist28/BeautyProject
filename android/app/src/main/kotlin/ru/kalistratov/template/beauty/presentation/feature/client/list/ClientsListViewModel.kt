package ru.kalistratov.template.beauty.presentation.feature.client.list

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.feature.clientslist.ClientsListInteractor
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.presentation.feature.client.list.view.ClientsListIntent
import javax.inject.Inject

data class ClientsListState(
    val clients: List<Client> = emptyList()
) : BaseState

sealed interface ClientsListAction : BaseAction {
    data class UpdateClients(val clients: List<Client>) : ClientsListAction
}

class ClientsListViewModel @Inject constructor(
    private val interactor: ClientsListInteractor
) : BaseViewModel<ClientsListIntent, ClientsListAction, ClientsListState>(),
    ViewModelLoadingSupport by ViewModelLoadingSupportBaseImpl() {

    private val initialState = ClientsListState()

    var router: ClientsListRouter? = null

    init {
        viewModelScope.launch {

            val initDataFlow = intentFlow
                .filterIsInstance<ClientsListIntent.InitData>()
                .share(this, replay = 1)

            val updateClientsAction = initDataFlow.map {
                showLoading()
                ClientsListAction.UpdateClients(interactor.getClients())
                    .also { hideLoading() }
            }

            intentFlow.filterIsInstance<ClientsListIntent.ClientClick>()
                .onEach { router?.toEdit(it.id) }
                .launchHere()

            merge(
                updateClientsAction
            ).flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .collect(stateFlow)
        }
    }

    override fun reduce(
        state: ClientsListState,
        action: ClientsListAction
    ): ClientsListState = when (action) {
        is ClientsListAction.UpdateClients -> state.copy(
            clients = action.clients
        )
    }
}