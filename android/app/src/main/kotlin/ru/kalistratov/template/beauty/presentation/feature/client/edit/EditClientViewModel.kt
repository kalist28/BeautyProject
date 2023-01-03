package ru.kalistratov.template.beauty.presentation.feature.client.edit

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.exist
import ru.kalistratov.template.beauty.domain.feature.client.edit.EditClientInteractor
import ru.kalistratov.template.beauty.domain.feature.contactpicker.ContactPickerBroadcast
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.clickDebounce
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.infrastructure.extensions.process
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.presentation.feature.client.edit.view.EditClientIntent
import javax.inject.Inject

data class EditClientState(
    val client: Client? = null,
) : BaseState

sealed interface EditClientAction : BaseAction {
    data class UpdateClient(val client: Client?) : EditClientAction
}

class EditClientViewModel @Inject constructor(
    private val helper: EditClientHelper,
    private val interactor: EditClientInteractor,
    private val pickerBroadcast: ContactPickerBroadcast,
) : BaseViewModel<EditClientIntent, EditClientAction, EditClientState>(),
    ViewModelLoadingSupport by ViewModelLoadingSupportBaseImpl(),
    ViewModelNotificationSupport by ViewModelNotificationSupportBaseImpl() {

    var router: EditClientRouter? = null

    init {
        viewModelScope.launch {

            pickerBroadcast.selections()
                .onEach {
                    helper.setContact(it)
                    helper.requestUpdate()
                }
                .launchHere()

            intentFlow.filterIsInstance<EditClientIntent.InitData>()
                .onEach { intent ->
                    showLoading()
                    val client = intent.id
                        ?.let {
                            when (val result = interactor.get(it)) {
                                is NetworkResult.Success -> result.value.toLocal()
                                is NetworkResult.GenericError -> {
                                    router?.back()
                                    return@onEach
                                }
                            }
                        }
                        ?: Client.EMPTY
                    helper.apply {
                        setClient(client)
                        requestUpdate()
                    }
                }
                .launchHere()

            intentFlow.filterIsInstance<EditClientIntent.DataChanges>()
                .clickDebounce()
                .onEach { helper.postChange(it.change) }
                .launchHere()

            intentFlow.filterIsInstance<EditClientIntent.ToPicker>()
                .clickDebounce()
                .onEach { router?.toPicker() }
                .launchHere()

            intentFlow
                .filterIsInstance<EditClientIntent.Save>()
                .clickDebounce()
                .onEach {
                    showLoading()
                    val client = helper.client
                    val numberExist = !client.id.exist() && interactor
                        .isNumberExist(client.number)
                    if (numberExist) post(
                        ViewNotification.Toast(
                            "Номер существует",
                            true
                        )
                    ) else {
                        interactor.save(helper.client).process(
                            success = { router?.back() },
                            error = { }
                        )
                    }
                    hideLoading()
                }
                .share(this)

            val clientUpdatesAction = helper.clientUpdates()
                .onEach { loge(it) }
                .map(EditClientAction::UpdateClient)

            merge(
                clientUpdatesAction
            ).collectState()

        }.addTo(workComposite)
    }

    override fun initialState() = EditClientState()

    override fun reduce(
        state: EditClientState,
        action: EditClientAction
    ): EditClientState = when (action) {
        is EditClientAction.UpdateClient -> state.copy(
            client = action.client
        )
    }
}