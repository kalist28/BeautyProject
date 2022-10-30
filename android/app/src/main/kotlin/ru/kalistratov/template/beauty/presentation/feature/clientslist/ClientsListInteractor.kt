package ru.kalistratov.template.beauty.presentation.feature.clientslist

import ru.kalistratov.template.beauty.domain.entity.Contact
import ru.kalistratov.template.beauty.domain.feature.clientslist.ClientsListInteractor
import ru.kalistratov.template.beauty.domain.repository.ContactsRepository

class ClientsListInteractorImpl(
    private val contactsRepository: ContactsRepository
) : ClientsListInteractor {
    override suspend fun getClients(): List<Contact> = contactsRepository.getAll()
}