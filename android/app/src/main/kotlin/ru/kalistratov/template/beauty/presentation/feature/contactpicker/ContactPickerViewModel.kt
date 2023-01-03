package ru.kalistratov.template.beauty.presentation.feature.contactpicker

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.Contact
import ru.kalistratov.template.beauty.domain.feature.contactpicker.ContactPickerBroadcast
import ru.kalistratov.template.beauty.domain.feature.contactpicker.ContactPickerInteractor
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.clickDebounce
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.view.ContactPickerIntent
import javax.inject.Inject

data class ContactPickerState(
    val contacts: List<Contact> = emptyList()
) : BaseState

sealed interface ContactPickerAction : BaseAction {
    data class UpdateClients(val contacts: List<Contact>) : ContactPickerAction
}

class ContactPickerViewModel @Inject constructor(
    private val interactor: ContactPickerInteractor,
    private val pickerBroadcast: ContactPickerBroadcast,
) : BaseViewModel<ContactPickerIntent, ContactPickerAction, ContactPickerState>(),
    ViewModelLoadingSupport by ViewModelLoadingSupportBaseImpl() {

    private val initialState = ContactPickerState()

    var router: ContactPickerRouter? = null

    init {
        viewModelScope.launch {

            val initDataFlow = intentFlow
                .filterIsInstance<ContactPickerIntent.InitData>()
                .share(this, replay = 1)

            val updateClientsAction = initDataFlow
                .map {
                    showLoading()
                    val contacts = interactor.getAll()
                    hideLoading()
                    ContactPickerAction.UpdateClients(contacts)
                }

            intentFlow.filterIsInstance<ContactPickerIntent.ContactClick>()
                .clickDebounce()
                .onEach { intent ->
                    state.contacts.find { it.id == intent.id }
                        ?.let(pickerBroadcast::post)
                    router?.back()
                }
                .launchHere()

            merge(updateClientsAction)
                .collectState()
        }
    }

    override fun initialState() = ContactPickerState()

    override fun reduce(
        state: ContactPickerState,
        action: ContactPickerAction
    ): ContactPickerState = when (action) {
        is ContactPickerAction.UpdateClients -> state.copy(
            contacts = action.contacts
        )
    }
}