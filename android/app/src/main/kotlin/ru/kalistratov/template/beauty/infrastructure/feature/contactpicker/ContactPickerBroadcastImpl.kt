package ru.kalistratov.template.beauty.infrastructure.feature.contactpicker

import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.domain.entity.Contact
import ru.kalistratov.template.beauty.domain.feature.contactpicker.ContactPickerBroadcast
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import javax.inject.Inject

class ContactPickerBroadcastImpl @Inject constructor() : ContactPickerBroadcast {

    private val selectionsMutableFlow = mutableSharedFlow<Contact>()

    override fun post(contact: Contact) {
        selectionsMutableFlow.tryEmit(contact)
    }

    override fun selections() = selectionsMutableFlow.asSharedFlow()
}