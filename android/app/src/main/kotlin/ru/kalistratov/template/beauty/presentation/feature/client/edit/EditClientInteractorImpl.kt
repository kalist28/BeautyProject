package ru.kalistratov.template.beauty.presentation.feature.client.edit

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.PhoneNumber
import ru.kalistratov.template.beauty.domain.entity.exist
import ru.kalistratov.template.beauty.domain.feature.client.edit.EditClientInteractor
import ru.kalistratov.template.beauty.domain.repository.ClientsRepository
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toBundle
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerClient
import ru.kalistratov.template.beauty.interfaces.server.service.ApiClientsService
import javax.inject.Inject

class EditClientInteractorImpl @Inject constructor(
    private val clientRepository: ClientsRepository,
    private val apiClientsService: ApiClientsService,
) : EditClientInteractor {
    override suspend fun get(id: Id): NetworkResult<ServerClient> =
        apiClientsService.load(id)

    override suspend fun save(client: Client) = when (client.id.exist()) {
        true -> apiClientsService.update(client.id, client.toBundle())
        else -> apiClientsService.create(client.toBundle())
    }

    override suspend fun isNumberExist(number: PhoneNumber) = clientRepository.contains(number)
}