package ru.kalistratov.template.beauty.infrastructure.repository

import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.PhoneNumber
import ru.kalistratov.template.beauty.domain.entity.exist
import ru.kalistratov.template.beauty.domain.repository.ClientsRepository
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.infrastructure.extensions.process
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toBundle
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.interfaces.server.dto.RemoveRequest
import ru.kalistratov.template.beauty.interfaces.server.service.ApiClientsService
import javax.inject.Inject

class ClientsRepositoryImpl @Inject constructor(
    private val apiClientsService: ApiClientsService
) : ClientsRepository {

    private var cache = listOf<Client>()

    override suspend fun add(client: Client) =
        when (client.id.exist()) {
            true -> apiClientsService.update(client.id, client.toBundle())
            else -> apiClientsService.create(client.toBundle())
        }.process(
            success = { },
            error = { }
        )

    override suspend fun get(id: Id) = apiClientsService
        .load(id).process(
            success = { toLocal() },
            error = { null }
        )

    override suspend fun getAll() = apiClientsService
        .loadAll().process(
            success = {
                map { it.toLocal() }
                    .also { cache = it }
            },
            error = { emptyList() }
        )
        .also { loge(it.firstOrNull()?.number) }

    override suspend fun contains(number: PhoneNumber): Boolean =
        getAll().find {
            loge("${it.number} == $number")
            it.number == number
        } != null

    override suspend fun remove(id: Id) {
        removeAll(listOf(id))
    }

    override suspend fun removeAll(ids: List<Id>) {
        apiClientsService.remove(RemoveRequest(ids))
    }

}