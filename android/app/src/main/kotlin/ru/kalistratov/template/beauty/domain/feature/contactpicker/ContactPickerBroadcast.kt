package ru.kalistratov.template.beauty.domain.feature.contactpicker

import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.domain.entity.Contact

interface ContactPickerBroadcast {
    fun post(contact: Contact)
    fun selections(): Flow<Contact>
}