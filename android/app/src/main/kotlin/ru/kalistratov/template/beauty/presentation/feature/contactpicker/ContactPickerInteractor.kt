package ru.kalistratov.template.beauty.presentation.feature.contactpicker

import ru.kalistratov.template.beauty.domain.entity.Contact
import ru.kalistratov.template.beauty.domain.feature.contactpicker.ContactPickerInteractor
import ru.kalistratov.template.beauty.domain.repository.ContactsRepository
import javax.inject.Inject

class ContactPickerInteractorImpl @Inject constructor(
    private val contactsRepository: ContactsRepository
) : ContactPickerInteractor {
    override suspend fun getAll(): List<Contact> = contactsRepository.getAll()
}