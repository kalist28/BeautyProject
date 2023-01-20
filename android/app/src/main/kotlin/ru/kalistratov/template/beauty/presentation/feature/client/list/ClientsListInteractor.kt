package ru.kalistratov.template.beauty.presentation.feature.client.list

import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.feature.clientslist.ClientsListInteractor
import ru.kalistratov.template.beauty.domain.repository.ClientsRepository
import java.util.*
import javax.inject.Inject

class ClientsListInteractorImpl @Inject constructor(
    private val clientsRepository: ClientsRepository
) : ClientsListInteractor {
    override suspend fun getClients(): List<Client> =
        clientsRepository.getAll().sortedWith { c1, c2 ->
            c1.fullname.lowercase(Locale.ROOT).compareTo(c2.fullname.lowercase(Locale.ROOT))
        }
}