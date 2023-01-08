package ru.kalistratov.template.beauty.presentation.feature.client.picker

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.feature.client.picker.ClientPickerInteractor
import ru.kalistratov.template.beauty.domain.repository.ClientsRepository
import ru.kalistratov.template.beauty.domain.service.ClientPickerService
import javax.inject.Inject

class ClientPickerInteractorImpl @Inject constructor(
    private val clientsRepository: ClientsRepository,
    private val clientPickerService: ClientPickerService
) : ClientPickerInteractor {
    override suspend fun getAll() = clientsRepository.getAll()

    override suspend fun search(query: String) =
        clientsRepository.search(query.ifBlank { null })

    override suspend fun postClient(id: Id) =
        clientPickerService.postSelected(id)
}