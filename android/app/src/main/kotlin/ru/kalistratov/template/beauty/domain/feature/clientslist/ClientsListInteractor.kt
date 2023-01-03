package ru.kalistratov.template.beauty.domain.feature.clientslist

import ru.kalistratov.template.beauty.domain.entity.Client

interface ClientsListInteractor {
    suspend fun getClients(): List<Client>
}