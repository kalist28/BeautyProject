package ru.kalistratov.template.beauty.presentation.feature.client.edit

import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.Contact
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.toNumbers
import ru.kalistratov.template.beauty.infrastructure.extensions.toStringNumbers
import ru.kalistratov.template.beauty.presentation.feature.client.edit.entity.ClientChange
import ru.kalistratov.template.beauty.presentation.feature.client.edit.entity.ClientKey.*
import javax.inject.Inject

class EditClientHelper @Inject constructor() {

    private var isInitialize = false

    var client: Client = Client.EMPTY
        private set

    private val clientUpdates = mutableSharedFlow<Client>()

    fun setClient(client: Client?) {
        if (isInitialize) return
        client?.let { this.client = it }
        isInitialize = true
    }

    fun setContact(contact: Contact) {
        val fullName = contact.name.split(" ")
        val name = fullName.first()
        val surname = fullName.getOrNull(1)
        client = client.copy(
            name = name,
            surname = surname,
            number = contact.number,
        )
    }

    fun clientUpdates() = clientUpdates.asSharedFlow()

    fun requestUpdate() {
        clientUpdates.tryEmit(client)
    }

    fun postChange(change: ClientChange) = with(change) {
        client = when (key) {
            NAME -> client.copy(name = value)
            SURNAME -> client.copy(surname = value)
            PATRONYMIC -> client.copy(patronymic = value)
            NUMBER -> client.copy(number = value.toStringNumbers())
            NOTE -> client.copy(note = value)
        }
    }
}