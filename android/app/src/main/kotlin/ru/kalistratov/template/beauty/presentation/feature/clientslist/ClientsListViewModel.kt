package ru.kalistratov.template.beauty.presentation.feature.clientslist

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.Contact
import ru.kalistratov.template.beauty.domain.feature.clientslist.ClientsListInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.clientslist.view.ClientsListIntent
import javax.inject.Inject

data class ClientsListState(
    val clients: List<Contact> = emptyList()
) : BaseState

sealed interface ClientsListAction : BaseAction {
    data class UpdateClients(val clients: List<Contact>): ClientsListAction
}

class ClientsListViewModel @Inject constructor(
    private val interactor: ClientsListInteractor
) : BaseViewModel<ClientsListIntent, ClientsListAction, ClientsListState>() {

    private val initialState = ClientsListState()

    init {
        viewModelScope.launch {

            intentFlow.onEach { loge(it.javaClass.simpleName) }.launchIn(this)

            val updateClientsAction = intentFlow.filterIsInstance<ClientsListIntent.InitData>()
                .map { ClientsListAction.UpdateClients(interactor.getClients()) }

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
    ): ClientsListState = when(action) {
        is ClientsListAction.UpdateClients -> state.copy(
            clients = action.clients
        )
    }
}