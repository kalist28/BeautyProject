package ru.kalistratov.template.beauty.interfaces.server.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.interfaces.server.dto.RemoveRequest
import ru.kalistratov.template.beauty.interfaces.server.dto.RemoveResponse
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerClient
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerClientDataBundle

interface ApiClientsService {

    suspend fun create(bundle: ServerClientDataBundle): NetworkResult<ServerClient>
    suspend fun update(id: Id, bundle: ServerClientDataBundle): NetworkResult<ServerClient>

    suspend fun load(id: Id): NetworkResult<ServerClient>
    suspend fun loadAll(): NetworkResult<List<ServerClient>>

    suspend fun remove(request: RemoveRequest): NetworkResult<RemoveResponse>
}