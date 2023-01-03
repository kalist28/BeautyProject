package ru.kalistratov.template.beauty.domain.feature.client.edit

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.PhoneNumber
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerClient

interface EditClientInteractor {
    suspend fun get(id: Id): NetworkResult<ServerClient>
    suspend fun save(client: Client) : NetworkResult<ServerClient>
    suspend fun isNumberExist(number: PhoneNumber): Boolean
}