package ru.kalistratov.template.beauty.infrastructure.repository

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
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

    override suspend fun getAll(fromCache: Boolean) = apiClientsService
        .loadAll().process(
            success = {
                map { it.toLocal() }
                    .also { cache = it }
            },
            error = { emptyList() }
        )

    override suspend fun search(query: String?): List<Client> =
        if (query == null) cache
        else cache.filter { searchCheck(it, query.toLowerCase(Locale.current)) }

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

    private fun searchCheck(client: Client, query: String) = client.run {
        name.search(query) or
                number.search(query) or
                (surname?.search(query) ?: false) or
                (patronymic?.search(query) ?: false) or
                (note?.search(query) ?: false)
    }

    private fun String.search(query: String) = toLowerCase(Locale.current).contains(query)

}