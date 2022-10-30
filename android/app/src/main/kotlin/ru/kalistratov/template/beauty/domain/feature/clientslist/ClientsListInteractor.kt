package ru.kalistratov.template.beauty.domain.feature.clientslist

import ru.kalistratov.template.beauty.domain.entity.Contact

interface ClientsListInteractor {
    suspend fun getClients(): List<Contact>
}