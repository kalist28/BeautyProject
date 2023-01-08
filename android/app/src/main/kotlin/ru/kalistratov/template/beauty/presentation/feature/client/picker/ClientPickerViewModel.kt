package ru.kalistratov.template.beauty.presentation.feature.client.picker

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.feature.client.picker.ClientPickerInteractor
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.clickDebounce
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.client.picker.view.ClientPickerIntent
import javax.inject.Inject

data class ClientPickerState(
    val clients: List<Client> = emptyList()
) : BaseState

sealed interface ClientPickerAction : BaseAction {
    data class UpdateClients(val clients: List<Client>) : ClientPickerAction
}

class ClientPickerViewModel @Inject constructor(
    private val interactor: ClientPickerInteractor
) : BaseViewModel<ClientPickerIntent, ClientPickerAction, ClientPickerState>(),
    ViewModelLoadingSupport by ViewModelLoadingSupportBaseImpl() {

    var router: ClientPickerRouter? = null

    init {
        viewModelScope.launch {
            val initialFlow = intentFlow
                .filterIsInstance<ClientPickerIntent.InitData>()
                .take(1)
                .share(this)

            val loadAllClientsFlow = initialFlow.map {
                showLoading()
                interactor.getAll()
            }

            val searchClientsFlow = intentFlow
                .filterIsInstance<ClientPickerIntent.SearchTextChanged>()
                .drop(1)
                .onEach { if (!isLoadingShown()) showLoading() }
                .clickDebounce(500)
                .map { interactor.search(it.text) }

            val updateClientsAction = merge(loadAllClientsFlow, searchClientsFlow)
                .map (ClientPickerAction::UpdateClients)

            intentFlow.filterIsInstance<ClientPickerIntent.ClientClicked>()
                .onEach {
                    interactor.postClient(it.id)
                    router?.back()
                }
                .launchHere()

            merge(
                updateClientsAction
            ).collectState()

        }.addTo(workComposite)
    }

    override fun reduce(
        state: ClientPickerState,
        action: ClientPickerAction
    ) = when (action) {
        is ClientPickerAction.UpdateClients -> state.copy(
            clients = action.clients
        )
    }

    override fun initialState() = ClientPickerState()
}
